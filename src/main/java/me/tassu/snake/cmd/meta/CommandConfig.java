package me.tassu.snake.cmd.meta;

import com.google.inject.Singleton;
import lombok.Getter;
import me.tassu.easy.register.config.Config;
import me.tassu.snake.util.Chat;
import ninja.leaping.configurate.objectmapping.Setting;

@Getter
@Singleton
@Config.Name("commands")
public class CommandConfig extends Config<CommandConfig> {

    private String prefix = Chat.BLUE + Chat.BIG_BLOCK + " Command " + Chat.SMALL_ARROWS_RIGHT +
            Chat.GRAY + " ";

    @Setting("permission")
    private String permissionMessage = prefix + "This command requires permission level " + Chat.BLUE + "{0}" + Chat.GRAY + "!";

    @Setting
    private String usageMessage = prefix + "Usage: " + Chat.WHITE + "{0}";

    @Setting
    private String generalSuccess = prefix + "Affected " + Chat.WHITE + "{0}" + Chat.GRAY + " entities.";

}
