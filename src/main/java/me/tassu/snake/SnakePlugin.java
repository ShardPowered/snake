package me.tassu.snake;

import me.tassu.easy.EasyPlugin;
import me.tassu.easy.api.binder.BindManager;
import me.tassu.easy.api.message.IMessageProvider;
import me.tassu.easy.register.config.Config;
import me.tassu.simple.TaskChainModule;
import me.tassu.snake.cmd.HealCommand;
import me.tassu.snake.cmd.SetRankCommand;
import me.tassu.snake.cmd.meta.CommandConfig;
import me.tassu.snake.db.MongoConfig;
import me.tassu.snake.db.MongoManager;
import me.tassu.snake.user.UserRegistry;
import me.tassu.snake.user.UserSaver;
import me.tassu.snake.user.rank.RankUtil;
import me.tassu.snake.util.Messager;

import static me.tassu.cfg.ConfigUtil.run;

public final class SnakePlugin extends EasyPlugin {

    @Override
    protected void init() {
        registerAll(
                MongoConfig.class,
                MongoManager.class,
                UserRegistry.class,
                UserSaver.class,

                TaskChainModule.class,
                RankUtil.class,

                CommandConfig.class,

                SetRankCommand.class,
                HealCommand.class
        );
    }

    @Override
    protected void stop() {
        // save ALL users
        getModule(UserRegistry.class)
                .ifPresent(UserRegistry::cleanup);

        // save configurations
        getRegistrableSet().stream()
                .filter(Config.class::isInstance)
                .map(Config.class::cast)
                .forEach(config -> run((config::save)));
    }

    @Override
    protected BindManager getBinder() {
        return new BindManager() {
            @Override
            public Class<? extends IMessageProvider> getMessageProvider() {
                return Messager.class;
            }
        };
    }
}
