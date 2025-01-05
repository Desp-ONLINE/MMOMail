package com.binggre.mmomail.objects;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MailItem  {

    private final List<ItemStack> itemStacks;

    public MailItem() {
        itemStacks = new ArrayList<>();
    }
}