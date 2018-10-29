/*
 * MIT License
 *
 * Copyright (c) 2018 Tassu <hello@tassu.me>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
