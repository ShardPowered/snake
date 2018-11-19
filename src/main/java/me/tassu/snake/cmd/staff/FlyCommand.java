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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.tassu.easy.register.command.Aliases;
import me.tassu.snake.cmd.meta.Message;
import me.tassu.snake.cmd.meta.UserTargetingCommand;
import me.tassu.snake.util.LocaleConfig;
import org.bukkit.entity.Player;

@Singleton
@Aliases({"fly", "flight"})
public class FlyCommand extends UserTargetingCommand {

    @Inject
    private LocaleConfig locale;

    public FlyCommand() {
        super("fly");

        this.setUsage("/fly [users] [on|off|toggle=toggle]");
        this.setDescription("Used to toggle flight mode for a player.");

        this.defaultToSelf();
        this.requireArguments(0);
    }

    @Override
    public void run(Player player) {
        if (getArguments().size() != 1) {
            player.setAllowFlight(!player.getAllowFlight());
            return;
        }

        player.setAllowFlight(parse(getArguments().get(0)));
    }

    @Override
    protected Message getMessage() {
        if (getArguments().size() == 1) {
            return parse(getArguments().get(0))
                    ? locale.getLocale().getFlightEnabled()
                    : locale.getLocale().getFlightDisabled();
        }

        return locale.getLocale().getFlightToggled();
    }

    private boolean parse(String string) {
        return string.equalsIgnoreCase("true") || string.equalsIgnoreCase("on");
    }

}
