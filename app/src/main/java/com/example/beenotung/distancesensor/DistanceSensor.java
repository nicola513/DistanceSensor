package com.example.beenotung.distancesensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by beenotung on 2/18/15.
 */
public class DistanceSensor {
    private final MainActivity mainActivity;

    public SensorManager sensorManager;
    public Sensor accelerometer;
    public double vibrateThreshold;
    public Vibrator v;
    public float background_acceleration_x, background_acceleration_y, background_acceleration_z;
    public float acceleration_x, acceleration_y, acceleration_z;
    public float displacement_x = 0, displacement_y = 0, displacement_z = 0;
    public TextView textView_x, textView_y, textView_z;
    public TextView textView_background_x, textView_background_y, textView_background_z;
    public boolean started = false;
    public boolean configurating = false;

    public DistanceSensor(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        //set up view
        textView_x = (TextView) mainActivity.findViewById(R.id.value_x);
        textView_y = (TextView) mainActivity.findViewById(R.id.value_y);
        textView_z = (TextView) mainActivity.findViewById(R.id.value_z);
        textView_background_x = (TextView) mainActivity.findViewById(R.id.value_background_x);
        textView_background_y = (TextView) mainActivity.findViewById(R.id.value_background_y);
        textView_background_z = (TextView) mainActivity.findViewById(R.id.value_background_z);

        //set up view event
        ((Button) mainActivity.findViewById(R.id.btn_start)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                started = true;
                configurating = false;
            }
        });
        ((Button) mainActivity.findViewById(R.id.btn_stop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                started = false;
            }
        });
        ((Button) mainActivity.findViewById(R.id.btn_config)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                config();
            }
        });
        ((Button) mainActivity.findViewById(R.id.btn_reset)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        //set up sensor
        //reference http://examples.javacodegeeks.com/android/core/hardware/sensor/android-accelerometer-example/
        sensorManager = (SensorManager) mainActivity.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(mainActivity, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! we dont have an accelerometer!
        }
        //initialize vibration
        v = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void config() {
        configurating = true;
        started = true;
    }

    private void reset() {
        displacement_x = displacement_y = displacement_z = 0;
        updateView();
    }

    public void onSensorChanged(SensorEvent event) {
        if (!started) return;
        if (configurating) {
            background_acceleration_x=event.values[0];
            background_acceleration_y=event.values[1];
            background_acceleration_z=event.values[2];
        } else {
            acceleration_x = event.values[0];
            acceleration_y = event.values[1];
            acceleration_z = event.values[2];
            displacement_x += acceleration_x;
            displacement_y += acceleration_y;
            displacement_z += acceleration_z;
        }
        updateView();
    }

    public void updateView() {
        if (configurating) {
            textView_background_x.setText(String.valueOf(background_acceleration_x));
            textView_background_y.setText(String.valueOf(background_acceleration_y));
            textView_background_z.setText(String.valueOf(background_acceleration_z));

            textView_background_x.setGravity(Gravity.CENTER);
            textView_background_y.setGravity(Gravity.CENTER);
            textView_background_z.setGravity(Gravity.CENTER);
        } else {
            textView_x.setText(String.valueOf(displacement_x));
            textView_y.setText(String.valueOf(displacement_y));
            textView_z.setText(String.valueOf(displacement_z));

            textView_x.setGravity(Gravity.CENTER);
            textView_y.setGravity(Gravity.CENTER);
            textView_z.setGravity(Gravity.CENTER);
        }
    }
}
