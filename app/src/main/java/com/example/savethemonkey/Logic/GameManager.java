package com.example.savethemonkey.Logic;

import android.content.Context;
import com.example.savethemonkey.DB.DataManager;
import com.example.savethemonkey.DB.Record;
import com.example.savethemonkey.Utils.SharedPreferances;
import com.google.gson.Gson;

import java.util.Random;

public class GameManager {

    private int life;
    private int score = 0;
    Random rand = new Random();
    public static final int OBJECTS_ROWS = 9;
    public static final int OBJECTS_COLS = 5;
    private int currentIndexBoots = OBJECTS_COLS / 2;
    private int[][] main_type_matrix;
    private String name;
    private Context context;
    private final String RECORD = "records";



    public GameManager() {
        this.life = 3;
        this.score = 0;
        initializeObjectsPositions();
    }

    public GameManager(int life,Context context,String name){
        this.life = life;
        this.score = 0;
        this.context = context;
        this.name = name;
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
        if (res > 3) {
            return 1;//bag
        }
        return 0;//swiper
    }

    public void save(double lon , double lat) {
        DataManager myDB;
        String json = SharedPreferances.getInstance().getStrSP(RECORD,"");
        myDB = new Gson().fromJson(json,DataManager.class);
        if(myDB == null){
            myDB = new DataManager();
        }
        Record rec = createRecord(lon,lat);
        myDB.getScoreResults().add(rec);
        SharedPreferances.getInstance().putString(RECORD,new Gson().toJson(myDB));
    }

    private Record createRecord(double lon , double lat) {

        return new Record().setName(name).setScore(score).setLat(lat).setLon(lon);
    }


    public int getObjectsCols() {
        return  OBJECTS_COLS;
    }
}
