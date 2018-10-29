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
