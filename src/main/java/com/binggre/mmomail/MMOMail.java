package com.binggre.mmomail;

import com.binggre.binggreapi.BinggrePlugin;
import com.binggre.binggreapi.utils.metadata.KeepMetadataManager;
import com.binggre.binggreapi.utils.metadata.MetadataManager;
import com.binggre.mmomail.api.MailAPI;
import com.binggre.mmomail.api.MailAPIImpl;
import com.binggre.mmomail.commands.user.UserCommand;
import com.binggre.mmomail.config.GUIConfig;
import com.binggre.mmomail.config.MessageConfig;
import com.binggre.mmomail.listeners.PlayerListener;
import com.binggre.mmomail.listeners.velocity.MailGUIUpdateListener;
import com.binggre.mmomail.repository.PlayerRepository;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.socket.SocketClient;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;

@Getter
public final class MMOMail extends BinggrePlugin implements CommandExecutor {

    @Getter
    private static MMOMail instance;
    public static final String DATA_BASE_NAME = "MMO-Mail";

    private PlayerRepository playerRepository;
    private SocketClient socketClient;
    private MailAPI mailAPI;
    private final MetadataManager keepMetadataManager = new KeepMetadataManager(this);

    @Override
    public void onEnable() {
        instance = this;
        saveResource("gui.json", false);

        socketClient = VelocityClient.getInstance().getConnectClient();
        socketClient.registerListener(MailGUIUpdateListener.class);
        mailAPI = new MailAPIImpl();
        playerRepository = new PlayerRepository(
                this,
                DATA_BASE_NAME,
                "Player",
                null);

        executeCommand(this, new UserCommand());
        registerEvents(this,
                new PlayerListener()
        );

        playerRepository.init();
        MessageConfig.getInstance().init();
        GUIConfig.getInstance().init();
    }

    @Override
    public void onDisable() {
        playerRepository.saveAll();
    }
}