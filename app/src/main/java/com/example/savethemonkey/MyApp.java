package com.example.savethemonkey;

import android.app.Application;

import com.example.savethemonkey.Utils.SharedPreferances;
import com.example.savethemonkey.Utils.Signal;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Signal.init(this);
        SharedPreferances.init(this);
    }

}
