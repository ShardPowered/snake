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

package me.tassu.snake.cmd.staff.admin;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import me.tassu.easy.register.command.Aliases;
import me.tassu.snake.cmd.meta.CommandConfig;
import me.tassu.snake.cmd.meta.UserTargetingCommand;
import me.tassu.snake.user.UserRegistry;
import me.tassu.snake.user.rank.Rank;
import me.tassu.snake.user.rank.RankConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
@Aliases({"setrank"})
public class SetRankCommand extends UserTargetingCommand {

    @Inject
    private UserRegistry registry;

    @Inject
    private RankConfig rankConfig;

    @Inject
    private CommandConfig config;

    public SetRankCommand() {
        super("setrank");
        this.setUsage("/setrank <users> <rank>");
        this.setDescription("Used to set a rank for a player.");

        this.requireArguments(1);
    }

    @Override
    public void run(Player player) {
        val rank = rankConfig.byName(getArguments().get(0));
        registry.get(player).setRank(rank);
    }

    @Override
    protected void sendSuccessMessage(CommandSender sender, Set<Player> target) {
        val rank = rankConfig.byName(getArguments().get(0));
        sendMessage(sender, config.getLocale().getSetRankMessage(), nameOrCount(target), rank.getName());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 2) {
            return rankConfig.getRanks().stream()
                    .filter(rank -> StringUtil.startsWithIgnoreCase(rank.getName(), args[1]))
                    .map(Rank::getName)
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());
        }

        return super.tabComplete(sender, alias, args);
    }
}
