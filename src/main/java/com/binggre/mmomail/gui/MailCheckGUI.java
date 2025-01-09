package com.binggre.mmomail.gui;

import com.binggre.binggreapi.functions.HolderListener;
import com.binggre.binggreapi.objects.items.CustomItemStack;
import com.binggre.binggreapi.utils.ItemManager;
import com.binggre.binggreapi.utils.NumberUtil;
import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.config.GUIConfig;
import com.binggre.mmomail.objects.Mail;
import com.binggre.mmomail.objects.PlayerMail;
import com.binggre.mmomail.repository.PlayerRepository;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MailCheckGUI implements InventoryHolder, HolderListener {

    private static final PlayerRepository playerRepository = MMOMail.getInstance().getPlayerRepository();

    private final Player player;
    private final Inventory inventory;
    private int page = 1;
    private final ItemStack airItem = new ItemStack(Material.AIR);

    // slot, mail index
    private final Map<Integer, Integer> mailIndexes = new HashMap<>();

    public static void open(Player player) {
        MailCheckGUI mailCheckGUI = new MailCheckGUI(player);
        player.openInventory(mailCheckGUI.inventory);
        mailCheckGUI.refresh();
    }

    private MailCheckGUI(Player player) {
        this.player = player;
        this.inventory = create();
    }

    private Inventory create() {
        GUIConfig guiConfig = GUIConfig.getInstance();

        TextComponent title = Component.text(guiConfig.getTitle());
        Inventory inventory = Bukkit.createInventory(this, guiConfig.getSize() * 9, title);

        CustomItemStack prev = guiConfig.getPrevItem();
        CustomItemStack next = guiConfig.getNextItem();
        CustomItemStack allReceive = guiConfig.getAllReceiveItem();

        inventory.setItem(prev.getSlot(), prev.getItemStack());
        inventory.setItem(next.getSlot(), next.getItemStack());
        inventory.setItem(allReceive.getSlot(), allReceive.getItemStack());
        return inventory;
    }

    public void refresh() {
        GUIConfig guiConfig = GUIConfig.getInstance();
        PlayerMail playerMail = playerRepository.get(player.getUniqueId());
        List<Mail> mails = playerMail.getMails();
        int total = mails.size();
        int viewSize = guiConfig.getViewSize();
        int startIdx = viewSize * (this.page - 1);
        int endIdx = (startIdx + viewSize);

        mailIndexes.clear();
        for (int index = startIdx; index < endIdx; index++) {
            int slot = index - startIdx;

            inventory.setItem(slot, airItem);
            if (index < total) {
                mailIndexes.put(slot, index);
                Mail mail = mails.get(index);
                CustomItemStack mailItem = guiConfig.getMailItem();
                ItemStack viewItem = mailItem.getItemStack();

                ItemManager.replaceDisplayName(viewItem, "<sender>", mail.getSender());
                ItemManager.replaceLore(viewItem, "<gold>", NumberUtil.applyComma(mail.getMoney()));
                ItemManager.replaceLore(viewItem, "<letter>", mail.getLetter());

                List<ItemStack> itemStacks = mail.getItemStacks();
                ItemManager.removeLore(viewItem, "<items>");
                if (!itemStacks.isEmpty()) {
                    for (ItemStack itemStack : itemStacks) {
                        int amount = itemStack.getAmount();
                        String content = "§7" + ItemManager.getDisplayName(itemStack) + " x" + amount;
                        ItemManager.addLore(viewItem, content);
                    }
                }
                inventory.setItem(slot, viewItem);
            }
        }
    }

    public void nextPage() {
        moveToPage(this.page + 1);
    }

    public void prevPage() {
        if (this.page > 1) {
            moveToPage(this.page - 1);
        }
    }

    private void moveToPage(int page) {
        this.page = page;

        CustomItemStack prevItem = GUIConfig.getInstance().getPrevItem();
        CustomItemStack nextItem = GUIConfig.getInstance().getNextItem();

        ItemStack prev = prevItem.getItemStack();
        ItemStack next = nextItem.getItemStack();

        prev.setAmount(page);
        next.setAmount(page);

        inventory.setItem(prevItem.getSlot(), prev);
        inventory.setItem(nextItem.getSlot(), next);
        refresh();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof MailCheckGUI)) {
            return;
        }
        event.setCancelled(true);

        if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
            return;
        }
        int clickSlot = event.getSlot();
        GUIConfig guiConfig = GUIConfig.getInstance();

        if (clickSlot == guiConfig.getPrevItem().getSlot()) {
            prevPage();
        } else if (clickSlot == guiConfig.getNextItem().getSlot()) {
            nextPage();
        } else if (clickSlot == guiConfig.getAllReceiveItem().getSlot()) {
            PlayerMail playerMail = playerRepository.get(player.getUniqueId());
            List<Mail> mails = playerMail.getMails();
            while (!mails.isEmpty()) {
                if (!playerMail.receive(0)) {
                    break;
                }
            }
            playerRepository.putIn(playerMail);
            playerRepository.saveAsync(playerMail);
            refresh();
        } else {
            if (clickSlot > guiConfig.getViewSize()) {
                return;
            }
            Integer mailIndex = mailIndexes.get(clickSlot);
            if (mailIndex == null) {
                return;
            }
            PlayerMail playerMail = playerRepository.get(event.getWhoClicked().getUniqueId());

            if (playerMail.receive(mailIndex)) {
                playerRepository.putIn(playerMail);
                playerRepository.saveAsync(playerMail);
                refresh();
            }
        }
    }

    @Override
    public void onClose(InventoryCloseEvent inventoryCloseEvent) {

    }

    @Override
    public void onDrag(InventoryDragEvent inventoryDragEvent) {
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
