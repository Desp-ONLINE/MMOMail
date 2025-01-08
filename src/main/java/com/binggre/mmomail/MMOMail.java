package com.binggre.mmomail;

import com.binggre.binggreapi.BinggrePlugin;
import com.binggre.mmomail.api.MailAPI;
import com.binggre.mmomail.api.MailAPIImpl;
import com.binggre.mmomail.commands.UserCommand;
import com.binggre.mmomail.config.MessageConfig;
import com.binggre.mmomail.listeners.MerchantTradeListener;
import com.binggre.mmomail.listeners.PlayerListener;
import com.binggre.mmomail.objects.Mail;
import com.binggre.mmomail.objects.PlayerMail;
import com.binggre.mmomail.repository.PlayerRepository;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public final class MMOMail extends BinggrePlugin implements CommandExecutor {

    @Getter
    private static MMOMail instance;
    public static final String DATA_BASE_NAME = "MMO-Mail";

    private PlayerRepository playerRepository;
    private MailAPI mailAPI;

    @Override
    public void onEnable() {
        instance = this;
        mailAPI = new MailAPIImpl();
        playerRepository = new PlayerRepository(
                this,
                DATA_BASE_NAME,
                "Player",
                null);

        executeCommand(this, new UserCommand());
        registerEvents(this,
                new PlayerListener(),
                new MerchantTradeListener()
        );

        playerRepository.init();
        MessageConfig.getInstance().init();
        getCommand("test").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        PlayerMail playerMail = playerRepository.get(player.getUniqueId());
        for (Mail mail : playerMail.getMails()) {
            for (ItemStack itemStack : mail.getItemStacks()) {
                player.getInventory().addItem(itemStack);
            }
        }
        return super.onCommand(sender, command, label, args);
    }

    @Override
    public void onDisable() {
        playerRepository.saveAll();
    }
}