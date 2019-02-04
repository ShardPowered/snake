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
import lombok.experimental.var;
import lombok.val;
import me.tassu.easy.register.command.Aliases;
import me.tassu.snake.cmd.meta.BaseCommand;
import me.tassu.snake.user.rank.Rank;
import me.tassu.snake.user.rank.RankRegistry;
import me.tassu.snake.util.LocaleConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Aliases({"rankadmin", "rank"})
public class RankCommand extends BaseCommand {

    @Inject
    private RankRegistry rankRegistry;

    @Inject
    private LocaleConfig locale;

    public RankCommand() {
        super("rankadmin", 250);
        this.setUsage("/rankadmin help");
        this.setDescription("Used to manage ranks.");
    }

    @Override
    protected void run(CommandSender sender, String label, List<String> args) {
        if (args.size() == 0) {
            sendHelp(sender);
            return;
        }

        switch (args.get(0).toLowerCase()) {
            case "list":
                sendMessage(sender, locale.getLocale().getRankAdminListHeader());
                for (Rank rank : rankRegistry.getRanks().values().stream()
                        .sorted(Comparator.comparingInt(Rank::getWeight).reversed())
                        .collect(Collectors.toList())) {
                    sendMessage(sender, locale.getLocale().getRankAdminListEntry(),
                            rank.getPrimary().toString(), rank.getName(), rank.getTag().trim(), rank.getWeight());
                }
                break;
            case "create":
                if (args.size() < 2) {
                    sendHelp(sender);
                    return;
                }

                var name = args.get(1);
                if (!name.matches("[A-Z0-9\\-]+")) {
                    sendMessage(sender, locale.getLocale().getRankAdminInvalidName());
                    return;
                }

                int weight = 0;
                if (args.size() > 2) {
                    try {
                        weight = Integer.parseInt(args.get(2));
                    } catch (Exception ignored) {}
                }

                var rank = rankRegistry.addRank(new Rank(name, weight));
                sendSuccessMessage(sender, locale.getLocale().getRankAdminRankCreated(), rank.getName());
                break;
            case "edit":
            case "modify":
                if (args.size() < 4) {
                    sendHelp(sender);
                    return;
                }

                name = args.get(1);
                var optionalRank = rankRegistry.matchByName(name);
                if (!optionalRank.isPresent()) {
                    sendHelp(sender);
                    return;
                }

                rank = optionalRank.get();

                switch (args.get(2).toLowerCase()) {
                    case "display":
                        rank.setNickname(args.get(3));
                        sendSuccessMessage(sender, locale.getLocale().getRankAdminRankModified(),
                                rank.getName(), "display name", rank.getNickname());
                        break;
                    case "weight":
                        weight = 0;
                        try {
                            weight = Integer.parseInt(args.get(3));
                        } catch (Exception ignored) {}

                        rank.setWeight(weight);
                        sendSuccessMessage(sender, locale.getLocale().getRankAdminRankModified(),
                                rank.getName(), "weight", rank.getWeight());
                        break;
                    case "primary":
                    case "secondary":
                        ChatColor color = ChatColor.GRAY;
                        try {
                            color = ChatColor.getByChar(args.get(3));
                            
                            //noinspection ResultOfMethodCallIgnored
                            color.name();
                        } catch (Exception ignored) {
                            try {
                                color = ChatColor.valueOf(args.get(3));
                            } catch (Exception ignored2) {}
                        }

                        if (args.get(2).equalsIgnoreCase("primary")) {
                            rank.setPrimary(color);
                            sendSuccessMessage(sender, locale.getLocale().getRankAdminRankModified(),
                                    rank.getName(), "primary color", rank.getPrimary().name());
                        } else {
                            rank.setSecondary(color);
                            sendSuccessMessage(sender, locale.getLocale().getRankAdminRankModified(),
                                    rank.getName(), "secondary color", rank.getSecondary().name());
                        }

                        break;
                    case "tab":
                        Rank.TablistMode mode = Rank.TablistMode.SHOW_COLOR;
                        try {
                            mode = Rank.TablistMode.valueOf(args.get(3));
                        } catch (Exception ignored) {}

                        rank.setTablistMode(mode);
                        sendSuccessMessage(sender, locale.getLocale().getRankAdminRankModified(),
                                rank.getName(), "tablist mode", rank.getTablistMode().name());
                        break;
                    default:
                        sendHelp(sender);
                        break;
                }

                rankRegistry.save(rank);
                break;
            case "delete":
                if (args.size() < 2) {
                    sendHelp(sender);
                    return;
                }

                name = args.get(1);
                optionalRank = rankRegistry.matchByName(name);
                if (!optionalRank.isPresent()) {
                    sendHelp(sender);
                    return;
                }

                rank = optionalRank.get();

                if (rank.isDefault()) {
                    sendMessage(sender, locale.getLocale().getRankAdminDeleteDefault());
                    return;
                }

                rankRegistry.deleteRank(rank);
                sendSuccessMessage(sender, locale.getLocale().getRankAdminRankDeleted(), rank.getName());
                break;
            default:
                sendHelp(sender);
                break;
        }
    }

    private void sendHelp(CommandSender sender) {
        for (String message : locale.getLocale().getRankAdminHelp()) {
            sendMessage(sender, message);
        }
    }

}
