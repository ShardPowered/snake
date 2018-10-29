package me.tassu.snake.user.level;

import com.google.common.collect.Maps;
import lombok.experimental.var;
import lombok.val;

import java.util.Map;

public class LevelUtil {

    private static Map<Integer, Long> levelMap;

    public static long getRequiredExperience(int level) {
        if (level < 0) return Long.MIN_VALUE;
        return levelMap.getOrDefault(level, Long.MAX_VALUE);
    }

    public static int getLevelForExp(long exp) {
        return levelMap.entrySet().stream()
                .filter(it -> it.getValue() <= exp)
                .reduce((first, second) -> second)
                .map(Map.Entry::getKey)
                .orElse(0);
    }

    public static float getProgress(long expIn, int level) {
        val prev = levelMap.getOrDefault(level, 0L);
        val total = levelMap.getOrDefault(level + 1, Long.MAX_VALUE) - prev;
        val exp = expIn - prev;

        return ((float) exp) / ((float) total);
    }

    static {
        levelMap = Maps.newHashMap();
        levelMap.put(1, 0L);

        var toAdd = 100L;
        var xp = 0L;

        for (var level = 2; level < 101; level++) {
            xp += toAdd;

            levelMap.put(level, xp);

            var multiplier = level > 10 ? 1.06 : 1.6;
            toAdd *= multiplier;
        }
    }

}
