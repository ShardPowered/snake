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

package me.tassu.snake.user;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import me.tassu.snake.api.event.PostUserAchievementGainEvent;
import me.tassu.snake.api.event.PostUserExperienceGainEvent;
import me.tassu.snake.achievement.Achievement;
import me.tassu.snake.api.event.PostUserLevelUpEvent;
import me.tassu.snake.user.level.ExperienceUtil;
import me.tassu.snake.user.level.LevelUtil;
import me.tassu.snake.user.rank.Rank;
import me.tassu.snake.user.rank.RankConfig;
import me.tassu.snake.util.BSONUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class User {

    @Getter(AccessLevel.NONE)
    private ExperienceUtil experienceUtil;

    @Getter(AccessLevel.PACKAGE)
    private Multimap<String, Object> setSaveQueue = HashMultimap.create();

    @Getter(AccessLevel.PACKAGE)
    private Map<String, Object> saveQueue = new HashMap<>();

    private void addToSaveQueue(String key, Object value) {
        saveQueue.put(key, value);
    }

    private void addToSetSaveQueue(String key, Object value) {
        setSaveQueue.put(key, value);
    }

    private Map<String, Long> achievements;

    private UUID uuid;

    private Rank rank;
    private long firstJoin;
    private long totalExperience;
    private String userName;

    @SuppressWarnings("WeakerAccess")
    public int getLevel() {
        return LevelUtil.getLevelForExp(totalExperience);
    }

    public double getLevelProgression() {
        return LevelUtil.getProgress(totalExperience, getLevel());
    }

    User(UUID uuid, Document document, RankConfig config, ExperienceUtil experienceUtil) {
        Preconditions.checkNotNull(uuid);

        this.experienceUtil = experienceUtil;

        this.uuid = uuid;
        this.rank = config.byName(document.getString(UserKey.RANK));

        val firstJoin = document.getLong(UserKey.FIRST_JOIN);
        this.firstJoin = firstJoin == null ? System.currentTimeMillis() : firstJoin;

        val experience = document.getLong(UserKey.EXPERIENCE);
        this.totalExperience = experience == null ? 0 : experience;

        val nickname = document.getString(UserKey.NICKNAME);
        this.userName = nickname == null ? "Steve" : nickname;

        val achievementDoc = BSONUtil.getSubDoc(document, UserKey.ACHIEVEMENTS);
        this.achievements = achievementDoc.keySet().stream()
                .collect(Collectors.toMap(Function.identity(), achievementDoc::getLong));

        addToSaveQueue(UserKey.UUID, uuid.toString());
        
        // nickname and tag updated by UserRegistry#onPlayerJoin
    }

    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public boolean addExperience(long experience, String reason) {
        if (experience < 1) {
            return false;
        }

        val oldLevel = getLevel();
        this.totalExperience += experience;
        boolean levelUp = oldLevel != getLevel();

        experienceUtil.sendMessage(this, experience, reason);

        if (levelUp) {
            for (int i = oldLevel + 1; i <= getLevel(); i++) {
                Bukkit.getPluginManager().callEvent(new PostUserLevelUpEvent(this, i - 1, i));
                experienceUtil.sendLevelUpMessage(this, i);
            }
        }

        addToSaveQueue(UserKey.EXPERIENCE, totalExperience);
        Bukkit.getPluginManager().callEvent(new PostUserExperienceGainEvent(this, experience, reason));

        return levelUp;
    }

    public void addAchievement(Achievement achievement) {
        if (achievement == null) {
            return;
        }

        if (achievements.containsKey(achievement.getId())) {
            return;
        }

        achievements.put(achievement.getId(), System.currentTimeMillis());
        addToSaveQueue(UserKey.ACHIEVEMENTS + "." + achievement.getId(), System.currentTimeMillis());

        Bukkit.getPluginManager().callEvent(new PostUserAchievementGainEvent(this, achievement));

        addExperience(achievement.getExperienceReward(), "completed achievement '" + achievement.getName() + "'");
    }

    public void setNickname(String nickname) {
        if (userName.equals(nickname)) return;

        this.userName = nickname;
        addToSaveQueue(UserKey.NICKNAME, nickname);
    }

    public void setRank(Rank rank) {
        if (rank == getRank()) return;
        this.rank = rank;
        updateTag();

        addToSaveQueue(UserKey.RANK, rank.getName());
    }

    public void updateTag() {
        this.getPlayer().ifPresent(player -> {
            if (rank.getTablistMode() == Rank.TablistMode.SHOW_PREFIX) {
                player.setPlayerListName(getPrefixedName());
            } else if (rank.getTablistMode() == Rank.TablistMode.SHOW_COLOR) {
                player.setPlayerListName(getColoredName());
            } else {
                player.setPlayerListName(null);
            }
        });
    }

    public boolean isOnline() {
        return getPlayer().isPresent();
    }

    public Optional<Player> getPlayer() {
        // update nickname
        val player = Optional.ofNullable(Bukkit.getPlayer(this.uuid));
        player.ifPresent(it -> setNickname(it.getName()));

        return player;
    }

    public String getPrefixedName() {
        return getRank().getTag() + getUserName();
    }

    public String getColoredName() {
        return getRank().getPrimary() + getUserName();
    }

}
