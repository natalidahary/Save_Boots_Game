package com.example.savethemonkey.Logic;

import java.util.Random;

public class GameManager {

    private int life;
    private int score;
    Random rand = new Random();
    public static final int OBJECTS_ROWS = 5;
    public static final int OBJECTS_COLS = 3;
    private int currentIndexBoots = OBJECTS_COLS / 2;
    private int[][] main_type_matrix;
    public static final int FALLING_SPEED = 1000;


    public GameManager() {
        this.life = 3;
        this.score = 0;
        initializeObjectsPositions();
    }

    public GameManager(int life) {
        this.life = life;
        this.score = 0;
        initializeObjectsPositions();
    }

    public void loseLife() {
        if (life > 0) {
            life--;
        }
    }
    public boolean isGameOver() {
        return life <= 0;
    }

    public int getLife() {
        return life;
    }
    public int getScore() {
        return score;
    }

    public void increaseScore(int points) {
        score += points;
    }

    public void resetScore() {
        score = 0;
    }

    public int getCurrentIndexBoots() {
        return currentIndexBoots;
    }

    public void moveDirectionBoots(int direction) {
        currentIndexBoots += direction;
        if (currentIndexBoots < 0) {
            currentIndexBoots = 0;
        } else if (currentIndexBoots >= OBJECTS_COLS) {
            currentIndexBoots = OBJECTS_COLS - 1;
        }
    }

    private void initializeObjectsPositions() {
        main_type_matrix = new int[OBJECTS_ROWS][OBJECTS_COLS];
        for (int row = 0; row < OBJECTS_ROWS; row++) {
            for (int col = 0; col < OBJECTS_COLS; col++) {
                main_type_matrix[row][col] = -1;
            }
        }
    }

    public void setMainTypeMatrix(int row, int col, int type) {// put the type of the coin
        main_type_matrix[row][col] = type;
    }


    public int getMain_type_matrix(int row, int col) {
        return main_type_matrix[row][col];
    }


    public int randomViewImage() {
        return rand.nextInt(OBJECTS_COLS);
    }


    public int randTypeImage() {
        int res = rand.nextInt(5);
        if( res > 3){
            return 1;//bag
        }
        return 0;//swiper

    }
}
