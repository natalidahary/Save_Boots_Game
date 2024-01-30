package com.example.savethemonkey;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.savethemonkey.Audio.SoundManager;
import com.example.savethemonkey.Data.DataManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;


public class MenuActivity extends AppCompatActivity {

    private MaterialButton start_BTN;
    private TextInputEditText menu_LBL_inputName;
    private MaterialButton globalScore_BTN;
    private SwitchMaterial menu_SW_faster;
    private SwitchMaterial menu_SW_sensor;
    private SoundManager soundManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Initialize the button and EditText
        start_BTN = findViewById(R.id.start_BTN);
        menu_LBL_inputName = findViewById(R.id.menu_LBL_inputName);
        menu_LBL_inputName.setText("");
        soundManager = new SoundManager(this);

        start_BTN.setOnClickListener(view -> {
            soundManager.playClickSound();
            String name = menu_LBL_inputName.getText().toString().trim();
            if(name.isEmpty()) {
                Toast.makeText(MenuActivity.this, "Please enter your name to start the game.", Toast.LENGTH_SHORT).show();
            } else {
                DataManager.getInstance().setPlayerName(name);
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
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