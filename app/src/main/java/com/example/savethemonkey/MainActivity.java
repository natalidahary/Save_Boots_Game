package com.example.savethemonkey;

import static com.example.savethemonkey.Logic.GameManager.OBJECTS_COLS;
import static com.example.savethemonkey.Logic.GameManager.OBJECTS_ROWS;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.savethemonkey.Audio.SoundManager;
import com.example.savethemonkey.DB.DataManager;
import com.example.savethemonkey.Logic.GameManager;
import com.example.savethemonkey.Sensors.SensorDetector;
import com.example.savethemonkey.Utils.SharedPreferances;
import com.example.savethemonkey.Utils.Signal;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.Arrays;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    // UI elements
    private ShapeableImageView[] main_boots_IMG;
    private ShapeableImageView[][] main_swiper_IMG;
    private MaterialTextView main_LBL_score;
    private ShapeableImageView[] main_IMG_lives;
    private ShapeableImageView[] broken_heart_IMG;
    private MaterialButton[] game_BTN_arrows;
    private RelativeLayout bar_background;

    // Game management components
    private GameManager gameManager;
    private SensorDetector sensorDetector;
    private SoundManager soundManager;
    private Handler gameHandler = new Handler();

    // Game settings and state
    private boolean isSensorOn = false;
    private String name = "";
    private double lat, lon;
    private int time = 0, delay= 1000;
    private boolean[] bootsDamaged = new boolean[OBJECTS_COLS];

    // Constants
    public static final String KEY_SENSOR = "KEY_SENSOR", KEY_DELAY = "KEY_DELAY",
            KEY_NAME = "KEY_NAME", KEY_LON = "KEY_LON", KEY_LAT = "KEY_LAT";
    private final String[] typeImage = new String[]{"ic_swiper", "ic_bag"};
    private final int fast_delay = 700, slow_delay = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        extractIntentData();
        initializeGameComponents();
        setupListeners();
    }


    private void extractIntentData() {
        Intent previousIntent = getIntent();
        isSensorOn = previousIntent.getBooleanExtra(KEY_SENSOR, false);
        boolean isFasterMode = previousIntent.getBooleanExtra(KEY_DELAY, false);
        name = previousIntent.getStringExtra(KEY_NAME);
        lon = previousIntent.getDoubleExtra(KEY_LON, 0);
        lat = previousIntent.getDoubleExtra(KEY_LAT, 0);
        setDelay(isFasterMode);
    }


    private void initializeGameComponents() {
        gameManager = new GameManager(main_IMG_lives.length, this, name);
        bootsDamaged = new boolean[gameManager.getObjectsCols()];
        soundManager = new SoundManager(this);
        sensorDetector = new SensorDetector(this, callBack_steps);
        setInitialGameViews();
        setupGame();
    }

    private void setInitialGameViews() {
        setInitialBootsVisibility();
        setInitialSwiperInvisible();
        setBrokenHeartsInvisible();
    }


    private Runnable updateObjectsRunnable = new Runnable() {
        @Override
        public void run() {
            // Check if the game is not over
            if (!gameManager.isGameOver()) {
                // Update the swiper
                checkCollsion();
                updateMatObjects();
                if (time % 2 != 0) {
                    randImage();
                }
                time++;

                // Schedule the next update
                gameHandler.postDelayed(this, delay);

                // Check if the game is over after updating
                if (gameManager.isGameOver()) {
                    runOnUiThread(() -> gameOver());
                }
            }
        }
    };

    private final Runnable scoreRunnable = new Runnable() {
        @Override
        public void run() {
            if (!gameManager.isGameOver()) {
                gameManager.increaseScore(10); // Increase score
                updateScoreDisplay();
                gameHandler.postDelayed(this, 5000); // Schedule next update
            }
        }
    };


    private SensorDetector.CallBackView callBack_steps = new SensorDetector.CallBackView() {
        @Override
        public void moveMonkeyBySensor(int index){
            main_boots_IMG[gameManager.getCurrentIndexBoots()].setVisibility(View.INVISIBLE);
            gameManager.moveDirectionBoots(index);
            main_boots_IMG[gameManager.getCurrentIndexBoots()].setVisibility(View.VISIBLE);
        }
        @Override
        public void changeSpeedBySensor(int speed) {
            delay = speed;
        }
    };


    private void setDelay(boolean isFasterMode) {
        if(isFasterMode)
            delay = fast_delay;
        else
            delay = slow_delay;

    }

    private void saveRecord() {
        gameManager.save(lon,lat);
    }

    private void setupListeners() {
        game_BTN_arrows[0].setOnClickListener(v -> handleArrowClick(-1));
        game_BTN_arrows[1].setOnClickListener(v -> handleArrowClick(1));
        if (isSensorOn) {
            sensorDetector.startX();
            hideGameButtons();
        } else {
            showGameButtons();
        }
    }

    private void showGameButtons() {
        game_BTN_arrows[0].setVisibility(View.VISIBLE);
        game_BTN_arrows[1].setVisibility(View.VISIBLE);
    }

    private void hideGameButtons() {
        game_BTN_arrows[0].setVisibility(View.INVISIBLE);
        game_BTN_arrows[1].setVisibility(View.INVISIBLE);
        bar_background.setVisibility(View.INVISIBLE);
    }

    private void handleArrowClick(int direction) {
        moveBoots(direction);
        soundManager.playClickSound();
    }

    private void setupGame() {
        gameHandler.postDelayed(updateObjectsRunnable, delay);
        gameHandler.postDelayed(scoreRunnable, 5000);
    }

    private void initializeViews() {
        main_boots_IMG = new ShapeableImageView[] {
                findViewById(R.id.main_IMG_leftBoots),
                findViewById(R.id.main_IMG_leftMiddleBoots),
                findViewById(R.id.main_IMG_middleBoots),
                findViewById(R.id.main_IMG_rightMiddleBoots),
                findViewById(R.id.main_IMG_rightBoots)
        };
        main_IMG_lives = new ShapeableImageView[]{
                findViewById(R.id.main_live1_IMG),
                findViewById(R.id.main_live2_IMG),
                findViewById(R.id.main_live3_IMG)
        };
        main_swiper_IMG = new ShapeableImageView[][] {
                {
                        findViewById(R.id.main_IMG_swiper1),
                        findViewById(R.id.main_IMG_swiper2),
                        findViewById(R.id.main_IMG_swiper3),
                        findViewById(R.id.main_IMG_swiper4),
                        findViewById(R.id.main_IMG_swiper5)
                },
                {
                        findViewById(R.id.main_IMG_swiper6),
                        findViewById(R.id.main_IMG_swiper7),
                        findViewById(R.id.main_IMG_swiper8),
                        findViewById(R.id.main_IMG_swiper9),
                        findViewById(R.id.main_IMG_swiper10)
                },
                {
                        findViewById(R.id.main_IMG_swiper11),
                        findViewById(R.id.main_IMG_swiper12),
                        findViewById(R.id.main_IMG_swiper13),
                        findViewById(R.id.main_IMG_swiper14),
                        findViewById(R.id.main_IMG_swiper15)

                },
                {
                        findViewById(R.id.main_IMG_swiper16),
                        findViewById(R.id.main_IMG_swiper17),
                        findViewById(R.id.main_IMG_swiper18),
                        findViewById(R.id.main_IMG_swiper19),
                        findViewById(R.id.main_IMG_swiper20)
                },
                {
                        findViewById(R.id.main_IMG_swiper21),
                        findViewById(R.id.main_IMG_swiper22),
                        findViewById(R.id.main_IMG_swiper23),
                        findViewById(R.id.main_IMG_swiper24),
                        findViewById(R.id.main_IMG_swiper25)
                },
                {
                        findViewById(R.id.main_IMG_swiper26),
                        findViewById(R.id.main_IMG_swiper27),
                        findViewById(R.id.main_IMG_swiper28),
                        findViewById(R.id.main_IMG_swiper29),
                        findViewById(R.id.main_IMG_swiper30)
                },
                {
                        findViewById(R.id.main_IMG_swiper31),
                        findViewById(R.id.main_IMG_swiper32),
                        findViewById(R.id.main_IMG_swiper33),
                        findViewById(R.id.main_IMG_swiper34),
                        findViewById(R.id.main_IMG_swiper35)
                },
                {
                        findViewById(R.id.main_IMG_swiper36),
                        findViewById(R.id.main_IMG_swiper37),
                        findViewById(R.id.main_IMG_swiper38),
                        findViewById(R.id.main_IMG_swiper39),
                        findViewById(R.id.main_IMG_swiper40)
                },
                {
                        findViewById(R.id.main_IMG_swiper41),
                        findViewById(R.id.main_IMG_swiper42),
                        findViewById(R.id.main_IMG_swiper43),
                        findViewById(R.id.main_IMG_swiper44),
                        findViewById(R.id.main_IMG_swiper45)
                }
        };
        broken_heart_IMG = new ShapeableImageView[]{
                findViewById(R.id.broken_heart1_IMG),
                findViewById(R.id.broken_heart2_IMG),
                findViewById(R.id.broken_heart3_IMG),
                findViewById(R.id.broken_heart4_IMG),
                findViewById(R.id.broken_heart5_IMG)
        };

        game_BTN_arrows = new MaterialButton[]{
                findViewById(R.id.main_BTN_left),
                findViewById(R.id.main_BTN_right)
        };

        bar_background = findViewById(R.id.bar_background);
    }

    private void setInitialBootsVisibility() {
        for (ShapeableImageView boots : main_boots_IMG) {
            boots.setVisibility(View.INVISIBLE);
        }
        main_boots_IMG[gameManager.getCurrentIndexBoots()].setVisibility(View.VISIBLE);
    }


    private void moveBoots(int direction) {
        gameManager.moveDirectionBoots(direction);
        updateBootsVisibility();
    }

    private void updateBootsVisibility() {
        int currentMonkeyPosition = gameManager.getCurrentIndexBoots();
        for (ShapeableImageView boots : main_boots_IMG) {
            boots.setVisibility(View.INVISIBLE);
        }
        main_boots_IMG[currentMonkeyPosition].setVisibility(View.VISIBLE);
    }

    private void updateBrokenHeartsVisibility() {
        for (int i = 0; i < bootsDamaged.length; i++) {
            broken_heart_IMG[i].setVisibility(bootsDamaged[i] ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void setInitialSwiperInvisible() {
        for (int row = 0; row < OBJECTS_ROWS; row++) {
            for (int col = 0; col < OBJECTS_COLS; col++) {
                main_swiper_IMG[row][col].setVisibility(View.INVISIBLE);
            }
        }
    }


    private void updateMatObjects() {
        for (int i = OBJECTS_ROWS - 1; i >= 0; i--) {
            for (int j = OBJECTS_COLS - 1; j >= 0; j--) {
                if(main_swiper_IMG[i][j].getVisibility() == View.VISIBLE && i == OBJECTS_ROWS -1) {
                    main_swiper_IMG[i][j].setVisibility(View.INVISIBLE);

                }
                if(main_swiper_IMG[i][j].getVisibility() == View.VISIBLE){
                    main_swiper_IMG[i][j].setVisibility(View.INVISIBLE);
                    int type = gameManager.getMain_type_matrix(i,j);
                    gameManager.setMainTypeMatrix(i,j,-1);
                    setImage(i+1,j,type);
                    gameManager.setMainTypeMatrix(i+1,j,type);
                    main_swiper_IMG[i+1][j].setVisibility(View.VISIBLE);
                }
            }

        }

    }

    private void checkCollsion() {
        for (int i = OBJECTS_ROWS - 1; i > 0; i--) {
            for (int j = OBJECTS_COLS - 1; j >= 0; j--) {
                if (i == OBJECTS_ROWS - 1 && main_swiper_IMG[i][j].getVisibility() == View.VISIBLE && j == gameManager.getCurrentIndexBoots()) {
                    checkType(gameManager.getMain_type_matrix(i,j));
                }

            }
        }
    }

    private void checkType(int type) {
        if (type == 0) {
            // Handle the collision with a Swiper
            handleSwiperCollision();
        } else { // Collision with Bag
            // Handle the collision with a Bag
            handleBagCollision();
        }
        Signal.getInstance().vibrate();
    }

    private void handleSwiperCollision() {
        if (!gameManager.isGameOver()) {
            soundManager.playSwiperCollisionSound();
            gameManager.loseLife();
            updateLivesDisplay();
            runOnUiThread(() -> {
                int currentIndexBoots = gameManager.getCurrentIndexBoots();
                if (currentIndexBoots >= 0 && currentIndexBoots < bootsDamaged.length) {
                    broken_heart_IMG[currentIndexBoots].setVisibility(View.VISIBLE);

                    new Handler().postDelayed(() -> {
                  // Update the damage after the heart is hidden
                        setBrokenHeartsInvisible();
                    }, 1000);
                }
                Signal.getInstance().vibrate();
                Signal.getInstance().toast("Chatfani, You must not kidnap!");
            });
        }
    }


    private void handleBagCollision() {
        if (!gameManager.isGameOver()) {
            soundManager.playBagTouchSound();
            gameManager.increaseScore(20); // Increase the score for collecting a bag
            updateScoreDisplay();
        }
    }



    private void randImage() {
        int col = gameManager.randomViewImage();//0
        int type = gameManager.randTypeImage();//1
        setImage(0,col,type);
        main_swiper_IMG[0][col].setVisibility(View.VISIBLE);
    }


    private void setImage(int row, int col, int type) {
        // Check if row and col are within valid bounds
        if (row >= 0 && row < main_swiper_IMG.length && col >= 0 && col < main_swiper_IMG[0].length) {
            int imageID = getResources().getIdentifier(typeImage[type], "drawable", getPackageName());
            gameManager.setMainTypeMatrix(row, col, type); // 0 == rock 1 == coins
            main_swiper_IMG[row][col].setImageResource(imageID);
        }
    }



    private void updateLivesDisplay() {
        for (int i = 0; i < main_IMG_lives.length; i++) {
            main_IMG_lives[i].setVisibility(i < gameManager.getLife() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void setBrokenHeartsInvisible() {
        for (int i = 0; i < broken_heart_IMG.length; i++) {
            broken_heart_IMG[i].setVisibility(View.INVISIBLE);
        }
    }

    private void updateScoreDisplay() {
        main_LBL_score = findViewById(R.id.main_LBL_score);
        main_LBL_score.setText(String.format(Locale.getDefault(), "%03d", gameManager.getScore()));
    }

    private void gameOver() {
        gameHandler.removeCallbacks(scoreRunnable);
        // Create a custom dialog
        Dialog gameOverDialog = new Dialog(this);
        gameOverDialog.setContentView(R.layout.dialog_game_over);

        // Make dialog modal and non-cancelable
        gameOverDialog.setCancelable(false);
        gameOverDialog.setCanceledOnTouchOutside(false);

        Button dialog_BTN_playAgain = gameOverDialog.findViewById(R.id.dialog_BTN_playAgain);
        Button dialog_BTN_backMenu = gameOverDialog.findViewById(R.id.dialog_BTN_backMenu);

        // Set a click listener for the play again button
        dialog_BTN_playAgain.setOnClickListener(view -> {
            soundManager.playClickSound();
            resetGame();
            gameOverDialog.dismiss();
        });

        // Set a click listener for the back to menu button
        dialog_BTN_backMenu.setOnClickListener(view -> {
            soundManager.playClickSound();
            gameOverDialog.dismiss();
            Intent intent = new Intent(MainActivity.this,MenuActivity.class);
            startActivity(intent);
            finish();
        });

        saveRecord();

        // Show the dialog
        gameOverDialog.show();
    }

    private void resetGame() {
        // Reset the game state
        //gameManager = new GameManager(main_IMG_lives.length);
        gameManager = new GameManager(main_IMG_lives.length,this,name);
        updateLivesDisplay();

        gameHandler.removeCallbacks(updateObjectsRunnable);

        updateBootsVisibility();

        setInitialSwiperInvisible();

        gameHandler.post(updateObjectsRunnable);

        // Reset damage state
        Arrays.fill(bootsDamaged, false);
        updateBrokenHeartsVisibility();

        // Reset the score to 0
        gameManager.resetScore();
        updateScoreDisplay();

        // Start score runnable when the game is reset
        gameHandler.postDelayed(scoreRunnable, 5000);

        //DataManager.getInstance().savePlayerScore(playerName, gameManager.getScore());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundManager.releaseResources();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restart or resume the game loop
        gameHandler.post(updateObjectsRunnable);
        // Resume score updates, if necessary
        gameHandler.postDelayed(scoreRunnable, 5000);
        if(isSensorOn) {
            sensorDetector.startX();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove callbacks to pause the game loop and score updates
        gameHandler.removeCallbacksAndMessages(updateObjectsRunnable);
        gameHandler.removeCallbacksAndMessages(scoreRunnable);
        if(isSensorOn) {
            sensorDetector.stopX();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameHandler.removeCallbacksAndMessages(null);
        if(isSensorOn) {
            sensorDetector.stopX();
        }
        soundManager.releaseResources();
    }

}


