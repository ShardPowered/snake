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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.val;
import me.tassu.easy.log.Log;
import me.tassu.easy.register.core.IRegistrable;
import me.tassu.snake.db.MongoManager;
import org.bson.Document;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Singleton
public class AchievementRegistry implements IRegistrable {

    private Map<String, Achievement> achievementMap;

    @Override
    public void register() {
        achievementMap = Maps.newHashMap();

        mongo.getDatabase().getCollection(COLLECTION)
                .find()
                .forEach((Consumer<Document>) document -> {
                    val achievement = AchievementBuilder.builder(document.getString(ID))
                            .setName(document.getString(NAME))
                            .setExperience(document.getInteger(EXP_REWARD));

                    achievementMap.put(achievement.getId(), new AchievementInstance(achievement));
                });

        log.debug("Loaded {} achievements.", achievementMap.size());
    }

    @Inject
    private Log log;


    @Inject
    private MongoManager mongo;

    private static final String COLLECTION = "achievements";
    private static final String ID = "_id";
    private static final String NAME = "name";
    private static final String EXP_REWARD = "exp_reward";

    public Achievement register(AchievementBuilder achievement) {
        Preconditions.checkNotNull(achievement);
        Preconditions.checkNotNull(achievement.getName()); // others should not be null

        if (achievementMap.containsKey(achievement.getId())) {
            return achievementMap.get(achievement.getId());
        }

        val instance = new AchievementInstance(achievement);
        achievementMap.put(instance.getId(), instance);

        mongo.getDatabase().getCollection(COLLECTION)
                .insertOne(new Document()
                        .append(ID, achievement.getId())
                        .append(NAME, achievement.getName())
                        .append(EXP_REWARD, achievement.getExperience()));

        return instance;
    }

    public Optional<Achievement> byId(String id) {
        return Optional.ofNullable(achievementMap.get(id));
    }

    @Getter
    private class AchievementInstance implements Achievement {
        private final String name, id;
        private final int experienceReward;

        public AchievementInstance(AchievementBuilder builder) {
            this.name = builder.getName();
            this.id = builder.getId();
            this.experienceReward = builder.getExperience();
        }
    }

}
