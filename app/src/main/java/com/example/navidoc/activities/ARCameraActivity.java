package com.example.navidoc.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.navidoc.MainActivity;
import com.example.navidoc.R;
import com.example.navidoc.database.DAO;
import com.example.navidoc.database.DatabaseHelper;
import com.example.navidoc.database.Node;
import com.example.navidoc.services.BackgroundScanService;
import com.example.navidoc.utils.ArrowDirections;
import com.example.navidoc.utils.BeaconUtility;
import com.example.navidoc.utils.Dijkstra;
import com.example.navidoc.utils.Graph;
import com.example.navidoc.utils.MessageToast;
import com.example.navidoc.utils.NodeGraph;
import com.example.navidoc.utils.Path;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.kontakt.sdk.android.ble.device.BeaconDevice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ARCameraActivity extends AppCompatActivity
{
    private static final String TAG = ARCameraActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private boolean isArrowPlaced = false;
    private Path path;
    private List<BeaconDevice> beacons;
    private Intent serviceIntent;
    private BroadcastReceiver broadcastReceiver;
    private boolean btOn;
    private static final int POST_DELAY_TIME = 5000;
    private DAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHelper db2 = DatabaseHelper.getInstance(this);
        dao = db2.dao();

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_a_r_camera);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);

        // necham to zakomentovane pre istotu, ak by sa nieco podrbalo :D
        // listener vytvara sipku po tuknuti na plane
        /*arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    Anchor anchor = hitResult.createAnchor();
                    placeObject(arFragment, anchor, Uri.parse("model.sfb"));
                });*/


        if (getIntent().hasExtra("source") && getIntent().getStringExtra("source") != null
                && getIntent().hasExtra("destination") && getIntent().getStringExtra("destination") != null)
        {
            Node node = dao.getNodeByUniqueId(getIntent().getStringExtra("source"));
            path = getShortestPath(node, getIntent().getStringExtra("destination"));
        }

        this.serviceIntent = BackgroundScanService.createIntent(this);
        beacons = new ArrayList<>();
        btOn = true;
        setUpBroadcastReceiver();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            MessageToast.makeToast(this, R.string.bluetooth_is_off, Toast.LENGTH_SHORT).show();
        }
        else
        {
            startService(serviceIntent);
        }

        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    private void onUpdate(FrameTime frameTime) {
        if (isArrowPlaced)
            return;

        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);

        for (Plane plane : planes) {
            if (plane.getTrackingState() == TrackingState.TRACKING) {
                Anchor anchor = plane.createAnchor(plane.getCenterPose()); // sipka sa vytvori v strede plane
                //float[] currentTranslation = anchor.getPose().getTranslation(); // zistenie x,y,z pozicie sipky v priestore
                //Anchor customAnchor = plane.createAnchor(Pose.makeTranslation(-1.5705881f, -0.8903943f, -1.7675675f)); // sipka sa vytvori na danych x,y,z suradniciach
                placeObject(arFragment, anchor, Uri.parse("model.sfb"));
            }
        }
    }



    private void placeObject(ArFragment arFragment, Anchor anchor, Uri uri) {
        ModelRenderable.builder()
                .setSource(arFragment.getContext(), uri)
                .build()
                .thenAccept(modelRenderable -> addNodeToScene(arFragment, anchor, modelRenderable))
                .exceptionally(throwable -> {
                            Toast.makeText(arFragment.getContext(), "Error:" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                            return null;
                        }

                );

        isArrowPlaced = true;
    }

    private void addNodeToScene(ArFragment arFragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        // PRE VAS : TODO :
        // - nastavovanie vektoru sipky
        // - vektor(0,1,0) - tvar sipky
        // - 180 - smer sipky(uhol "nase ENUM")
        //node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 180));
        ArrowDirections arrowDirections = new ArrowDirections(ArrowDirections.VectorDirection.BACK);
        Vector3 vector3 = new Vector3(arrowDirections);
        node.setLocalRotation(Quaternion.axisAngle(vector3, arrowDirections.getAngle()));
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }


    @SuppressLint("ObsoleteSdkInt")
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        stopService(serviceIntent);
        super.onBackPressed();
    }

    @Override
    protected void onResume()
    {
        IntentFilter intentFilter = new IntentFilter(BackgroundScanService.DEVICE_DISCOVERED);
        registerReceiver(broadcastReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        try
        {
            unregisterReceiver(broadcastReceiver);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        super.onPause();
    }

    private void setUpBroadcastReceiver()
    {
        this.broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction()))
                {
                    onBluetoothAction(intent);

                    return;
                }

                BeaconDevice device = intent.getParcelableExtra(BackgroundScanService.EXTRA_DEVICE);
                if (device == null)
                {
                    return;
                }

                removeDevice(device.getAddress());

                if (intent.getAction().equals(BackgroundScanService.DEVICE_DISCOVERED))
                {
                    beacons.add(device);
                }

                handleNextHop();
            }

            private void onBluetoothAction(Intent intent)
            {
                if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_TURNING_OFF)
                {
                    stopService(serviceIntent);
                    btOn = false;
                }
                else
                {
                    startService(serviceIntent);
                    btOn = true;
                }
            }

            private void removeDevice(String address)
            {
                if (beacons.stream().anyMatch(beacon -> beacon.getAddress().equals(address)))
                {
                    BeaconDevice beaconDevice = beacons.stream().filter(beacon -> beacon.getAddress().equals(address)).collect(Collectors.toList()).get(0);
                    beacons.remove(beaconDevice);
                }
            }
        };
    }

    @SuppressLint("DefaultLocale")
    private BeaconDevice displayClosestBeacon()
    {
        if (!btOn)
        {
            return null;
        }

        if (beacons.size() != 0)
        {
            BeaconDevice closestDevice = beacons.get(0);
            for (BeaconDevice beacon : beacons)
            {
                double leftVal = closestDevice.getDistance();
                double rightVal = beacon.getDistance();
                if (leftVal > rightVal)
                {
                    closestDevice = beacon;
                }
            }

            return closestDevice;
        }

        return null;
    }

    private void handleNextHop()
    {
        BeaconDevice beacon = displayClosestBeacon();
        if (beacon != null && BeaconUtility.getUniqueId(beacon).equals(path.getCurrentHop().getDestinationUniqueId())
                && beacon.getDistance() < 0.7)
        {
            try
            {
                stopService(serviceIntent);
                unregisterReceiver(broadcastReceiver);
                if (path.isFinalHop())
                {
                    MessageToast.makeToast(this, R.string.u_have_reached_dest, Toast.LENGTH_LONG).show();

                    final Handler handler = new Handler();
                    handler.postDelayed(() -> startActivity(new Intent(this, MainActivity.class)), POST_DELAY_TIME);
                } else
                {
                    startService(serviceIntent);
                    IntentFilter intentFilter = new IntentFilter(BackgroundScanService.DEVICE_DISCOVERED);
                    registerReceiver(broadcastReceiver, intentFilter);
                    MessageToast.makeToast(this, R.string.new_hop, Toast.LENGTH_SHORT).show();
                    path.nextHop();
                    try
                    {
                        Thread.sleep(2000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private Path getShortestPath(Node currentLocation, String destination)
    {
        Dijkstra dijkstra = new Dijkstra(dao, currentLocation);
        Graph graph = dijkstra.calculateShortestPathFromSource();
        String destinationUniqueId = dao.getDoctorsByName(destination).get(0).getBeacon_unique_id();

        NodeGraph node = null;
        for (NodeGraph nodeGraph: graph.getNodes())
        {
            if (nodeGraph.getName().equals(destinationUniqueId))
            {
                node = nodeGraph;
            }
        }

        if (node != null)
        {

            int distance = 0;
            for (NodeGraph nodeGraph: node.getShortestPath())
            {
                distance += nodeGraph.getDistance();
            }

            NodeGraph tmp = new NodeGraph(destinationUniqueId);
            tmp.setDistance(node.getDistance() - distance);
            node.getShortestPath().add(tmp);

            return new Path(node);
        }
        else
        {
            MessageToast.makeToast(this, "something went wrong", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
