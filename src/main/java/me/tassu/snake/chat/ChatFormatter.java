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

package me.tassu.snake.chat;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.experimental.var;
import lombok.val;
import me.tassu.easy.register.core.IRegistrable;
import me.tassu.snake.cmd.meta.Message;
import me.tassu.snake.user.UserRegistry;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.text.MessageFormat;

import static me.tassu.easy.util.StringUtil.color;

@Singleton
public class ChatFormatter implements IRegistrable {

    @Inject
    private UserRegistry registry;

    @Inject
    private ChatConfig config;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        val sender = registry.get(event.getPlayer());

        val variables = ImmutableMap.<String, String>builder()
                .put("USER", Message.getUserName(sender, config.getNameMode()))
                .put("LEVEL", String.valueOf(sender.getLevel()))
                .put("MESSAGE", "%2$s")
                .build();

        var message = config.getFormat();

        for (String key : variables.keySet()) {
            message = message.replace("{{" + key + "}}", variables.get(key));
        }

        message = message.replace(event.getPlayer().getName(), "%1$s");
        event.setFormat(message);
    }

}
