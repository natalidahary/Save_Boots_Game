package com.example.savethemonkey.Data;

import java.util.HashMap;
import java.util.Map;

public class DataManager {
    private static DataManager instance;
    private String playerName;
    private final Map<String, Integer> playerScores;

    private DataManager() {
        playerScores = new HashMap<>();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void savePlayerScore(String playerName, int score) {
        playerScores.put(playerName, score);
    }

    public int getPlayerScore(String playerName) {
        return playerScores.getOrDefault(playerName, 0);
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public String getPlayerName() {
        return playerName;
    }
}
