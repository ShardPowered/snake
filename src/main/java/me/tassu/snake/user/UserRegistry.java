package me.tassu.snake.user;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.client.model.UpdateOptions;
import lombok.experimental.var;
import lombok.val;
import me.tassu.easy.log.Log;
import me.tassu.easy.register.core.IRegistrable;
import me.tassu.simple.TaskChainModule;
import me.tassu.snake.cmd.meta.CommandConfig;
import me.tassu.snake.db.MongoManager;
import me.tassu.snake.util.Chat;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import static com.mongodb.client.model.Filters.eq;

@Singleton
public class UserRegistry implements IRegistrable {

    private static final UpdateOptions SAVE_OPTIONS = new UpdateOptions().upsert(true);

    @Inject
    private TaskChainModule chain;

    @Inject
    private Log log;

    @Inject
    private MongoManager mongo;

    private Map<UUID, User> users = new WeakHashMap<>();
    private Map<UUID, Long> locked = Maps.newHashMap();

    public void lock(UUID uuid) {
        locked.put(uuid, System.currentTimeMillis() + 1000);
    }

    public void release(UUID uuid) {
        locked.remove(uuid);
    }

    public User get(UUID uuid) {
        if (!users.containsKey(uuid)) {
            var document = mongo.getDatabase().getCollection(UserKey.COLLECTION)
                    .find(eq(UserKey.UUID, uuid.toString()))
                    .first();

            if (document == null) {
                document = new Document();
            }

            users.put(uuid, new User(uuid, document));
        }

        return users.get(uuid);
    }

    private User get(Player player) {
        return get(player.getUniqueId());
    }

    private void save(User user) {
        val queue = user.getSaveQueue();
        if (queue.isEmpty()) return;

        val document = new Document(new LinkedHashMap<>());

        for (String key : queue.keySet()) {
            document.put(key, queue.get(key));
        }

        val updateDocument = new Document();
        updateDocument.put("$set", document);

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
        val user = get(event.getPlayer());
        user.setNickname(event.getPlayer().getName());
        user.updateTag();

        user.getPlayer().ifPresent(it -> it.sendMessage(Chat.format("Your rank is {0}", user.getRank().name())));
    }

    public void cleanup() {
        for (UUID uuid : users.keySet()) {
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
