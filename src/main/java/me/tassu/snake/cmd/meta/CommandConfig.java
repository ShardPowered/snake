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

import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import lombok.Getter;
import me.tassu.easy.register.config.Config;
import me.tassu.snake.util.Chat;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Map;

@Getter
@Singleton
@Config.Name("commands")
public class CommandConfig extends Config<CommandConfig> {

    @Setting
    private Locale locale = new Locale();

    @Getter
    @ConfigSerializable
    @SuppressWarnings("WeakerAccess")
    public static class Locale {
        private String prefix = Chat.prefix("Command");

        @Setting("permission")
        private String permissionMessage = Chat.prefix(Chat.RED, "Command") + "Missing permissions.";

        @Setting("usage")
        private String usageMessage = prefix + "Usage: " + Chat.WHITE + "{0}";

        @Setting("rank-set")
        private String setRankMessage = prefix + "Set rank of " + Chat.BLUE + "{0}" + Chat.GRAY + " to " + Chat.WHITE + "{1}" + Chat.GRAY + ".";

        @Setting("general-affected")
        private String entityAffectSuccess = prefix + "Affected " + Chat.WHITE + "{0}" + Chat.GRAY + ".";

        @Setting(value = "prefixed-command", comment = "Error message when a prefixed command (\"plugin:command\") is used.")
        private String noPrefixing = Chat.prefix(Chat.RED, "Command") + "Please do not use prefixed commands.";

        @Setting("uptime")
        private String uptimeMessage = prefix + "The server has been up for " + Chat.WHITE + "{0}" + Chat.GRAY + ".";
    }

    @Setting("permissions")
    private Map<String, String> requiredRanks = ImmutableMap.<String, String>builder()
            .put("help", "MEMBER")
            .put("uptime", "MEMBER")
            .put("setrank", "ADMIN")
            .put("gamemode", "ADMIN")
            .put("heal", "MODERATOR")
            .put("feed", "MODERATOR")
            .build();

}
