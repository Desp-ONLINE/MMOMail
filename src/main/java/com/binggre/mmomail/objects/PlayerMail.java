package com.binggre.mmomail.objects;

import com.binggre.binggreapi.utils.EconomyManager;
import com.binggre.binggreapi.utils.InventoryManager;
import com.binggre.mmomail.config.MessageConfig;
import com.binggre.mongolibraryplugin.base.MongoData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class PlayerMail implements MongoData<UUID> {

    private final UUID id;
    private String nickname;
    private final List<Mail> mails;

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

    public boolean receive(int index) {
        Player player = toPlayer();
        if (player == null) {
            throw new NullPointerException("플레이어가 오프라인입니다.");
        }
        Mail mail = mails.get(index);
        List<ItemStack> itemStacks = mail.getItemStacks();
        if (!itemStacks.isEmpty()) {
            int size = itemStacks.size();
            if (!InventoryManager.hasEmpty(player, size)) {
                int emptySize = (int) Arrays.stream(player.getInventory().getStorageContents()).filter(Objects::isNull).count();
                int has = size - emptySize;
                String inventoryCheck = MessageConfig.getInstance().getInventoryCheck();
                player.sendMessage(inventoryCheck.replace("<size>", String.valueOf(has)));
                return false;
            }
            player.getInventory().addItem(itemStacks.toArray(new org.bukkit.inventory.ItemStack[0]));
        }
        EconomyManager.addMoney(player, mail.getMoney());
        mails.remove(index);
        return true;
    }

    @Nullable
    public Player toPlayer() {
        return Bukkit.getPlayer(id);
    }
}