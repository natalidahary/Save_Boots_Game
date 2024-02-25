package com.example.savethemonkey.Utils;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

public class Signal {
    private static Signal mySignal = null;
    private Context context;


    private Signal(Context context){this.context = context;}

    public static void init(Context context){
        if (mySignal == null) {
            mySignal = new Signal(context);
        }
    }
    public static Signal getInstance(){return mySignal;}

    public void toast(String text){
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }

    public void vibrate() {
        //noinspection deprecation
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }


}
