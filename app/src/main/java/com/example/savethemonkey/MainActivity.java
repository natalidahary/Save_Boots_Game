package com.example.savethemonkey;

import static com.example.savethemonkey.Logic.GameManager.OBJECTS_COLS;
import static com.example.savethemonkey.Logic.GameManager.OBJECTS_ROWS;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.savethemonkey.Audio.SoundManager;
import com.example.savethemonkey.Data.DataManager;
import com.example.savethemonkey.Logic.GameManager;
import com.google.android.material.button.MaterialButton;
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
    private MaterialButton leftButton, rightButton;
    private ShapeableImageView[] broken_heart_IMG;

    // Sound management
    private SoundManager soundManager;

    // Game management
    private GameManager gameManager;
    private int time = 0;
    private String playerName;

    // Constants
    private final String[] typeImage = new String[]{"ic_swiper", "ic_bag"};
    private Handler gameHandler = new Handler();
    private boolean[] bootsDamaged = new boolean[OBJECTS_COLS];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeGameManager();
        setInitialBootsVisibility();
        setInitialSwiperInvisible();
        setBrokenHeartsInvisible();
        initializeSoundManager();

        setupGame();

        setupButtons();
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
                gameHandler.postDelayed(this, GameManager.FALLING_SPEED);

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

    private void setupGame() {
        playerName = DataManager.getInstance().getPlayerName();
        gameHandler.postDelayed(scoreRunnable, 5000);
        gameHandler.post(updateObjectsRunnable);
    }

    private void setupButtons() {
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveBoots(-1); // Move left
                soundManager.playClickSound();
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveBoots(1); // Move right
                soundManager.playClickSound();
            }
        });
    }


    private void initializeViews() {
        main_boots_IMG = new ShapeableImageView[] {
                findViewById(R.id.main_IMG_leftBoots),
                findViewById(R.id.main_IMG_middleBoots),
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
                        findViewById(R.id.main_IMG_swiper3)
                },
                {
                        findViewById(R.id.main_IMG_swiper4),
                        findViewById(R.id.main_IMG_swiper5),
                        findViewById(R.id.main_IMG_swiper6)
                },
                {
                        findViewById(R.id.main_IMG_swiper7),
                        findViewById(R.id.main_IMG_swiper8),
                        findViewById(R.id.main_IMG_swiper9)
                },
                {
                        findViewById(R.id.main_IMG_swiper10),
                        findViewById(R.id.main_IMG_swiper11),
                        findViewById(R.id.main_IMG_swiper12)
                },
                {
                        findViewById(R.id.main_IMG_swiper13),
                        findViewById(R.id.main_IMG_swiper14),
                        findViewById(R.id.main_IMG_swiper15)
                }
        };
        broken_heart_IMG = new ShapeableImageView[]{
                findViewById(R.id.broken_heart1_IMG),
                findViewById(R.id.broken_heart2_IMG),
                findViewById(R.id.broken_heart3_IMG)
        };

        leftButton = findViewById(R.id.main_BTN_left);
        rightButton = findViewById(R.id.main_BTN_right);
    }

    private void initializeGameManager() {
        gameManager = new GameManager(main_IMG_lives.length);
        Arrays.fill(bootsDamaged, false);
    }

    private void initializeSoundManager() {
        soundManager = new SoundManager(this);
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
                Toast.makeText(MainActivity.this, "Chatfani, You must not kidnap!", Toast.LENGTH_SHORT).show();
                vibrate();
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
            resetGame();
            gameOverDialog.dismiss();
        });

        // Set a click listener for the back to menu button
        dialog_BTN_backMenu.setOnClickListener(view -> {
            gameOverDialog.dismiss();
            finish();
        });

        // Show the dialog
        gameOverDialog.show();
    }

    private void resetGame() {
        // Reset the game state
        gameManager = new GameManager(main_IMG_lives.length);
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

        DataManager.getInstance().savePlayerScore(playerName, gameManager.getScore());
    }

    private void vibrate() {
        //noinspection deprecation
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        }
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove callbacks to pause the game loop and score updates
        gameHandler.removeCallbacksAndMessages(updateObjectsRunnable);
        gameHandler.removeCallbacksAndMessages(scoreRunnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameHandler.removeCallbacksAndMessages(null);
    }


}


