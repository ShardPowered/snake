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

package me.tassu.snake.cmd.meta;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;
import me.tassu.snake.user.User;
import me.tassu.snake.user.UserRegistry;
import me.tassu.snake.util.LocaleConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@NoArgsConstructor
@AllArgsConstructor
@ConfigSerializable
public class Message {

    public static Message of(String prefix, String message) {
        if (message.length() < 2) {
            throw new IllegalArgumentException("too short message");
        }

        return new Message(LocaleConfig.UserNameMode.NAME_ONLY, prefix + message, prefix + "{ACTOR} "
                + Character.toLowerCase(message.charAt(0)) + message.substring(1));
    }

    @Setting
    private LocaleConfig.UserNameMode actorMode = LocaleConfig.UserNameMode.NAME_ONLY;

    @Setting
    private String self, others;

    public String getSelf() {
        return self;
    }

    public String getOthers(CommandSender actor, UserRegistry userRegistry) {
        if (actor instanceof Player && actorMode != LocaleConfig.UserNameMode.NAME_ONLY) {
            val player = userRegistry.get((Player) actor);
            return others.replace("{ACTOR}", getUserName(player, actorMode));
        }

        return others.replace("{ACTOR}", actor.getName());
    }

    public static String getUserName(User user, LocaleConfig.UserNameMode mode) {
        switch (mode) {
            case FULL:
                return user.getPrefixedName();
            case COLORED:
                return user.getColoredName();
            case NAME_ONLY:
                return user.getUserName();
        }

        throw new IllegalArgumentException("mode == null");
    }

}
