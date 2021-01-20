
package com.example.navidoc.utils;

import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.TransformableNode;

public class ArrowDirections extends Vector3{

    public enum VectorDirection{
        FRONT,
        FRONT_RIGHT,
        RIGHT,
        BACK_RIGHT,
        BACK,
        BACK_LEFT,
        LEFT,
        FRONT_LEFT

    }
    public int angle = 0;
    public ArrowDirections(VectorDirection vectorDirection){
        // Vektor = tvar sipky - nikdy sa nemeni menime iba uhol(smer sipky)
        set(new Vector3(0,1f,0));
        switch (vectorDirection){
            case FRONT:
                angle = 225;
                break;
            case RIGHT:
                angle = 135;
                break;
            case BACK:
                angle = 45;
                break;
            case LEFT:
                angle = 315;
                break;
            case FRONT_LEFT:
                angle = 270;
                break;
            case FRONT_RIGHT:
                angle = 180;
                break;
            case BACK_LEFT:
                angle = 360;
                break;
            case BACK_RIGHT:
                angle = 90;
                break;
        }
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
}
