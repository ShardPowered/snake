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

package me.tassu.snake.achievement;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.tassu.easy.register.core.IRegistrable;

@Singleton
public class StandardAchievements implements IRegistrable {

    public static final String FIRST_JOIN = "FIRST_JOIN";
    public static final String CHAT = "CHAT";

    @Inject
    private AchievementRegistry registry;

    @Override
    public void register() {
        registry.register(AchievementBuilder.builder(FIRST_JOIN)
                .setName("First join")
                .setDescription("Join the server. It's that simple.")
                .setExperience(10));

        registry.register(AchievementBuilder.builder(CHAT)
                .setName("Your beautiful voice")
                .setDescription("Talk in chat.")
                .setExperience(25));
    }
}
