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
