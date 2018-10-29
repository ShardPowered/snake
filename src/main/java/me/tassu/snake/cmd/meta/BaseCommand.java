package me.tassu.snake.cmd.meta;

import com.google.inject.Inject;
import lombok.val;
import me.tassu.easy.register.command.Command;
import me.tassu.easy.register.command.error.CommandException;
import me.tassu.easy.register.command.error.MissingPermissionException;
import me.tassu.snake.user.UserRegistry;
import me.tassu.snake.user.rank.Rank;
import me.tassu.snake.util.Chat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class BaseCommand extends Command {

    @Inject private UserRegistry registry;

    private Rank rank;

    public BaseCommand(String name, Rank rank) {
        super(name);
        this.rank = rank;
    }

    @Override
    protected void check(CommandSender sender, String label, List<String> args) throws CommandException {
        super.check(sender, label, args);

        if (sender instanceof Player) {
            val user = registry.get(((Player) sender).getUniqueId());
            if (user == null || user.getRank().getWeight() < rank.getWeight()) {
                throw new MissingPermissionException(rank.name());
            }
        }
    }

    protected void sendMessage(CommandSender sender, String message, Object... replacements) {
        sender.sendMessage(Chat.format(message, replacements));
    }

}
