package com.binggre.mmomail.api;

import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.objects.Mail;
import com.binggre.mmomail.objects.PlayerMail;
import com.binggre.mmomail.repository.PlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class MailAPIImpl implements MailAPI {

    @Override
    public Mail createMail(String sender, String letter, double money, List<ItemStack> itemStacks) {
        return new Mail(sender, letter, money, itemStacks);
    }

    @Override
    public MailSendResult sendMail(String targetNickname, Mail mail) {
        Player target = Bukkit.getPlayer(targetNickname);
        PlayerRepository playerRepository = MMOMail.getInstance().getPlayerRepository();

        if (target != null) {
            UUID uniqueId = target.getUniqueId();
            PlayerMail playerMail = playerRepository.get(uniqueId);
            if (playerMail == null) {
                return MailSendResult.ONLINE_AND_NOT_LOAD;
            }
            playerMail.addMail(mail);
            playerRepository.saveAsync(playerMail);

        } else {
            PlayerMail playerMail = playerRepository.findByFilter("nickname", targetNickname);
            if (playerMail == null) {
                return MailSendResult.NOT_FOUND_PLAYER;
            }
            playerMail.addMail(mail);
            playerRepository.saveAsync(playerMail);
        }
        return MailSendResult.SUCCESS;
    }
}
