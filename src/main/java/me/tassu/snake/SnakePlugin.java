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

package me.tassu.snake;

import com.google.inject.Binder;
import com.google.inject.Inject;
import io.papermc.lib.PaperLib;
import me.tassu.easy.EasyPlugin;
import me.tassu.easy.api.binder.BindManager;
import me.tassu.easy.api.message.IMessageProvider;
import me.tassu.easy.log.Log;
import me.tassu.simple.TaskChainModule;
import me.tassu.snake.api.SnakeAPI;
import me.tassu.snake.chat.ChatConfig;
import me.tassu.snake.chat.ChatFormatter;
import me.tassu.snake.cmd.meta.CommandConfig;
import me.tassu.snake.user.achievement.AchievementListener;
import me.tassu.snake.user.level.ExperienceUtil;
import me.tassu.snake.util.LocaleConfig;
import me.tassu.snake.cmd.meta.NoPrefixedCommand;
import me.tassu.snake.cmd.staff.FeedCommand;
import me.tassu.snake.cmd.staff.GameModeCommand;
import me.tassu.snake.cmd.staff.HealCommand;
import me.tassu.snake.cmd.staff.admin.SetRankCommand;
import me.tassu.snake.cmd.user.HelpCommand;
import me.tassu.snake.cmd.user.UptimeCommand;
import me.tassu.snake.db.MongoConfig;
import me.tassu.snake.db.MongoManager;
import me.tassu.snake.user.UserRegistry;
import me.tassu.snake.user.UserSaver;
import me.tassu.snake.user.rank.RankConfig;
import me.tassu.snake.user.rank.RankUtil;
import me.tassu.snake.util.Messager;

public final class SnakePlugin extends EasyPlugin {

    @Inject
    private Log log;

    @Override
    protected void init() {
        if (!PaperLib.isPaper()) {
            log.error("Snake does heavily depend on");
            log.error("Paper as a server software.");
            log.error("");
            log.error("Learn more about Paper: https://papermc.io");

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerAll(
                MongoConfig.class,
                MongoManager.class,
                UserRegistry.class,
                UserSaver.class,

                TaskChainModule.class,
                ExperienceUtil.class,
                RankUtil.class,

                CommandConfig.class,
                LocaleConfig.class,
                RankConfig.class,
                ChatConfig.class,

                AchievementListener.class,
                NoPrefixedCommand.class,
                ChatFormatter.class,

                UptimeCommand.class,
                HelpCommand.class,

                GameModeCommand.class,
                SetRankCommand.class,
                FeedCommand.class,
                HealCommand.class
        );
    }

    @Override
    protected void stop() {
        // save ALL users
        getModule(UserRegistry.class)
                .ifPresent(UserRegistry::cleanup);
    }

    @Override
    protected BindManager getBinder() {
        return new BindManager() {
            @Override
            public void bindCustom(Binder binder) {
                binder.bind(SnakeAPI.class).toInstance(new SnakeAPI());
            }

            @Override
            public Class<? extends IMessageProvider> getMessageProvider() {
                return Messager.class;
            }
        };
    }
}
