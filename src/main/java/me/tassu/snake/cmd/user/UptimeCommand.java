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

package me.tassu.snake.cmd.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import me.tassu.easy.register.command.Aliases;
import me.tassu.easy.util.TimeUtil;
import me.tassu.snake.cmd.meta.BaseCommand;
import me.tassu.snake.cmd.meta.CommandConfig;
import org.bukkit.command.CommandSender;

import java.util.List;

@Singleton
@Aliases({"uptime"})
public class UptimeCommand extends BaseCommand {

    @Inject
    private CommandConfig config;

    private final long started = System.currentTimeMillis();

    public UptimeCommand() {
        super("uptime");
        this.setDescription("Views the uptime of this server.");
    }

    @Override
    protected void run(CommandSender sender, String label, List<String> args) {
        val seconds = (System.currentTimeMillis() - started) / 1000;
        sendMessage(sender, config.getLocale().getUptimeMessage(), TimeUtil.format(seconds, 0));
    }
}
