package com.binggre.mmomail.listeners.velocity;

import com.binggre.mmomail.gui.MailCheckGUI;
import com.binggre.velocitysocketclient.listener.VelocitySocketListener;
import com.binggre.velocitysocketclient.socket.SocketResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class MailGUIUpdateListener extends VelocitySocketListener {

    @Override
    public void onReceive(String[] strings) {
        Player player = Bukkit.getPlayer(strings[0]);
        if (player == null) {
            return;
        }
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        if (topInventory.getHolder() instanceof MailCheckGUI mailCheckGUI) {
            mailCheckGUI.refresh();
        }
    }

    @Override
    public @NotNull SocketResponse onRequest(String... strings) {
        return null;
    }

    @Override
    public void onResponse(SocketResponse socketResponse) {

    }
}
