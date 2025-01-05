package com.binggre.mmomail.gui;

import com.binggre.binggreapi.functions.HolderListener;
import com.binggre.mmomail.MMOMail;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class MailItemGUI implements InventoryHolder, HolderListener {

    public static void open(MailSendGUI mailSendGUI) {
        MailItemGUI mailItemGUI = new MailItemGUI(mailSendGUI);
        mailSendGUI.player.openInventory(mailItemGUI.inventory);
    }

    private final Inventory inventory;
    private final MailSendGUI mailSendGUI;


    private MailItemGUI(MailSendGUI mailSendGUI) {
        this.mailSendGUI = mailSendGUI;
        inventory = create();
    }

    private Inventory create() {
        Inventory inventory = Bukkit.createInventory(this, InventoryType.DROPPER, Component.text("아이템을 올려주세요."));
        mailSendGUI.items.forEach(inventory::addItem);
        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent inventoryClickEvent) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof MailItemGUI)) {
            return;
        }
        mailSendGUI.items.clear();
        event.getInventory().forEach(itemStack -> {
            if (itemStack == null) {
                return;
            }
            mailSendGUI.items.add(itemStack);
        });
        mailSendGUI.refresh();
        Bukkit.getScheduler().runTask(MMOMail.getInstance(), () -> mailSendGUI.player.openInventory(mailSendGUI.getInventory()));
    }

    @Override
    public void onDrag(InventoryDragEvent inventoryDragEvent) {

    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
