package me.tassu.snake.util;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;

import java.text.MessageFormat;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class Chat {

    public static final String AQUA = ChatColor.AQUA.toString();
    public static final String CYAN = ChatColor.DARK_AQUA.toString();
    public static final String BLACK = ChatColor.BLACK.toString();
    public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
    public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
    public static final String DARK_RED = ChatColor.DARK_RED.toString();
    public static final String PURPLE = ChatColor.DARK_PURPLE.toString();
    public static final String GOLD = ChatColor.GOLD.toString();
    public static final String GRAY = ChatColor.GRAY.toString();
    public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
    public static final String BLUE = ChatColor.BLUE.toString();
    public static final String GREEN = ChatColor.GREEN.toString();
    public static final String RED = ChatColor.RED.toString();
    public static final String PINK = ChatColor.LIGHT_PURPLE.toString();
    public static final String YELLOW = ChatColor.YELLOW.toString();
    public static final String WHITE = ChatColor.WHITE.toString();
    public static final String MAGIC = ChatColor.MAGIC.toString();
    public static final String BOLD = ChatColor.BOLD.toString();
    public static final String STRIKE = ChatColor.STRIKETHROUGH.toString();
    public static final String UNDERLINE = ChatColor.UNDERLINE.toString();
    public static final String ITALIC = ChatColor.ITALIC.toString();
    public static final String RESET = ChatColor.RESET.toString();

    public static final List<String> lights = Lists.newArrayList(GOLD, GREEN, AQUA, BLUE, RED, PINK, YELLOW);
    public static final List<String> darks = Lists.newArrayList(DARK_BLUE, DARK_GREEN, CYAN, DARK_RED, PURPLE);
    public static final List<String> shades = Lists.newArrayList(BLACK, GRAY, DARK_GRAY, WHITE);

    public static final String DARK_STAR = "★";
    public static final String WHITE_STAR = "☆";
    public static final String CIRCLE_BLANK_STAR = "✪";
    public static final String BIG_BLOCK = "█";
    public static final String SMALL_BLOCK = "▌";
    public static final String SMALL_DOT = "•";
    public static final String LARGE_DOT = "●";
    public static final String HEART = "♥";
    public static final String SMALL_ARROWS_RIGHT = "»";
    public static final String SMALL_ARROWS_LEFT = "«";
    public static final String BIG_HORIZONTAL_LINE = "▍";
    public static final String SMALL_HORIZONTAL_LINE = "▏";

    public static String format(String input, Object... replacements) {
        return ChatColor.translateAlternateColorCodes('&', MessageFormat.format(input, replacements));
    }
}
