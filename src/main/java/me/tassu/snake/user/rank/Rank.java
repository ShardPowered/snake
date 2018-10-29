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
