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

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.OperationType;
import lombok.experimental.var;
import lombok.val;
import me.tassu.easy.log.Log;
import me.tassu.easy.register.core.IRegistrable;
import me.tassu.simple.TaskChainModule;
import me.tassu.snake.SnakePlugin;
import me.tassu.snake.api.event.AsyncUserJoinedEvent;
import me.tassu.snake.api.event.SyncUserJoinedEvent;
import me.tassu.snake.db.MongoManager;
import me.tassu.snake.user.level.ExperienceUtil;
import me.tassu.snake.user.rank.Rank;
import me.tassu.snake.user.rank.RankRegistry;
import me.tassu.snake.util.SettingUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;

@Singleton
public class UserRegistry implements IRegistrable {

    private static final UpdateOptions SAVE_OPTIONS = new UpdateOptions().upsert(true);

    @Inject
    private RankRegistry config;

    @Inject
    private TaskChainModule chain;

    @Inject
    private ExperienceUtil experienceUtil;

    @Inject
    private SettingUtil settingUtil;

    @Inject
    private Log log;

    @Inject
    private SnakePlugin plugin;

    @Inject
    private MongoManager mongo;

    @Inject
    private BukkitScheduler scheduler;

    @Override
    public void register() {
        scheduler.runTaskAsynchronously(plugin, () -> {
            val collection = mongo.getDatabase().getCollection(UserKey.COLLECTION);
            collection.watch()
                    .fullDocument(FullDocument.UPDATE_LOOKUP)
                    .forEach((Consumer<ChangeStreamDocument<Document>>) change -> {
                        if (change.getOperationType() == OperationType.DELETE) {
                            return;
                        }

                        val document = change.getFullDocument();
                        val id = UUID.fromString(document.getString("_id"));
                        if (users.containsKey(id)) {
                            users.get(id).reload(document);
                        }
                    });
        });

    }

    private Map<UUID, User> users = new WeakHashMap<>();
    private Map<UUID, Long> locked = Maps.newHashMap();

    public void lock(UUID uuid) {
        locked.put(uuid, System.currentTimeMillis() + 1000);
    }

    public void release(UUID uuid) {
        locked.remove(uuid);
    }

    public Optional<User> get(String lastName) {
        if (lastName.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) {
            return Optional.ofNullable(get(UUID.fromString(lastName)));
        }

        val existing = users.values()
                .stream()
                .filter(it -> it.getUserName().equalsIgnoreCase(lastName))
                .findFirst();

        if (existing.isPresent()) {
            return existing;
        }

        var document = mongo.getDatabase().getCollection(UserKey.COLLECTION)
                .find(eq(UserKey.NICKNAME, lastName))
                .first();

        if (document == null) {
            return Optional.empty();
        }

        val user = new User(UUID.fromString(document.getString(UserKey.UUID)), document, config, experienceUtil, settingUtil);
        users.put(user.getUuid(), user);
        return Optional.of(user);
    }

    public User get(UUID uuid) {
        if (!users.containsKey(uuid)) {
            var document = mongo.getDatabase().getCollection(UserKey.COLLECTION)
                    .find(eq(UserKey.UUID, uuid.toString()))
                    .first();

            if (document == null) {
                document = new Document();
            }

            users.put(uuid, new User(uuid, document, config, experienceUtil, settingUtil));
        }

        return users.get(uuid);
    }

    public User get(Player player) {
        return get(player.getUniqueId());
    }

    private void save(User user) {
        val queue = user.getSaveQueue();
        val setQueue = user.getSetSaveQueue();
        if (queue.isEmpty() && setQueue.isEmpty()) return;

        val addDocument = new Document(new LinkedHashMap<>());
        val setDocument = new Document(new LinkedHashMap<>());
        val updateDocument = new Document(new LinkedHashMap<>());

        for (val key : queue.keySet()) {
            setDocument.put(key, queue.get(key));
        }

        setQueue.asMap().forEach(addDocument::put);

        if (!addDocument.isEmpty()) {
            addDocument.forEach((key, val) -> {
                if (val instanceof Collection) {
                    updateDocument.put("$addToSet", new Document(key, new Document("$each", val)));
                } else {
                    updateDocument.put("$addToSet", val);
                }
            });
        }

        updateDocument.put("$set", setDocument);

        val result = mongo.getDatabase().getCollection(UserKey.COLLECTION)
                .updateOne(eq(UserKey.UUID, user.getUuid().toString()), updateDocument, SAVE_OPTIONS);

        if (!result.wasAcknowledged()) {
            log.error("Update for user {} was not saved.", user.getUuid().toString());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        try {
            get(event.getUniqueId()).getRank();
        } catch (Exception ex) {
            log.error("Failure loading user data", ex);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, MongoManager.KICK_MESSAGE);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        chain
                .newChain()
                .delay(5)
                .asyncFirst(() -> get(event.getPlayer()))
                .sync(user -> {
                    Bukkit.getPluginManager().callEvent(new SyncUserJoinedEvent(user));
                    return user;
                })
                .asyncLast(user -> {
                    Bukkit.getPluginManager().callEvent(new AsyncUserJoinedEvent(user));
                })
                .execute();
    }

    public void cleanup() {
        for (val uuid : users.keySet()) {
            save(users.get(uuid));

            if (locked.containsKey(uuid)) {
                if (locked.get(uuid) > System.currentTimeMillis()) {
                    locked.remove(uuid);
                } else {
                    continue;
                }
            }

            if (Bukkit.getPlayer(uuid) == null) {
                users.remove(uuid);
            }
        }
    }



}
