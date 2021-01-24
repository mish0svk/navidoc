package com.example.navidoc.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.navidoc.R;
import com.example.navidoc.adapters.Place;
import com.example.navidoc.database.CardinalDirection;
import com.example.navidoc.database.Converter;
import com.example.navidoc.database.DAO;
import com.example.navidoc.database.DatabaseHelper;
import com.example.navidoc.database.Department;
import com.example.navidoc.database.Doctor;
import com.example.navidoc.database.Node;
import com.example.navidoc.services.BackgroundOrientationService;
import com.example.navidoc.services.BackgroundScanService;
import com.example.navidoc.utils.AbstractDialog;
import com.example.navidoc.utils.ArrowDirections;
import com.example.navidoc.utils.BeaconUtility;
import com.example.navidoc.utils.Dijkstra;
import com.example.navidoc.utils.Graph;
import com.example.navidoc.utils.MessageToast;
import com.example.navidoc.utils.NodeGraph;
import com.example.navidoc.utils.Path;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Sun;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
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
    private DAO dao;
    private BackgroundOrientationService orientationService;
    private double lastBeaconDistance;
    private Doctor doctor;
    private CardinalDirection initialDirection;

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

        orientationService = new BackgroundOrientationService(this);
        try
        {
            orientationService.resume();
        }
        catch (Exception e)
        {
            Log.d(TAG, "Error");
        }

        if (getIntent().hasExtra("source") && getIntent().getStringExtra("source") != null
                && getIntent().hasExtra("destination") && getIntent().getStringExtra("destination") != null)
        {
            Node node = dao.getNodeByUniqueId(getIntent().getStringExtra("source"));
            path = getShortestPath(node, getIntent().getStringExtra("destination"));
        }

        this.serviceIntent = BackgroundScanService.createIntent(this);
        beacons = new ArrayList<>();
        btOn = true;
        lastBeaconDistance = 0.0f;
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

    private ArrowDirections.VectorDirection chooseArrowDirection()
    {
        CardinalDirection currentDirection = orientationService.getOrientation();
        if (initialDirection == null) {
            initialDirection = currentDirection;
        }
        CardinalDirection destDirection = path.getCurrentHop().getCardinalDirection();

        int degree = 0;
        int idx = Converter.fromDirectionToDegree(initialDirection);
        while (idx != Converter.fromDirectionToDegree(destDirection))
        {
            degree += 45;
            idx += 45;
            if (idx >= 360)
            {
                idx = 0;
            }
        }

        return Converter.fromDegreeToArrowDirection(degree);
    }

    private synchronized void onUpdate(FrameTime frameTime) {
        if (isArrowPlaced)
            return;

        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);

        try {
            for (Plane plane : planes) {
                if (plane.getTrackingState() == TrackingState.TRACKING) {
                    placeObject(arFragment, Uri.parse("model.sfb"));
                }
            }
        }
        catch (Exception ex) {
            isArrowPlaced = false;
        }
    }

    private void removeChildren() {
        List<com.google.ar.sceneform.Node> children = new ArrayList<>(arFragment.getArSceneView().getScene().getChildren());
        for (com.google.ar.sceneform.Node node : children) {
            if (node instanceof AnchorNode) {
                if (((AnchorNode) node).getAnchor() != null) {
                    ((AnchorNode) node).getAnchor().detach();
                }
            }
            if (!(node instanceof Camera) && !(node instanceof Sun)) {
                node.setParent(null);
            }
        }
    }

    private synchronized void placeObject(ArFragment arFragment, Uri uri)
    {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if(isArrowPlaced)
                return;

            removeChildren();
            ModelRenderable.builder()
                    .setSource(arFragment.getContext(), uri)
                    .build()
                    .thenAccept(modelRenderable -> addNodeToScene(arFragment, modelRenderable))
                    .exceptionally(throwable -> {
                                Toast.makeText(arFragment.getContext(), "Error:" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                                return null;
                            }
                    );
            isArrowPlaced = true;
        }, 1000);
    }

    private void addNodeToScene(ArFragment arFragment, Renderable renderable) {
        Vector3 cameraPos = arFragment.getArSceneView().getScene().getCamera().getWorldPosition();
        Vector3 cameraForward = arFragment.getArSceneView().getScene().getCamera().getForward();
        Vector3 position = Vector3.add(cameraPos, cameraForward.scaled(0.8f));

        Pose pose = Pose.makeTranslation(position.x, position.y, position.z);
        Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(pose);
        AnchorNode anchorNode = new AnchorNode(anchor);
        com.google.ar.sceneform.Node node = new com.google.ar.sceneform.Node();
        // PRE VAS:
        // - nastavovanie vektoru sipky
        // - vektor(0,1,0) - tvar sipky
        // - 180 - smer sipky(uhol "nase ENUM")
        //node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 180));
        ArrowDirections arrowDirections = new ArrowDirections(chooseArrowDirection());

        Vector3 vector3 = new Vector3(arrowDirections);
        renderable.getMaterial().setFloat3("baseColorTint", new Color(android.graphics.Color.rgb(255,151, 23)));
        anchorNode.setParent(arFragment.getArSceneView().getScene());
        node.setLocalRotation(Quaternion.axisAngle(vector3, arrowDirections.getAngle()));
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
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
        orientationService.resume();
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
        orientationService.pause();
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

                if (BeaconUtility.getUniqueId(device.getAddress()).equals(path.getCurrentHop().getDestinationUniqueId()))
                {
                    if (Math.abs(device.getDistance() - lastBeaconDistance) > 0.57)
                    {
                        lastBeaconDistance = device.getDistance();
                        isArrowPlaced = false;
                    }
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

    private synchronized void handleNextHop()
    {
        BeaconDevice beacon = displayClosestBeacon();
        if (beacon != null && BeaconUtility.getUniqueId(beacon).equals(path.getCurrentHop().getDestinationUniqueId())
                && beacon.getDistance() < 0.75)
        {
            try
            {
                stopService(serviceIntent);
                unregisterReceiver(broadcastReceiver);
                if (path.isFinalHop())
                {
                    this.doctor = dao.getDoctorByBeaconUniqueId(path.getCurrentHop().getDestinationUniqueId());
                    String navigateTo = getResources().getString(R.string.u_have_reached_dest) + ":\n" + doctor.getAmbulance_name()
                            + " " + "(" + doctor.getName() + ")";

                    AbstractDialog dialog = AbstractDialog.getInstance();
                    dialog.newBuilderInstance(this).setTitle(R.string.app_name).setMessage(navigateTo)
                            .setOKbutton().getBuilder()
                            .setNeutralButton("DETAIL", this::onClick).create().show();
                } else
                {
                    startService(serviceIntent);
                    IntentFilter intentFilter = new IntentFilter(BackgroundScanService.DEVICE_DISCOVERED);
                    registerReceiver(broadcastReceiver, intentFilter);
                    path.nextHop();
                    MessageToast.makeToast(this, "Continue walking", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void onClick(DialogInterface dialogInterface, int i) {
        Department doctorDepartment = dao.getDepartmentByID(doctor.getDepartment_id());

        Place place = new Place(doctor.getAmbulance_name(),doctorDepartment.getName(),doctorDepartment.getFloor(),
                doctor.getName(),doctor.getStart_time(),doctor.getEnd_time(),doctor.getPhone_number(),
                doctor.getWeb_site(),doctor.getIsFavorite());

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("selected_place", place);
        intent.putExtra("activity", "Places");
        startActivity(intent);
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

            return new Path(node, dao);
        }
        else
        {
            MessageToast.makeToast(this, "something went wrong", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
