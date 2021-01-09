package com.example.navidoc.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.navidoc.R;
import com.example.navidoc.utils.ArrowDirections;
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

import java.util.Collection;
import java.util.Vector;

public class ARCameraActivity extends AppCompatActivity {
    private static final String TAG = ARCameraActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private boolean isArrowPlaced = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_a_r_camera);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);
        // necham to zakomentovane pre istotu, ak by sa nieco podrbalo :D
        // listener vytvara sipku po tuknuti na plane
//        arFragment.setOnTapArPlaneListener(
//                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
//                    Anchor anchor = hitResult.createAnchor();
//                    placeObject(arFragment, anchor, Uri.parse("model.sfb"));
//                });
    }

    private void onUpdate(FrameTime frameTime) {
        if (isArrowPlaced)
            return;

        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);

        for (Plane plane : planes) {
            if (plane.getTrackingState() == TrackingState.TRACKING) {
//                Anchor anchor = plane.createAnchor(plane.getCenterPose()); // sipka sa vytvori v strede plane
//                float[] currentTranslation = anchor.getPose().getTranslation(); // zistenie x,y,z pozicie sipky v priestore
                Anchor anchor = plane.createAnchor(Pose.makeTranslation(-1.5705881f, -0.8903943f, -1.7675675f));
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
}
