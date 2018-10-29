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

package me.tassu.snake.user;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import me.tassu.snake.user.rank.RankUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
@Singleton
public class UserParser {

    @Inject
    private RankUtil rankUtil;

    public Set<Player> select(@NonNull String query, @Nullable Player sender) {
        if (sender != null && ("self".equalsIgnoreCase(query) || query.isEmpty())) {
            return Collections.singleton(sender);
        }

        if ("all".equalsIgnoreCase(query)) {
            return Sets.newHashSet(Bukkit.getOnlinePlayers());
        }

        if (query.contains("&")) {
            val parts = query.split("&", 2);
            val selectors = parts[1].split(",");
            return filter(select(parts[0], sender), selectors);
        }

        if (query.contains(":")) {
            val selectors = query.split(",");
            return filter(Sets.newHashSet(Bukkit.getOnlinePlayers()), selectors);
        }

        if (!query.contains(",")) {
            val player = Bukkit.getPlayer(query);
            if (player != null) return Collections.singleton(player);
        }

        return Arrays.stream(query.split(","))
                .filter(it -> !it.isEmpty())
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Set<Player> filter(Set<Player> input, String... filters) {
        for (val filter : filters) {
            val parts = filter.split(":");
            if (parts.length < 2) continue;

            switch (parts[0].toLowerCase()) {
                case "hasrank":
                    input.removeIf(rankUtil.predicate(parts[1]).negate());
                    break;
                case "hasnorank":
                    input.removeIf(rankUtil.predicate(parts[1]).negate());
                    break;
            }
        }

        return input;
    }

}
