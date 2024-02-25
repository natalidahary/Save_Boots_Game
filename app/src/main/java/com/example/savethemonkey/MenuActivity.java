package com.example.savethemonkey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;

import com.example.savethemonkey.Audio.SoundManager;
import com.example.savethemonkey.Utils.Signal;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import im.delight.android.location.SimpleLocation;


public class MenuActivity extends AppCompatActivity {

    private MaterialButton startGame_BTN;
    private TextInputEditText menu_LBL_inputName;
    private MaterialButton top10_BTN;
    private SwitchMaterial menu_SW_faster;
    private SwitchMaterial menu_SW_sensor;
    private SoundManager soundManager;

    private boolean isSensorOn = false;
    private boolean isFasterOn = false;

    private double lat = 0.0;
    private double lon = 0.0;
    public SimpleLocation location;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        soundManager = new SoundManager(this);
        location = new SimpleLocation(this);

        // Initialize the button and EditText
        findViews();
        requestLocationPermission(location);

        startGame_BTN.setOnClickListener(view -> {
            soundManager.playClickSound();
            if(menu_LBL_inputName.getText().length() == 0) {
                Signal.getInstance().toast("Please enter your name to start the game.");
            } else {
                //DataManager.getInstance().setPlayerName(name);
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.KEY_SENSOR, isSensorOn);
                intent.putExtra(MainActivity.KEY_NAME, menu_LBL_inputName.getText().toString());
                intent.putExtra(MainActivity.KEY_DELAY, isFasterOn);
                intent.putExtra(MainActivity.KEY_LON,lon);
                intent.putExtra(MainActivity.KEY_LAT,lat);
                startActivity(intent);
                finish();
            }
        });

        menu_SW_sensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean sensorOn) {
                soundManager.playClickSound();
                isSensorOn = sensorOn;
            }
        });
        menu_SW_faster.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean fasterModeOn) {
                soundManager.playClickSound();
                isFasterOn = fasterModeOn;
            }
        });

        top10_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MenuActivity.this, ScoreMapActivity.class);
                startActivity(intent);
            }
        });

    }

    private void requestLocationPermission(SimpleLocation location) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);

        }
        putLatLon(location);
    }

    private void putLatLon(SimpleLocation location){
        location.beginUpdates();
        this.lat = location.getLatitude();
        this.lon = location.getLongitude();
    }


    private void findViews() {
        startGame_BTN = findViewById(R.id.startGame_BTN);
        menu_LBL_inputName = findViewById(R.id.menu_LBL_inputName);
        top10_BTN = findViewById(R.id.top10_BTN);
        menu_SW_faster = findViewById(R.id.menu_SW_faster);
        menu_SW_sensor = findViewById(R.id.menu_SW_sensor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetPlayerNameInput();
    }

    private void resetPlayerNameInput() {
        TextInputEditText playerNameInput = findViewById(R.id.menu_LBL_inputName);
        playerNameInput.setText("");
    }

}