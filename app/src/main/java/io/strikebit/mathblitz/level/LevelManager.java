package io.strikebit.mathblitz.level;

import io.strikebit.mathblitz.config.GameConfig;

public class LevelManager {
    public static int getNextLevel(int currentLevel, int score) {
        if (0 == score % 10) {
            if (GameConfig.DIFFICULTY_EASY == currentLevel) {
                return GameConfig.DIFFICULTY_ADEPT;
            } else if (GameConfig.DIFFICULTY_ADEPT == currentLevel) {
                return GameConfig.DIFFICULTY_HARD;
            } else if (GameConfig.DIFFICULTY_HARD == currentLevel) {
                return GameConfig.DIFFICULTY_VERY_HARD;
            } else {
                return GameConfig.DIFFICULTY_LEGENDARY;
            }
        }

        return GameConfig.DIFFICULTY_EASY;
    }
}
