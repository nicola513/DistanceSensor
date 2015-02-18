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
    public double accelerometer_vibrateThreshold;
    public Sensor gyroscope;
    public double gyroscope_vibrateThreshold;
    public Vibrator v;
    public float background_acceleration_x, background_acceleration_y, background_acceleration_z;
    public float background_rotation_x, background_rotation_y, background_rotation_z;
    public float rotation_x, rotation_y, rotation_z;
    public float acceleration_x, acceleration_y, acceleration_z;
    public float displacement_x = 0, displacement_y = 0, displacement_z = 0;
    public float displacement_rotation_x = 0, displacement_rotation_y = 0, displacement_rotation_z = 0;
    public TextView textView_x, textView_y, textView_z;
    public TextView textView_background_x, textView_background_y, textView_background_z;
    public TextView textView_background_rotate_x, textView_background_rotate_y, textView_background_rotate_z;
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
        textView_background_rotate_x = (TextView) mainActivity.findViewById(R.id.value_background_rotate_x);
        textView_background_rotate_y = (TextView) mainActivity.findViewById(R.id.value_background_rotate_y);
        textView_background_rotate_z = (TextView) mainActivity.findViewById(R.id.value_background_rotate_z);

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
            //accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sensorManager.registerListener(mainActivity, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            accelerometer_vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fail! we dont have an accelerometer!
        }
        // android API demo on http://developer.android.com/
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            // success! we have an gyroscope
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            //accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sensorManager.registerListener(mainActivity, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
            gyroscope_vibrateThreshold = gyroscope.getMaximumRange() / 2;
        } else {
            // fail! we dont have an gyroscope!
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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (configurating) {
                background_acceleration_x = event.values[0];
                background_acceleration_y = event.values[1];
                background_acceleration_z = event.values[2];
            } else {
                acceleration_x = event.values[0] - background_acceleration_x;
                acceleration_y = event.values[1] - background_acceleration_y;
                acceleration_z = event.values[2] - background_acceleration_z;
                displacement_x += acceleration_x;
                displacement_y += acceleration_y;
                displacement_z += acceleration_z;
            }
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (configurating) {
                background_rotation_x= event.values[0];
                background_rotation_y= event.values[1];
                background_rotation_z= event.values[2];
            } else {
                rotation_x = event.values[0] - background_rotation_x;
                rotation_y = event.values[1] - background_rotation_y;
                rotation_z = event.values[2] - background_rotation_z;
                displacement_rotation_x+=rotation_x;
                displacement_rotation_y+=rotation_y;
                displacement_rotation_z+=rotation_z;
            }
        }

        updateView();
    }

    public void updateView() {
        if (configurating) {
            textView_background_x.setText(String.valueOf(background_acceleration_x));
            textView_background_y.setText(String.valueOf(background_acceleration_y));
            textView_background_z.setText(String.valueOf(background_acceleration_z));
            textView_background_rotate_x.setText(String.valueOf(background_rotation_x));
            textView_background_rotate_y.setText(String.valueOf(background_rotation_y));
            textView_background_rotate_z.setText(String.valueOf(background_rotation_z));

            textView_background_x.setGravity(Gravity.CENTER);
            textView_background_y.setGravity(Gravity.CENTER);
            textView_background_z.setGravity(Gravity.CENTER);
            textView_background_rotate_x.setGravity(Gravity.CENTER);
            textView_background_rotate_y.setGravity(Gravity.CENTER);
            textView_background_rotate_z.setGravity(Gravity.CENTER);
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
