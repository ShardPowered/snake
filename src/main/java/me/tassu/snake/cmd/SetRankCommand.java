package me.tassu.snake.cmd;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import me.tassu.easy.register.command.Aliases;
import me.tassu.easy.register.command.error.CommandException;
import me.tassu.snake.cmd.meta.BaseCommand;
import me.tassu.snake.cmd.meta.CommandConfig;
import me.tassu.snake.user.UserParser;
import me.tassu.snake.user.UserRegistry;
import me.tassu.snake.user.rank.Rank;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

@Singleton
@Aliases({"setrank"})
public class SetRankCommand extends BaseCommand {

    @Inject
    private UserParser parser;

    @Inject
    private UserRegistry registry;

    @Inject
    private CommandConfig config;

    public SetRankCommand() {
        super("setrank", Rank.ADMIN);
        this.usageMessage = "/setrank <users> <rank>";
        this.setDescription("Used to set a rank for a player.");
    }

    @Override
    public void run(CommandSender sender, String label, List<String> args) throws CommandException {
        if (args.size() != 2) {
            sendMessage(sender, config.getUsageMessage(), getUsage());
            return;
        }

        val rank = Rank.byName(args.get(1));
        val target = parser.select(args.get(0), sender instanceof Player ? ((Player) sender) : null);
        target.stream().map(Entity::getUniqueId).map(registry::get).forEach(it -> it.setRank(rank));
        sendMessage(sender, config.getGeneralSuccess(), target.size());
    }
}
