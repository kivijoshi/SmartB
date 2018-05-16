package com.marichitech.ridestatistics;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

public class DispRideStats_List extends AppCompatActivity implements SensorEventListener
{
    int[] images = {R.drawable.motorcycle, R.drawable.motorcycle,R.drawable.motorcycle};

    String[] Angle_Name = {"Azimuth", "Pitch", "Roll"};

    String[] Angle_values = {"0.0", "0.0", "0.0"};
    float[] Angles = {0f, 0f, 0f};
    ListView lView;

    ILineDataSet[] datasets = new ILineDataSet[3];

    ListAdapter lAdapter;
    // System sensor manager instance.
    private SensorManager mSensorManager;

    // Accelerometer and magnetometer sensors, as retrieved from the
    // sensor manager.
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;

    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;


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

    private boolean b_init = false;
    private float Azimuth_max = 0;
    private float Pitch_max = 0;
    private float Roll_max = 0;

    private float[] init_values = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disp_ride_stats__list);

        lView = (ListView) findViewById(R.id.androidList);

        for (int i = 0; i < 3; i++) {
            Utils.init(getResources());
            LineDataSet set_temp = new LineDataSet(null,"Dynamic Data");
            set_temp.setAxisDependency(YAxis.AxisDependency.LEFT);
            set_temp.setLineWidth(3);
            set_temp.setColor(Color.MAGENTA);
            set_temp.setHighlightEnabled(false);
            set_temp.setDrawValues(false);
            set_temp.setDrawCircles(false);
            set_temp.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set_temp.setCubicIntensity(0.2f);
            datasets[i] = set_temp;
        }
        //lAdapter = new ListAdapter(DispRideStats_List.this, Angle_Name, Angle_values,Angles,datasets, images);

        //lView.setAdapter(lAdapter);

        //startPlot();
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



    private LineDataSet CreateSet(){
        LineDataSet set = new LineDataSet(null,"Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3);
        set.setColor(Color.MAGENTA);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
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

            if(b_init == false)
            {

                SensorManager.getOrientation(rotationMatrix, orientations);
                for (int i = 0; i < 3; i++) {
                    init_values[i] = (float) (Math.toDegrees(orientations[i]));
                }
                if( init_values[0]!=0 && init_values[1]!= 0 && init_values[2]!=0 ){
                    b_init = true;
                }

            }
            else {
                SensorManager.getOrientation(rotationMatrix, orientations);
                for (int i = 0; i < 3; i++) {
                    orientations[i] = (float) (Math.toDegrees(orientations[i]));
                    float temp = orientations[i] - init_values[i];
                    float temp_1 = Float.parseFloat(Angle_values[i]);
                    Angles[i] = temp;
                    datasets[i].addEntry(new Entry(datasets[i].getEntryCount(),temp));

                    if(Math.abs(temp_1) < Math.abs(temp)) {
                        Angle_values[i] = Float.toString(temp);
                    }
                    else{

                    }
                }
                if(plotData == true) {
                    lView = (ListView) findViewById(R.id.androidList);
                    lAdapter = new ListAdapter(DispRideStats_List.this, Angle_Name, Angle_values, Angles, datasets, images);
                    lView.setAdapter(lAdapter);
                    plotData = true;
                }
            }
        }





    }

    private void startPlot(){

        if(thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(100);
                    }
                    catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Must be implemented to satisfy the SensorEventListener interface;
     * unused in this app.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /*@Override
    public void OnDestroy(){
        mSensorManager.unregisterListener(DispRideStats_List.this);
        thread.interrupt();
        super.onDestroy();
    }*/
}
