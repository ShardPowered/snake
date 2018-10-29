package me.tassu.snake.db;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.event.*;
import lombok.Getter;
import lombok.val;
import me.tassu.easy.log.Log;
import me.tassu.easy.register.core.IRegistrable;
import me.tassu.snake.SnakePlugin;
import me.tassu.snake.util.Chat;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class MongoManager implements IRegistrable {

    public static final String KICK_MESSAGE = Chat.RED + Chat.BOLD + "MongoDB (database) failure";

    private boolean connected = false;

    @Inject
    private Log log;

    @Inject
    private SnakePlugin plugin;

    @Inject
    private MongoConfig config;

    @Getter
    private MongoClient client;

    public MongoDatabase getDatabase() {
        return client.getDatabase(config.getDatabase());
    }

    @Override
    public void register() {
        close();

        val credential = MongoCredential.createCredential(config.getUsername(), config.getDatabase(), config.getPassword().toCharArray());
        val settings = MongoClientSettings.builder()
                .credential(credential)
                .applyToSslSettings(builder -> builder.enabled(config.isUseSSL()))
                .applyToClusterSettings(builder ->
                        builder.hosts(Collections.singletonList(new ServerAddress(config.getHost(), config.getPort()))))
                .applyToServerSettings(builder -> builder.addServerMonitorListener(new ServerMonitorListener() {
                    @Override
                    public void serverHearbeatStarted(ServerHeartbeatStartedEvent event) {}

                    @Override
                    public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent event) {
                        if (!connected) {
                            log.info("Connected to MongoDB.");
                        }

                        connected = true;
                    }

                    @Override
                    public void serverHeartbeatFailed(ServerHeartbeatFailedEvent event) {
                        connected = false;

                        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getOnlinePlayers().forEach(it -> it.kickPlayer(KICK_MESSAGE)));
                        log.error("MongoDB connection dropped", event.getThrowable());
                    }
                }))
                .build();
        try {
            client = MongoClients.create(settings);
        } catch (MongoException ex) {
            connected = false;

            Bukkit.getOnlinePlayers().forEach(it -> it.kickPlayer(KICK_MESSAGE));
            log.error("MongoDB connection failed", ex);
        }

        Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
        Logger.getLogger("org.mongodb.driver.connection").setLevel(Level.SEVERE);
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
            client = null;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!connected) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, KICK_MESSAGE);
        }
    }
}
