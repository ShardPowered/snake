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

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import lombok.val;
import me.tassu.easy.register.command.error.CommandException;
import me.tassu.snake.cmd.meta.ex.UsageException;
import me.tassu.snake.user.UserParser;
import me.tassu.util.ArrayUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public abstract class UserTargetingCommand extends BaseCommand {

    private boolean defaultToSelf = false;
    private boolean useArguments = false;
    private int requireArguments = 0;

    @SuppressWarnings("WeakerAccess")
    public UserTargetingCommand defaultToSelf() {
        this.defaultToSelf = true;
        return this;
    }

    @SuppressWarnings("WeakerAccess")
    public UserTargetingCommand useArguments() {
        this.useArguments = true;
        return this;
    }

    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public UserTargetingCommand requireArguments(int requireArguments) {
        this.requireArguments = requireArguments;
        return useArguments();
    }

    private List<String> arguments;

    protected List<String> getArguments() {
        if (arguments == null) {
            throw new IllegalStateException("whoops");
        }

        return arguments;
    }

    @Inject
    private UserParser parser;

    @Inject
    private CommandConfig config;

    public UserTargetingCommand(String name) {
        super(name);
    }

    @Override
    public final void run(CommandSender sender, String label, List<String> args) throws CommandException {
        if (args.isEmpty()) {
            if (!useArguments && sender instanceof Player) {
                args = Lists.newArrayList(sender.getName());
            } else {
                sendMessage(sender, config.getLocale().getUsageMessage(), getUsage());
                return;
            }
        }

        if (useArguments) {
            arguments = ArrayUtil.withoutFirst(args);

            if (arguments.size() < requireArguments) {
                if (arguments.size() + 1 >= requireArguments && defaultToSelf && sender instanceof Player) {
                    arguments = args;
                    args = Lists.newArrayList(sender.getName());
                } else {
                    arguments = null;
                    sendMessage(sender, config.getLocale().getUsageMessage(), getUsage());
                    return;
                }
            }
        }

        try {
            val target = parser.select(args.get(0), sender instanceof Player ? ((Player) sender) : null);

            try {
                for (Player player : target) {
                    run(player);
                }
            } catch (UsageException e) {
                sendMessage(sender, config.getLocale().getUsageMessage(), getUsage());
                return;
            }

            sendSuccessMessage(sender, target);
        } finally {
            arguments = null;
        }
    }

    public abstract void run(Player player) throws CommandException;

    protected void sendSuccessMessage(CommandSender sender, Set<Player> target) {
        sendMessage(sender, config.getLocale().getEntityAffectSuccess(), nameOrCount(target));
    }

}
