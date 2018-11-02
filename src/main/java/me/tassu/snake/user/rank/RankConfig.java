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

package me.tassu.snake.user.rank;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import me.tassu.easy.log.Log;
import me.tassu.easy.register.config.Config;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Getter
@Singleton
@Config.Name("ranks")
public class RankConfig extends Config<RankConfig> {

    @Inject
    @Getter(AccessLevel.NONE)
    private Log log;

    @Setting
    private List<Rank> ranks = Lists.newArrayList(
            new Rank("MEMBER", 0, ChatColor.GRAY, ChatColor.GRAY).setDefault(),
            new Rank("MODERATOR", 25, ChatColor.BLUE, ChatColor.AQUA),
            new Rank("ADMIN", 150, ChatColor.DARK_RED, ChatColor.RED)
    );

    @Override
    public void load() throws IOException, ObjectMappingException {
        super.load();

        if (ranks.isEmpty()) {
            log.error("No ranks loaded. Something WILL break.");
        }
    }

    public Optional<Rank> matchByName(String name) {
        if (name == null || name.isEmpty()) return Optional.empty();

        return ranks.stream()
                .filter(it -> it.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Gets a rank by its name, defaults to if none match
     */
    public Rank byName(String name) {
        if (name == null || name.isEmpty()) return ranks.get(0);

        return matchByName(name)
                .orElseGet(() -> ranks.get(0));
    }

}