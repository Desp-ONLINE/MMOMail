package com.binggre.mmomail.objects;

import com.binggre.mmomail.gui.MailSendGUI;
import com.binggre.mongolibraryplugin.base.MongoData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class PlayerMail implements MongoData<UUID> {

    private final UUID id;
    private String nickname;
    private final List<Mail> mails;

    private transient SignType signType;
    private transient MailSendGUI mailSendGUI;

    public PlayerMail(Player player) {
        id = player.getUniqueId();
        nickname = player.getName();
        mails = new ArrayList<>();
    }

    @Override
    public UUID getId() {
        return id;
    }

    public void updateNickname(Player player) {
        if (player.getUniqueId() != this.id) return;
        if (player.getName().equals(nickname)) return;
        this.nickname = player.getName();
    }

    public void addMail(Mail mail) {
        this.mails.add(mail);
    }

    @Nullable
    public Player toPlayer() {
        return Bukkit.getPlayer(id);
    }


    public boolean isSign() {
        return signType != null || mailSendGUI != null;
    }

    public void closeSign() {
        Player player = toPlayer();
        if (player != null) {
            player.openInventory(mailSendGUI.getInventory());
        }
        this.signType = null;
        this.mailSendGUI = null;
    }

    public void openSign(SignType signType, MailSendGUI mailSendGUI) {
        this.signType = signType;
        this.mailSendGUI = mailSendGUI;
    }

    public enum SignType {

        LETTER, GOLD, ITEM

    }
}