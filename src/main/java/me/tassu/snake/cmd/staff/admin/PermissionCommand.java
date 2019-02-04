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
import me.tassu.snake.cmd.meta.BaseCommand;
import me.tassu.snake.perm.PermissionManager;
import me.tassu.snake.util.LocaleConfig;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

@Singleton
@Aliases({"permissionadmin", "permission", "perm", "permadmin"})
public class PermissionCommand extends BaseCommand {

    @Inject
    private LocaleConfig locale;

    @Inject
    private PermissionManager permissionManager;

    public PermissionCommand() {
        super("permissionadmin", 250);
        this.setUsage("/rankadmin help");
        this.setDescription("Used to manage permissions.");
    }

    @Override
    protected void run(CommandSender sender, String label, List<String> args) {
        if (args.size() == 0) {
            sendHelp(sender);
            return;
        }

        switch (args.get(0).toLowerCase()) {
            case "list":
                sendMessage(sender, locale.getLocale().getPermAdminListHeader());

                for (Map.Entry<String, Integer> permission : permissionManager.getPermissionData().entrySet()) {
                    sendMessage(sender, locale.getLocale().getPermAdminListEntry(),
                            permission.getKey(), permission.getValue());
                }

                break;
            case "set":
                if (args.size() != 3) {
                    sendHelp(sender);
                    return;
                }

                val name = args.get(1);

                if (!permissionManager.getPermissionData().containsKey(name)) {
                    sendMessage(sender, locale.getLocale().getPermAdminInvalidRank());
                    return;
                }

                int value = 0;
                try {
                    value = Integer.parseInt(args.get(2));
                } catch (Exception ignored) {}

                permissionManager.setPermission(name, value);
                sendSuccessMessage(sender, locale.getLocale().getPermAdminSetValue(), name, value);
                break;
            default:
                sendHelp(sender);
                break;
        }
    }

    private void sendHelp(CommandSender sender) {
        for (String message : locale.getLocale().getPermAdminHelp()) {
            sendMessage(sender, message);
        }
    }

}
