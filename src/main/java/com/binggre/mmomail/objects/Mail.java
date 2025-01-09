package com.binggre.mmomail.objects;

import com.binggre.binggreapi.utils.ItemManager;
import com.binggre.mmomail.util.MailUtil;
import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class Mail {

    private final String sender;
    private final List<String> letter;
    private final double money;
    private boolean read;

    @Getter(AccessLevel.PRIVATE)
    @SerializedName("items")
    private final List<byte[]> serializedItemStacks;
    private final LocalDateTime date;

    public Mail(String sender, String letter, double money, List<ItemStack> itemStacks) {
        this.sender = sender;
        this.letter = MailUtil.splitLetter(letter, 15);
        this.money = money;
        this.date = LocalDateTime.now();
        this.serializedItemStacks = new ArrayList<>();
        this.read = false;
        itemStacks.forEach(itemStack -> serializedItemStacks.add(itemStack.serializeAsBytes()));
    }

    public Mail(String sender, List<String> letter, double money, List<ItemStack> itemStacks) {
        this.sender = sender;
        this.letter = letter;
        this.money = money;
        this.date = LocalDateTime.now();
        this.serializedItemStacks = new ArrayList<>();
        this.read = false;
        itemStacks.forEach(itemStack -> serializedItemStacks.add(itemStack.serializeAsBytes()));
    }

    public void read() {
        this.read = true;
    }

    public List<ItemStack> getItemStacks() {
        if (serializedItemStacks == null || serializedItemStacks.isEmpty()) {
            return new ArrayList<>();
        }

        List<ItemStack> items = new ArrayList<>();
        for (byte[] serializedItemStack : serializedItemStacks) {
            items.add(ItemStack.deserializeBytes(serializedItemStack));
        }
        ItemManager.merge(items);
        items.removeIf(Objects::isNull);
        return items;
    }
}