package com.example.savethemonkey.Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorDetector {

    private Context context;
    private SensorManager mSensorManager;
    private Sensor sensor;
    long timeStamp = 0;
    private final int ResponseTime = 300;
    public interface CallBackView {
        void moveMonkeyBySensor(int index);
        void changeSpeedBySensor(int speed);
    }
    private CallBackView callBack_MonkeyView;
    public SensorDetector(Context context,CallBackView callBack_MonkeyView) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.callBack_MonkeyView = callBack_MonkeyView;
    }

    private SensorEventListener sensorEventListenerX = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            calculateStepX(x);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void startX() {
        mSensorManager.registerListener(sensorEventListenerX, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopX() {
        mSensorManager.unregisterListener(sensorEventListenerX);
    }

    private void calculateStepX(float x) {

        if (x > 3.0) {//left
            if (System.currentTimeMillis() - timeStamp > ResponseTime) {
                timeStamp = System.currentTimeMillis();
                callBack_MonkeyView.moveMonkeyBySensor(-1);
            }
        }
        if (x < -3.0) {//right
            if (System.currentTimeMillis() - timeStamp > ResponseTime) {
                timeStamp = System.currentTimeMillis();
                callBack_MonkeyView.moveMonkeyBySensor(1);
            }
        }
    }
}
