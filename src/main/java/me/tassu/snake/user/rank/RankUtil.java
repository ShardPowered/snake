package me.tassu.snake.user.rank;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import me.tassu.easy.register.core.IRegistrable;
import me.tassu.snake.user.UserRegistry;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

@Singleton
public class RankUtil implements IRegistrable {

    @Inject private UserRegistry registry;

    public Predicate<Player> predicate(String rankIn) {
        val rank = Rank.byName(rankIn);
        return player -> registry.get(player.getUniqueId()).getRank().getWeight() >= rank.getWeight();
    }
}
