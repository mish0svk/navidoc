package com.example.navidoc.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.navidoc.database.CardinalDirection;

public class BackgroundOrientationService implements SensorEventListener
{
    private SensorManager sensorManager;
    private final float[] accelerometerValues = new float[3];
    private final float[] magnetometerValues = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    public BackgroundOrientationService(Context context)
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        final float alpha = 0.97f;
        synchronized (this)
        {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            {
                accelerometerValues[0] = alpha * accelerometerValues[0] + (1 - alpha) * event.values[0];
                accelerometerValues[1] = alpha * accelerometerValues[1] + (1 - alpha) * event.values[1];
                accelerometerValues[2] = alpha * accelerometerValues[2] + (1 - alpha) * event.values[2];
            }
            else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            {
                magnetometerValues[0] = alpha * magnetometerValues[0] + (1 - alpha) * event.values[0];
                magnetometerValues[1] = alpha * magnetometerValues[1] + (1 - alpha) * event.values[1];
                magnetometerValues[2] = alpha * magnetometerValues[2] + (1 - alpha) * event.values[2];
            }

            if (accelerometerValues != null && magnetometerValues != null)
            {
                updateOrientationAngles();
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    public void resume()
    {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null)
        {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }

        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null)
        {
            sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void pause()
    {
        sensorManager.unregisterListener(this);
    }

    private void updateOrientationAngles()
    {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magnetometerValues);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
    }

    public CardinalDirection getOrientation()
    {
        float rotation = (((float) Math.toDegrees(orientationAngles[0])) + 360) % 360 ;


        if (rotation >= 337.5 && rotation <= 22.5)
        {
            return CardinalDirection.NORTH;
        }
        else if (rotation > 22.5 && rotation < 67.5)
        {
            return CardinalDirection.NORTH_EAST;
        }
        else if (rotation >= 67.5 && rotation <= 112.5)
        {
            return CardinalDirection.EAST;
        }
        else if (rotation > 112.5 && rotation < 157.5)
        {
            return CardinalDirection.SOUTH_EAST;
        }
        else if (rotation >= 157.5 && rotation <= 202.5)
        {
            return CardinalDirection.SOUTH;
        }
        else if (rotation > 202.5 && rotation < 247.5)
        {
            return CardinalDirection.SOUTH_WEST;
        }
        else if (rotation >= 247.5 && rotation <= 292.5)
        {
            return CardinalDirection.WEST;
        }
        else
        {
            return CardinalDirection.NORTH_WEST;
        }
    }
}
