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

package me.tassu.snake.util;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import lombok.Getter;
import me.tassu.easy.register.config.Config;
import me.tassu.snake.cmd.meta.Message;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

import static me.tassu.snake.cmd.meta.Message.of;

@Getter
@Singleton
@Config.Name("locale")
public class LocaleConfig extends Config<LocaleConfig> {

    @Setting(comment = "This is the locale file of Snake.\n\n" +
            "Available settings for Message.actorMode are NAME_ONLY|COLORED|FULL.\n")
    private Locale locale = new Locale();

    public enum UserNameMode {
        NAME_ONLY,
        COLORED,
        FULL
    }

    @Getter
    @ConfigSerializable
    @SuppressWarnings("WeakerAccess")
    public static class Locale {

        // misc
        @Setting("permission")
        private String permissionMessage = Chat.RED + "Your permission level does not permit this.";

        // experience
        @Setting("experience-gain")
        private String experienceGainMessage = Chat.GOLD + Chat.PLUS + Chat.YELLOW + " Gained " + Chat.UNDERLINE + "{0} experience"
                + Chat.RESET + Chat.YELLOW + " ({1})";

        @Setting
        private List<String> levelUpMessage = Lists.newArrayList("",
                Chat.GOLD + Chat.BOLD + "Level up!",
                Chat.YELLOW + "You are now level {LEVEL}!",
                "");

        // misc command related

        @Setting("usage")
        private String usageMessage = Chat.YELLOW + "Usage: " + Chat.UNDERLINE + "{0}";

        @Setting(value = "prefixed-command", comment = "Error message when a prefixed command (\"plugin:command\") is used.")
        private String noPrefixing = Chat.RED + "Please do not use prefixed commands.";

        // informal commands

        @Setting("uptime")
        private String uptimeMessage = Chat.YELLOW + "The server has been up for " + Chat.WHITE + "{0}" + Chat.YELLOW + ".";

        // admin commands

        @Setting("gamemode-set")
        private Message gamemodeSetMessage = of(Chat.YELLOW, "Set game mode ''{0}'' to {1}" + Chat.YELLOW + ".");

        @Setting("rank-set")
        private Message setRankMessage = of(Chat.YELLOW, "Set rank ''{0}'' to {1}" + Chat.YELLOW + ".");

        @Setting("general-affected")
        private Message entityAffectSuccess = of(Chat.YELLOW, "Affected {0}" + Chat.YELLOW + ".");

    }

}
