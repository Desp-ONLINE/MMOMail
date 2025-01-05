package com.binggre.mmomail;

import com.binggre.binggreapi.BinggrePlugin;
import com.binggre.mmomail.commands.UserCommand;
import com.binggre.mmomail.config.MessageConfig;
import com.binggre.mmomail.listeners.PlayerListener;
import com.binggre.mmomail.repository.PlayerRepository;
import lombok.Getter;

import java.util.HashMap;

@Getter
public final class MMOMail extends BinggrePlugin {

    @Getter
    private static MMOMail instance;
    public static final String DATA_BASE_NAME = "MMO-Mail";

    private PlayerRepository playerRepository;

    @Override
    public void onEnable() {
        instance = this;
        playerRepository = new PlayerRepository(this, DATA_BASE_NAME, "Player", new HashMap<>());
        executeCommand(this, new UserCommand());
        registerEvents(this,
                new PlayerListener()
        );

        playerRepository.init();
        MessageConfig.getInstance().init();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
