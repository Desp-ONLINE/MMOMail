package com.binggre.mmomail.listeners;

import com.binggre.binggreapi.utils.ItemManager;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

public class MerchantTradeListener implements Listener {

    @EventHandler
    public void onClick(TradeSelectEvent event) {
        event.setCancelled(true);
        MerchantRecipe recipe = event.getMerchant().getRecipe(event.getIndex());
        ItemStack result = recipe.getResult();
        System.out.println(result);

        event.setResult(Event.Result.DENY);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory instanceof MerchantInventory)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory instanceof MerchantInventory merchantInventory)) {
            return;
        }
        merchantInventory.setItem(0, ItemManager.create(Material.ACACIA_BUTTON, "§f모두 수령"));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory instanceof MerchantInventory)) {
            return;
        }
    }
}
