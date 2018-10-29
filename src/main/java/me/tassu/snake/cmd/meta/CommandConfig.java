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

import com.google.inject.Singleton;
import lombok.Getter;
import me.tassu.easy.register.config.Config;
import me.tassu.snake.util.Chat;
import ninja.leaping.configurate.objectmapping.Setting;

@Getter
@Singleton
@Config.Name("commands")
public class CommandConfig extends Config<CommandConfig> {

    private String prefix = Chat.BLUE + Chat.BIG_BLOCK + " Command " + Chat.SMALL_ARROWS_RIGHT +
            Chat.GRAY + " ";

    @Setting("permission")
    private String permissionMessage = prefix + "This command requires permission level " + Chat.BLUE + "{0}" + Chat.GRAY + "!";

    @Setting
    private String usageMessage = prefix + "Usage: " + Chat.WHITE + "{0}";

    @Setting
    private String generalSuccess = prefix + "Affected " + Chat.WHITE + "{0}" + Chat.GRAY + " entities.";

}
