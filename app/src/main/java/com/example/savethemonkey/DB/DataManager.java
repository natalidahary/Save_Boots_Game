package com.example.savethemonkey.DB;

import java.util.ArrayList;

public class DataManager {
    private ArrayList<Record> scoreResults;
    private final int LIMIT_TOP10 = 10;


    public DataManager() {
        this.scoreResults = new ArrayList<>();
    }

    public ArrayList<Record> getScoreResults() {
        scoreResults.sort((r1, r2) -> r2.getScore() - r1.getScore());
        if (scoreResults.size() == LIMIT_TOP10) {
            scoreResults.remove(LIMIT_TOP10 - 1);
        }

        return scoreResults;
    }

    public DataManager setScoreResults(ArrayList<Record> scoreResults) {
        this.scoreResults = scoreResults;
        return this;
    }
}
