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

import lombok.Getter;
import lombok.val;
import me.tassu.snake.util.Chat;
import org.bukkit.ChatColor;

@Getter
public enum Rank {

    MEMBER(ChatColor.GRAY),
    MODERATOR(ChatColor.BLUE, ChatColor.AQUA),
    ADMIN(ChatColor.DARK_RED, ChatColor.RED),
    ;

    private int weight;
    private ChatColor primary;
    private ChatColor secondary;

    Rank(ChatColor color) {
        this(color, color);
    }

    Rank(ChatColor primary, ChatColor secondary) {
        this.weight = ordinal();
        this.primary = primary;
        this.secondary = secondary;
    }

    /**
     * Gets a rank by its name, defaults to {@link Rank#MEMBER} if none match
     */
    public static Rank byName(String name) {
        if (name == null) return Rank.MEMBER;

        for (val rank : values()) {
            if (rank.name().equals(name.toUpperCase())) {
                return rank;
            }
        }

        return Rank.MEMBER;
    }

    public String getTag() {
        if (this == MEMBER) return Chat.GRAY;
        return primary.toString() + "[" + name() + "] " + secondary.toString();
    }

}
