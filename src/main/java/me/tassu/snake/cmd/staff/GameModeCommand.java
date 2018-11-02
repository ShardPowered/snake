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

package me.tassu.snake.cmd.staff;

import com.google.inject.Singleton;
import lombok.val;
import me.tassu.easy.register.command.Aliases;
import me.tassu.easy.register.command.error.CommandException;
import me.tassu.snake.cmd.meta.UserTargetingCommand;
import me.tassu.snake.cmd.meta.ex.UsageException;
import me.tassu.snake.user.rank.Rank;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Aliases({"gamemode", "gm"})
public class GameModeCommand extends UserTargetingCommand {

    public GameModeCommand() {
        super("gamemode");
        this.defaultToSelf();
        this.requireArguments(1);
        this.setUsage("/gamemode [users=self] <mode>");
    }

    @Override
    public void run(Player player) throws CommandException {
        val mode = byName(getArguments().get(0));

        if (mode == null) {
            throw new UsageException();
        }

        player.setGameMode(mode);
    }

    private GameMode byName(String name) {
        switch (name.toLowerCase().trim()) {
            case "c":
            case "creative":
            case "1":
                return GameMode.CREATIVE;
            case "s":
            case "survival":
            case "0":
                return GameMode.SURVIVAL;
            case "a":
            case "adventure":
            case "2":
                return GameMode.ADVENTURE;
            case "sp":
            case "spectator":
            case "3":
                return GameMode.SPECTATOR;
            default:
                return null;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 2) {
            return Arrays.stream(new String[]{ "creative", "survival", "adventure", "spectator", "0", "1", "2", "3" })
                    .filter(mode -> StringUtil.startsWithIgnoreCase(mode, args[1]))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());
        }

        return super.tabComplete(sender, alias, args);
    }
}
