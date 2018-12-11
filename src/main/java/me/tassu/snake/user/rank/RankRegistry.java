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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.OperationType;
import lombok.Getter;
import lombok.val;
import me.tassu.easy.log.Log;
import me.tassu.easy.register.core.IRegistrable;
import me.tassu.snake.SnakePlugin;
import me.tassu.snake.db.MongoManager;
import me.tassu.snake.user.UserKey;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;

@Singleton
public class RankRegistry implements IRegistrable {

    @Inject
    private Log log;

    @Inject
    private MongoManager mongoManager;

    @Inject
    private BukkitScheduler scheduler;

    @Inject
    private SnakePlugin plugin;

    private UpdateOptions SAVE_OPTIONS = new UpdateOptions().upsert(true);

    @Getter
    private Map<String, Rank> ranks = Maps.newHashMap();

    private MongoCollection<Document> collection() {
        return mongoManager.getDatabase().getCollection("ranks");
    }

    @Override
    public void register() {
        reloadRanks();

        if (ranks.isEmpty()) {
            log.debug("Creating default ranks");
            addRank(new Rank("DEFAULT", "Member", 0, ChatColor.GRAY, ChatColor.GRAY, true, Rank.TablistMode.SHOW_COLOR));
            addRank(new Rank("MODERATOR", "Moderator", 25, ChatColor.DARK_GREEN, ChatColor.GREEN, false, Rank.TablistMode.SHOW_COLOR));
            addRank(new Rank("ADMIN", "Admin", 250, ChatColor.DARK_RED, ChatColor.RED, false, Rank.TablistMode.SHOW_COLOR));
        }

        scheduler.runTaskAsynchronously(plugin, () -> {
            val collection = collection();
            collection.watch()
                    .fullDocument(FullDocument.UPDATE_LOOKUP)
                    .forEach((Consumer<ChangeStreamDocument<Document>>) change -> {
                        if (change.getOperationType() == OperationType.DELETE) {
                            val name = change.getDocumentKey().getString("_id").getValue();
                            ranks.remove(name);

                            log.info("Removed rank {}.", name);
                            return;
                        }

                        val document = change.getFullDocument();
                        val name = change.getFullDocument().getString("_id");
                        val current = matchByName(name);

                        if (current.isPresent()) {
                            current.get().updateFrom(document);
                        } else {
                            val rank = Rank.fromDocument(document);
                            addRank(rank);
                        }

                        log.info("Reloaded rank {}.", name);
                    });
        });
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
                addRank(rank);
            }
        }
    }

    public Rank addRank(Rank rank) {
        val current = matchByName(rank.getName());

        if (current.isPresent()) {
            throw new IllegalArgumentException("Rank with specified name already exists.");
        } else {
            ranks.put(rank.getName(), rank);
        }

        save(rank);
        return rank;
    }

    public void save(Rank rank) {
        val collection = collection();
        collection.updateOne(eq("_id", rank.getName()), new Document("$set", rank.toDocument()), SAVE_OPTIONS);
    }

    public Rank getDefaultRank() {
        return ranks.values().stream()
                .filter(Rank::isDefault)
                .findFirst().orElseThrow(() -> new IllegalStateException("no default rank"));
    }

    public Optional<Rank> matchByName(String name) {
        if (name == null || name.isEmpty()) return Optional.empty();

        return ranks.keySet().stream()
                .filter(it -> it.equalsIgnoreCase(name))
                .map(ranks::get)
                .findFirst();
    }

    /**
     * Gets a rank by its name, defaults to if none match
     */
    public Rank byName(String name) {
        if (name == null || name.isEmpty()) return ranks.values()
                .stream().findFirst().orElseGet(this::getDefaultRank);

        return matchByName(name)
                .orElseGet(this::getDefaultRank);
    }

    public void deleteRank(Rank rank) {
        // update user ranks
        val userCollection = mongoManager.getDatabase().getCollection(UserKey.COLLECTION);
        userCollection.updateMany(eq("rank", rank.getName()),
                new Document("$set", new Document("rank", getDefaultRank().getName())));

        // drop from database
        val collection = collection();
        collection.deleteOne(eq("_id", rank.getName()));

        // remove from map
        ranks.remove(rank.getName());
    }
}
