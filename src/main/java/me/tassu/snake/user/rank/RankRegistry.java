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

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.val;
import me.tassu.easy.log.Log;
import me.tassu.easy.register.core.IRegistrable;
import me.tassu.snake.db.MongoManager;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;

@Singleton
public class RankRegistry implements IRegistrable {

    @Inject
    private Log log;

    @Inject
    private MongoManager mongoManager;

    private UpdateOptions SAVE_OPTIONS = new UpdateOptions().upsert(true);

    @Getter
    private Set<Rank> ranks = Sets.newHashSet();

    private MongoCollection<Document> collection() {
        return mongoManager.getDatabase().getCollection("ranks");
    }

    @Override
    public void register() {
        reloadRanks();

        if (ranks.isEmpty()) {
            log.debug("Creating default ranks");
            addRank(new Rank("DEFAULT", "Member", 0, ChatColor.GRAY, ChatColor.GRAY, true, Rank.TablistMode.SHOW_COLOR));
            addRank(new Rank("ADMIN", "Admin", 250, ChatColor.DARK_RED, ChatColor.RED, false, Rank.TablistMode.SHOW_COLOR));
        }
    }

    private void reloadRanks() {
        val collection = collection();
        val all = collection.find();

        for (Document document : all) {
            val id = document.getString("_id");
            val current = matchByName(id);

            if (current.isPresent()) {
                current.get().updateFrom(document);
            } else {
                val rank = Rank.fromDocument(document);
                ranks.add(rank);
            }
        }
    }

    private void addRank(Rank rank) {
        val current = matchByName(rank.getName());

        if (current.isPresent()) {
            throw new IllegalArgumentException("Rank with specified name already exists.");
        } else {
            ranks.add(rank);
        }

        save(rank);
    }

    private void save(Rank rank) {
        val collection = collection();
        collection.updateOne(eq("_id", rank.getName()), new Document("$set", rank.toDocument()), SAVE_OPTIONS);
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
        if (name == null || name.isEmpty()) return ranks.stream().findFirst().orElseThrow(() -> new IllegalStateException("no ranks found"));

        return matchByName(name)
                .orElseGet(() -> ranks.stream().findFirst()
                        .orElseThrow(() -> new IllegalStateException("no ranks found")));
    }
}
