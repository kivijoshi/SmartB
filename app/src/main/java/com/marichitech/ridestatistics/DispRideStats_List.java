package com.marichitech.ridestatistics;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class DispRideStats_List extends AppCompatActivity implements SensorEventListener
{
    int[] images = {R.drawable.motorcycle, R.drawable.motorcycle,R.drawable.motorcycle};

    String[] Angle_Name = {"Azimuth", "Pitch", "Roll"};

    String[] Angle_values = {"0.0", "0.0", "0.0"};

    ListView lView;

    ListAdapter lAdapter;
    // System sensor manager instance.
    private SensorManager mSensorManager;

    // Accelerometer and magnetometer sensors, as retrieved from the
    // sensor manager.
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;

    // TextViews to display current sensor values.
    private TextView mTextSensorAzimuth;
    private TextView mTextSensorPitch;
    private TextView mTextSensorRoll;

    // Very small values for the accelerometer (on all three axes) should
    // be interpreted as 0. This value is the amount of acceptable
    // non-zero drift.
    private static final float VALUE_DRIFT = 0.05f;

    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disp_ride_stats__list);

        lView = (ListView) findViewById(R.id.androidList);

        lAdapter = new ListAdapter(DispRideStats_List.this, Angle_Name, Angle_values, images);

        lView.setAdapter(lAdapter);
        // Lock the orientation to portrait (for now)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        // Get accelerometer and magnetometer sensors from the sensor manager.
        // The getDefaultSensor() method returns null if the sensor
        // is not available on the device.
        mSensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD);
    }

    /**
     * Listeners for the sensors are registered in this callback so that
     * they can be unregistered in onStop().
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Listeners for the sensors are registered in this callback and
        // can be unregistered in onStop().
        //
        // Check to ensure sensors are available before registering listeners.
        // Both listeners are registered with a "normal" amount of delay
        // (SENSOR_DELAY_NORMAL).
        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        int sensorType = sensorEvent.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = sensorEvent.values.clone();
                break;
            default:
                return;
        }
        float[] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix,
                null, mAccelerometerData, mMagnetometerData);

        float orientations[] = new float[3];


        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrix, orientations);
        }
        for (int i = 0; i < 3; i++) {
            orientations[i] = (float)(Math.toDegrees(orientations[i]));
            float temp = orientations[i];
            Angle_values[i] = Float.toString(temp);
        }


        lView = (ListView) findViewById(R.id.androidList);
        lAdapter = new ListAdapter(DispRideStats_List.this, Angle_Name, Angle_values, images);
        lView.setAdapter(lAdapter);

    }

    /**
     * Must be implemented to satisfy the SensorEventListener interface;
     * unused in this app.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
