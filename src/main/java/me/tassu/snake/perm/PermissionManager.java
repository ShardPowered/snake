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

package me.tassu.snake.perm;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertOneOptions;
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
import me.tassu.snake.user.User;
import me.tassu.snake.user.UserRegistry;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Map;
import java.util.function.Consumer;

@Singleton
public class PermissionManager implements IRegistrable {

    @Getter
    private Map<String, Integer> permissionData = Maps.newHashMap();

    @Inject
    private MongoManager mongoManager;

    @Inject
    private BukkitScheduler scheduler;

    @Inject
    private SnakePlugin plugin;

    @Inject
    private UserRegistry userRegistry;

    @Inject
    private Log log;

    private UpdateOptions SAVE_OPTIONS = new UpdateOptions().upsert(true);

    private MongoCollection<Document> collection() {
        return mongoManager.getDatabase().getCollection("permissions");
    }

    @Override
    public void register() {
        loadPermissions();

        scheduler.runTaskAsynchronously(plugin, () -> collection().watch()
                .fullDocument(FullDocument.UPDATE_LOOKUP)
                .forEach((Consumer<ChangeStreamDocument<Document>>) change -> {
                    if (change.getOperationType() == OperationType.DELETE) {
                        val id = change.getDocumentKey().getString("_id").getValue();
                        permissionData.remove(id);
                        return;
                    }

                    val document = change.getFullDocument();
                    val id = change.getFullDocument().getString("_id");

                    val value = ((Number) document.get("value")).intValue();

                    permissionData.put(id, value);
                }));
    }

    private void loadPermissions() {
        val collection = collection();
        val all = collection.find();

        for (Document document : all) {
            val id = document.getString("_id");
            val value = ((Number) document.get("value")).intValue();
            permissionData.put(id, value);
        }
    }

    public void registerPermission(String name, int defaultValue) {
        log.debug("Registering permission {}", name);

        val value = new Document()
                .append("_id", name)
                .append("value", defaultValue);

        try {
            collection().insertOne(value);
        } catch (MongoWriteException e) {
            // ignore E11000
            if (!e.getMessage().contains("E11000")) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setPermission(String name, int permValue) {
        val key = new Document("_id", name);
        val value = new Document("$set", new Document("value", permValue));
        collection().updateOne(key, value, SAVE_OPTIONS);
    }

    public int getPermission(String name) {
        return permissionData.get(name);
    }

    public int getPermission(String name, int defaultValue) {
        return permissionData.getOrDefault(name, defaultValue);
    }

    public boolean hasPermission(String permission, Player player) {
        return hasPermission(permission, userRegistry.get(player));
    }

    public boolean hasPermission(String permission, User user) {
        return permissionData.getOrDefault(permission, Integer.MAX_VALUE) < user.getRank().getWeight();
    }

}
