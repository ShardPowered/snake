package me.tassu.snake.cmd;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import me.tassu.easy.register.command.Aliases;
import me.tassu.easy.register.command.error.CommandException;
import me.tassu.snake.cmd.meta.BaseCommand;
import me.tassu.snake.cmd.meta.CommandConfig;
import me.tassu.snake.user.UserParser;
import me.tassu.snake.user.rank.Rank;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Singleton
@Aliases({"heal", "hp", "life"})
public class HealCommand extends BaseCommand {

    @Inject
    private UserParser parser;

    @Inject
    private CommandConfig config;

    public HealCommand() {
        super("heal", Rank.MODERATOR);
        this.setUsage("/heal <users>");
        this.setDescription("Used to heal a player.");
    }

    @Override
    public void run(CommandSender sender, String label, List<String> args) throws CommandException {
        if (args.isEmpty()) {
            sendMessage(sender, config.getUsageMessage(), getUsage());
            return;
        }

        val target = parser.select(args.get(0), sender instanceof Player ? ((Player) sender) : null);
        target.forEach(player -> player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        sendMessage(sender, config.getGeneralSuccess(), target.size());
    }
}
