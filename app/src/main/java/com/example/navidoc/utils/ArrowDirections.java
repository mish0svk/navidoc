
package com.example.navidoc.utils;

import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.TransformableNode;

public class ArrowDirections extends Vector3{

    public enum VectorDirection{
        FRONT,
        BACK,
        RIGHT,
        LEFT,
        FRONTLEFT,
        FRONTRIGHT,
        BACKLEFT,
        BACKRIGHT
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
            case FRONTLEFT:
                angle = 270;
                break;
            case FRONTRIGHT:
                angle = 180;
                break;
            case BACKLEFT:
                angle = 360;
                break;
            case BACKRIGHT:
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
