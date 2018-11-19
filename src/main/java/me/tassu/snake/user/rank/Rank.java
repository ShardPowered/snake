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

package me.tassu.snake.user.rank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.tassu.snake.util.Chat;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.ChatColor;

@Getter
@NoArgsConstructor
@ConfigSerializable
public class Rank {

    @Setting
    private String name;

    @Setting
    @Getter(AccessLevel.NONE)
    private String nickname;

    @Setting
    private int weight;

    @Setting
    private ChatColor primary;

    @Setting
    private ChatColor secondary;

    @Setting
    private boolean isDefault = false;

    @Setting(comment = "SHOW_PREFIX | SHOW_COLOR | NONE")
    private TablistMode tablistMode = TablistMode.SHOW_COLOR;

    public Rank(String name, int weight, ChatColor primary, ChatColor secondary) {
        this.name = name;
        this.weight = weight;
        this.primary = primary;
        this.secondary = secondary;
    }

    public String getTag() {
        if (isDefault()) return Chat.GRAY;
        return primary.toString() + "[" + getNickname() + "] " + secondary.toString();
    }

    public String getNickname() {
        if (nickname == null) return getName();
        return nickname;
    }

    Rank setDefault() {
        this.isDefault = true;
        return this;
    }

    Rank setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public enum TablistMode {
        SHOW_PREFIX,
        SHOW_COLOR,
        NONE
    }

}
