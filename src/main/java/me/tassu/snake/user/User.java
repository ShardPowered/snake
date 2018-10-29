package me.tassu.snake.user;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import me.tassu.snake.user.level.LevelUtil;
import me.tassu.snake.user.rank.Rank;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public class User {

    @Getter(AccessLevel.PACKAGE)
    private Map<String, Object> saveQueue = new HashMap<>();

    private UUID uuid;

    private Rank rank;
    private long firstJoin;
    private long totalExperience;
    private String lastNickname;

    @SuppressWarnings("WeakerAccess")
    public int getLevel() {
        return LevelUtil.getLevelForExp(totalExperience);
    }

    public double getLevelProgression() {
        return LevelUtil.getProgress(totalExperience, getLevel());
    }

    User(UUID uuid, Document document) {
        Preconditions.checkNotNull(uuid);

        this.uuid = uuid;
        this.rank = Rank.byName(document.getString(UserKey.RANK));

        val firstJoin = document.getLong(UserKey.FIRST_JOIN);
        this.firstJoin = firstJoin == null ? System.currentTimeMillis() : firstJoin;

        val experience = document.getLong(UserKey.EXPERIENCE);
        this.totalExperience = experience == null ? 0 : experience;

        val nickname = document.getString(UserKey.NICKNAME);
        this.lastNickname = nickname == null ? "Steve" : nickname;

        addToSaveQueue(UserKey.UUID, uuid.toString());

        // nickname and tag updated by UserRegistry#onPlayerJoin
    }

    public void addExperience(long experience) {
        // TODO check levelup and add rewards
        this.totalExperience += experience;
        addToSaveQueue(UserKey.EXPERIENCE, totalExperience);
    }

    public void setNickname(String nickname) {
        if (lastNickname.equals(nickname)) return;

        this.lastNickname = nickname;
        addToSaveQueue(UserKey.NICKNAME, nickname);
    }

    public void setRank(Rank rank) {
        if (rank == getRank()) return;
        this.rank = rank;
        updateTag();

        addToSaveQueue(UserKey.RANK, rank.name());
    }

    public void updateTag() {
        this.getPlayer().ifPresent(player -> {
            player.setPlayerListName(rank.getTag() + getLastNickname());
            player.setCustomName(rank.getTag() + getLastNickname());
        });
    }

    private void addToSaveQueue(String key, Object value) {
        saveQueue.put(key, value);
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

}
