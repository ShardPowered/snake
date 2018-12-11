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

import lombok.*;
import me.tassu.snake.util.Chat;
import org.bson.Document;
import org.bukkit.ChatColor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Rank {

    private final String name;

    @Getter(AccessLevel.NONE)
    private String nickname;

    private int weight;
    private ChatColor primary;
    private ChatColor secondary;
    private boolean isDefault = false;
    private TablistMode tablistMode = TablistMode.SHOW_COLOR;

    private Rank(String name) {
        this.name = name;
    }

    public String getTag() {
        if (isDefault()) return Chat.GRAY;
        return primary.toString() + "[" + getNickname() + "] " + secondary.toString();
    }

    public String getNickname() {
        if (nickname == null) return getName();
        return nickname;
    }

    public Document toDocument() {
        return new Document()
                .append("_id", this.getName())
                .append("nickname", this.getNickname())
                .append("weight", this.getWeight())
                .append("primary", this.getPrimary().getChar())
                .append("secondary", this.getSecondary().getChar())
                .append("tabmode", this.getTablistMode().name())
                .append("default", this.isDefault());
    }

    public void updateFrom(Document document) {
        this.weight = document.getInteger("weight");

        val nickname = document.getString("nickname");
        if (!nickname.equals(this.getName())) {
            this.nickname = nickname;
        }

        this.primary = ChatColor.getByChar(document.getString("primary"));
        this.secondary = ChatColor.getByChar(document.getString("secondary"));
        this.isDefault = document.getBoolean("default");

        try {
            this.tablistMode = TablistMode.valueOf(document.getString("tabmode"));
        } catch (IllegalArgumentException ex) {
            this.tablistMode = TablistMode.SHOW_COLOR;
        }
        
    }

    public static Rank fromDocument(Document document) {
        val rank = new Rank(document.getString("_id"));
        rank.updateFrom(document);
        return rank;
    }

    public enum TablistMode {
        SHOW_PREFIX,
        SHOW_COLOR,
        NONE
    }

}
