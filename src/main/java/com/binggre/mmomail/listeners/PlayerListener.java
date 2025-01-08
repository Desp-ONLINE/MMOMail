package com.binggre.mmomail.listeners;

import com.binggre.binggreapi.utils.ItemManager;
import com.binggre.binggreapi.utils.NumberUtil;
import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.api.MailAPI;
import com.binggre.mmomail.config.MessageConfig;
import com.binggre.mmomail.gui.MailSendGUI;
import com.binggre.mmomail.objects.Mail;
import com.binggre.mmomail.objects.PlayerMail;
import com.binggre.mmomail.repository.PlayerRepository;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PlayerListener implements Listener {

    private final PlayerRepository repository = MMOMail.getInstance().getPlayerRepository();
    private final MailAPI mailAPI = MMOMail.getInstance().getMailAPI();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        repository.findByIdAsync(player.getUniqueId(), playerMail -> {
            playerMail = repository.init(player, playerMail);
            playerMail.updateNickname(player);
            Mail mail = mailAPI.createMail("test", "testLetter", 10, List.of(ItemManager.create(Material.DIAMOND, "testItem")));
            playerMail.addMail(mail);
            repository.save(playerMail);
            repository.putIn(playerMail);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerMail removed = repository.remove(event.getPlayer().getUniqueId());
        if (removed != null) {
            repository.saveAsync(removed);
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        PlayerMail playerMail = repository.get(player.getUniqueId());

        if (playerMail == null) {
            return;
        }
        PlayerMail.SignType signType = playerMail.getSignType();
        if (signType == null) {
            return;
        }
        event.setCancelled(true);
        String message = event.signedMessage().message();

        if (message.equals("취소") || message.equalsIgnoreCase("cancel")) {
            playerMail.closeSign();
            return;
        }

        switch (signType) {
            case LETTER -> {
                MailSendGUI mailSendGUI = playerMail.getMailSendGUI();
                mailSendGUI.setLetter(message);
                mailSendGUI.refresh();
            }
            case GOLD -> {
                double gold = NumberUtil.parseDouble(message);
                if (gold == NumberUtil.PARSE_ERROR) {
                    player.sendMessage(MessageConfig.getInstance().getInputErrorNum());
                    return;
                }
                MailSendGUI mailSendGUI = playerMail.getMailSendGUI();
                mailSendGUI.setMoney(gold);
                mailSendGUI.refresh();
            }
        }
        playerMail.closeSign();
    }
}
