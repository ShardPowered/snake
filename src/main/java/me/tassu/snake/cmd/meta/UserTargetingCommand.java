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
import me.tassu.snake.user.UserParser;
import me.tassu.snake.user.rank.Rank;
import me.tassu.util.ArrayUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public abstract class UserTargetingCommand extends BaseCommand {

    private boolean useArguments;
    private int requireArguments;

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

    public UserTargetingCommand(String name, Rank rank) {
        super(name, rank);
    }

    @Override
    public final void run(CommandSender sender, String label, List<String> args) {
        if (args.isEmpty()) {
            sendMessage(sender, config.getUsageMessage(), getUsage());
            return;
        }

        if (useArguments) {
            arguments = ArrayUtil.withoutFirst(args);

            if (arguments.size() < requireArguments) {
                arguments = null;
                sendMessage(sender, config.getUsageMessage(), getUsage());
                return;
            }
        }

        try {
            val target = parser.select(args.get(0), sender instanceof Player ? ((Player) sender) : null);
            target.forEach(this::run);
            sendSuccessMessage(sender, target);
        } finally {
            arguments = null;
        }
    }

    public abstract void run(Player player);

    protected void sendSuccessMessage(CommandSender sender, Set<Player> target) {
        sendMessage(sender, config.getEntityAffectSuccess(), target.size());
    }

}
