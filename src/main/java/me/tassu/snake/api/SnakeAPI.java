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

package me.tassu.snake.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import me.tassu.snake.achievement.AchievementRegistry;
import me.tassu.snake.db.MongoManager;
import me.tassu.snake.user.UserParser;
import me.tassu.snake.user.UserRegistry;
import me.tassu.snake.user.level.LevelUtil;
import me.tassu.snake.user.rank.RankRegistry;
import me.tassu.snake.user.rank.RankUtil;
import me.tassu.snake.util.LocaleConfig;

@Getter
@Singleton
public class SnakeAPI {

    @Getter
    private static SnakeAPI instance;

    public SnakeAPI() {
        if (instance != null) {
            throw new IllegalStateException();
        }

        instance = this;
    }

    @Inject
    private UserRegistry userRegistry;

    @Inject
    private UserParser userParser;

    @Inject
    private RankUtil rankUtil;

    @Inject
    private RankRegistry rankRegistry;

    @Inject
    private LevelUtil levelUtil;

    @Inject
    private AchievementRegistry achievementRegistry;

    @Inject
    private MongoManager mongoManager;

    @Inject
    private LocaleConfig localeConfig;

    public LocaleConfig.Locale getLocale() {
        return localeConfig.getLocale();
    }

}
