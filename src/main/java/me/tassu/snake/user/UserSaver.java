package me.tassu.snake.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.tassu.easy.register.Worker;

@Singleton
public class UserSaver extends Worker {

    @Inject private UserRegistry userRegistry;

    @Override
    public void register() {
        this.setDelay(50)
                .setPeriod(50)
                .setSync(false)
                .setRunning(true);
    }

    @Override
    public void run() {
        userRegistry.cleanup();
    }
}
