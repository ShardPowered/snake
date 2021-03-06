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

package me.tassu.snake.achievement;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import me.tassu.easy.register.core.IRegistrable;
import me.tassu.snake.api.event.SyncUserJoinedEvent;
import me.tassu.snake.user.UserRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@Singleton
public class AchievementListener implements IRegistrable {

    @Inject
    private AchievementRegistry registry;

    @Inject
    private UserRegistry userRegistry;

    private Achievement firstJoinAchievement, chatAchievement;

    @Override
    public void register() {
        firstJoinAchievement = registry.byId(StandardAchievements.FIRST_JOIN).orElse(null);
        chatAchievement = registry.byId(StandardAchievements.CHAT).orElse(null);
    }

    @EventHandler
    public void onPlayerJoin(SyncUserJoinedEvent event) {
        if (firstJoinAchievement == null) {
            return;
        }

        event.getUser().addAchievement(firstJoinAchievement);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (chatAchievement == null) {
            return;
        }

        val user = userRegistry.get(event.getPlayer());
        user.addAchievement(chatAchievement);
    }
}
