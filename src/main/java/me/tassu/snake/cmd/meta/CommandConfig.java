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

package me.tassu.snake.cmd.meta;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import lombok.Getter;
import me.tassu.easy.register.config.Config;
import ninja.leaping.configurate.objectmapping.Setting;

import java.util.Map;

@Getter
@Singleton
@Config.Name("commands")
public class CommandConfig extends Config<CommandConfig> {

    @Setting("permissions")
    private Map<String, String> requiredRanks = ImmutableMap.<String, String>builder()
            .put("help", "DEFAULT")
            .put("uptime", "DEFAULT")
            .put("setrank", "ADMIN")
            .put("rankadmin", "ADMIN")
            .put("gamemode", "ADMIN")
            .put("fly", "MODERATOR")
            .put("heal", "MODERATOR")
            .put("feed", "MODERATOR")
            .build();


}
