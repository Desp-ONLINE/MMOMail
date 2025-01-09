package com.binggre.mmomail.api;

import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.gui.MailCheckGUI;
import com.binggre.mmomail.listeners.velocity.MailGUIUpdateListener;
import com.binggre.mmomail.objects.Mail;
import com.binggre.mmomail.objects.PlayerMail;
import com.binggre.mmomail.repository.PlayerRepository;
import com.binggre.mmoplayerdata.MMOPlayerDataPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class MailAPIImpl implements MailAPI {

    @Override
    public Mail createMail(String sender, String letter, double money, List<ItemStack> itemStacks) {
        return new Mail(sender, letter, money, itemStacks);
    }

    @Override
    public Mail createMail(String sender, List<String> letter, double money, List<ItemStack> itemStacks) {
        return new Mail(sender, letter, money, itemStacks);
    }

    @Override
    public void updateMailGUI(String targetNickname) {
        Player target = Bukkit.getPlayer(targetNickname);
        if (target != null) {
            Inventory topInventory = target.getOpenInventory().getTopInventory();
            if (topInventory.getHolder() instanceof MailCheckGUI mailCheckGUI) {
                mailCheckGUI.refresh();
            }
        } else {
            MMOMail.getInstance()
                    .getSocketClient()
                    .send(MailGUIUpdateListener.class, targetNickname);
        }
    }

    @Override
    public MailSendResult sendMail(String targetNickname, Mail mail) {
        PlayerRepository playerRepository = MMOMail.getInstance().getPlayerRepository();

        UUID targetUUID = MMOPlayerDataPlugin.getInstance().getPlayerRepository()
                .getUUID(targetNickname);

        if (targetUUID == null) {
            return MailSendResult.NOT_FOUND_PLAYER;
        }

        PlayerMail playerMail = playerRepository.get(targetUUID);
        if (playerMail != null) {
            playerMail.addMail(mail);
            playerRepository.putIn(playerMail);
            playerRepository.save(playerMail);

            updateMailGUI(targetNickname);

        } else {
            playerMail = playerRepository.findByFilter("nickname", targetNickname);
            if (playerMail == null) {
                return MailSendResult.NOT_FOUND_PLAYER;
            }
            playerMail.addMail(mail);
            playerRepository.save(playerMail);
        }
        return MailSendResult.SUCCESS;
    }
}
