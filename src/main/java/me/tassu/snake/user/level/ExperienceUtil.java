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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import me.tassu.easy.register.core.IRegistrable;
import me.tassu.snake.user.User;
import me.tassu.snake.util.Chat;
import me.tassu.snake.util.LocaleConfig;

@Singleton
public class ExperienceUtil implements IRegistrable {

    @Inject
    private LocaleConfig locale;

    public void sendMessage(User user, long amount, String reason) {
        if (!user.isOnline()) return;
        val bukkit = user.getPlayer().orElseThrow(RuntimeException::new);

        bukkit.sendMessage(Chat.format(locale.getLocale().getExperienceGainMessage(), amount, reason));
    }

    public void sendLevelUpMessage(User user, int level) {
        if (!user.isOnline()) return;
        val bukkit = user.getPlayer().orElseThrow(RuntimeException::new);

        for (String msg : locale.getLocale().getLevelUpMessage()) {
            if (msg.contains("{LEVEL}")) {
                msg = msg.replace("{LEVEL}", String.valueOf(level));
            }

            bukkit.sendMessage(Chat.center(Chat.format(msg)));
        }
    }

}
