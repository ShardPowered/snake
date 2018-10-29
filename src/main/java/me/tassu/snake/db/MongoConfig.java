package me.tassu.snake.db;

import com.google.inject.Singleton;
import lombok.Getter;
import me.tassu.easy.register.config.Config;
import ninja.leaping.configurate.objectmapping.Setting;

@Getter
@Singleton
@Config.Name("mongo")
public class MongoConfig extends Config<MongoConfig> {

    @Setting
    private String host = "localhost";

    @Setting
    private int port = 27017;

    @Setting
    private String username = "root";

    @Setting
    private String password = "password";

    @Setting
    private String database = "minecraft";

    @Setting
    private boolean useSSL = false;

}
