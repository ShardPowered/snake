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

package me.tassu.snake.cmd.meta;

import com.google.inject.Inject;
import lombok.val;
import me.tassu.easy.log.Log;
import me.tassu.easy.register.command.Command;
import me.tassu.easy.register.command.error.CommandException;
import me.tassu.easy.register.command.error.MissingPermissionException;
import me.tassu.snake.perm.PermissionManager;
import me.tassu.snake.user.User;
import me.tassu.snake.user.UserRegistry;
import me.tassu.snake.user.rank.Rank;
import me.tassu.snake.user.rank.RankRegistry;
import me.tassu.snake.util.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public abstract class BaseCommand extends Command {

    @Inject private PermissionManager permissionManager;
    @Inject private UserRegistry registry;

    private final int defaultPermission;

    public BaseCommand(String name) {
        this(name, 250);
    }

    public BaseCommand(String name, int defaultPermission) {
        super(name);
        this.defaultPermission = defaultPermission;
    }

    @Override
    public void register() {
        permissionManager.registerPermission("command." + getName() + ".execute", defaultPermission);
        super.register();
    }

    @Override
    protected void check(CommandSender sender, String label, List<String> args) throws CommandException {
        super.check(sender, label, args);

        if (sender instanceof Player) {
            val user = registry.get((Player) sender);
            val weight = getRequiredPerm();
            if (user == null || user.getRank().getWeight() < weight) {
                throw new MissingPermissionException("");
            }
        }
    }

    protected int getRequiredPerm() {
        return permissionManager.getPermission("command." + getName() + ".execute", defaultPermission);
    }

    protected void sendMessage(CommandSender sender, String message, Object... replacements) {
        sender.sendMessage(Chat.format(message, replacements));
    }

    protected String nameOrCount(Set<? extends Entity> input) {
        if (input.size() == 1) {
            val first = input.stream().findFirst().get();
            if (first instanceof Player) {
                return registry.get((Player) first).getPrefixedName();
            }

            return first.isCustomNameVisible() ? first.getCustomName() : first.getName();
        }

        return input.size() + " " + (input.stream().allMatch(Player.class::isInstance) ? "players" : "entities");
    }

    protected String nameOrCountUsers(Set<User> input) {
        if (input.size() == 1) {
            val first = input.stream().findFirst().get();
            return first.getPrefixedName();
        }

        return input.size() + " users";
    }

    protected void sendSuccessMessage(CommandSender sender, Message message, Object... placeholders) {
        sendMessage(sender, message.getSelf(), placeholders);

        val others = message.getOthers(sender, registry);

        if (!(sender instanceof ConsoleCommandSender)) {
            sendMessage(Bukkit.getConsoleSender(), others, placeholders);
        }

        Bukkit.getOnlinePlayers().stream()
                .filter(it -> it != sender)
                .filter(it -> registry.get(it).getRank().getWeight() >= getRequiredPerm())
                .forEach(it -> sendMessage(it, others, placeholders));
    }

}
