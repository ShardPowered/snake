package me.tassu.snake.util;

import com.google.inject.Inject;
import me.tassu.easy.api.message.SimpleMessageProvider;
import me.tassu.snake.cmd.meta.CommandConfig;
import org.bukkit.command.CommandSender;

public class Messager extends SimpleMessageProvider {

    @Inject private CommandConfig commandConfig;

    @Override
    public void sendMissingPermissionMessage(CommandSender sender, String permission) {
        sender.sendMessage(Chat.format(commandConfig.getPermissionMessage(), permission));
    }
}
