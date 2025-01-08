package com.binggre.mmomail.gui;

import com.binggre.binggreapi.functions.HolderListener;
import com.binggre.binggreapi.utils.EconomyManager;
import com.binggre.binggreapi.utils.ItemManager;
import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.api.MailAPI;
import com.binggre.mmomail.api.MailSendResult;
import com.binggre.mmomail.config.MessageConfig;
import com.binggre.mmomail.objects.Mail;
import com.binggre.mmomail.objects.PlayerMail;
import com.binggre.mmomail.repository.PlayerRepository;
import com.binggre.mmomail.util.MailUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MailSendGUI implements InventoryHolder, HolderListener {

    private final PlayerRepository playerRepository = MMOMail.getInstance().getPlayerRepository();

    private static final int LETTER_SLOT = 0;
    private static final int ITEM_SLOT = 1;
    private static final int GOLD_SLOT = 2;
    private static final int SEND_SLOT = 4;

    public static void open(Player player, String nickname) {
        MailSendGUI mailSendGUI = new MailSendGUI(nickname);
        mailSendGUI.playerMail = MMOMail.getInstance()
                .getPlayerRepository()
                .get(player.getUniqueId());
        mailSendGUI.player = player;
        player.openInventory(mailSendGUI.inventory);
    }

    private final Inventory inventory;
    protected Player player;
    protected String targetName;
    protected PlayerMail playerMail;
    protected List<ItemStack> items = new ArrayList<>();

    private boolean send;

    @Getter
    @Setter
    private double money;
    @Getter
    @Setter
    private String letter;

    private MailSendGUI(String nickname) {
        targetName = nickname;
        inventory = create(nickname);
    }

    private Inventory create(String nickname) {
        String title = String.format("받는 사람 [%s]", nickname);
        Inventory inventory = Bukkit.createInventory(this, InventoryType.HOPPER, Component.text(title));

        inventory.setItem(LETTER_SLOT, ItemManager.create(Material.WRITABLE_BOOK, "&f편지 쓰기"));
        inventory.setItem(ITEM_SLOT, ItemManager.create(Material.CHEST, "&f아이템 보내기"));
        inventory.setItem(GOLD_SLOT, ItemManager.create(Material.GOLD_INGOT, "&f골드 보내기"));
        inventory.setItem(SEND_SLOT, ItemManager.create(Material.PAPER, "&f전송"));
        return inventory;
    }

    public void refresh() {
        refreshLetter();
        refreshGold();
        refreshItem();
    }

    private void refreshLetter() {
        ItemStack item = inventory.getItem(LETTER_SLOT);
        String letter = this.letter;
        List<String> splitLetters = new ArrayList<>();
        if (letter != null) {
            splitLetters = MailUtil.splitLetter(letter, 15);
        }
        inventory.setItem(LETTER_SLOT, ItemManager.setLore(Objects.requireNonNull(item), splitLetters));
    }

    private void refreshGold() {
        ItemStack item = inventory.getItem(GOLD_SLOT);
        String format = String.format("§f%,.0f", money);
        ItemManager.setLore(Objects.requireNonNull(item), List.of(format));
        inventory.setItem(GOLD_SLOT, item);
    }

    private void refreshItem() {
        ItemStack item = inventory.getItem(ITEM_SLOT);

        List<String> lore = new ArrayList<>();
        items.forEach(itemStack -> {
            lore.add("§f" + ItemManager.getDisplayName(itemStack) + " §fx" + itemStack.getAmount());
        });

        ItemManager.setLore(Objects.requireNonNull(item), lore);
    }

    private void send() {
        if (!EconomyManager.hasMoney(player, money)) {
            player.sendMessage(messageConfig().getShortOfMoney());
            return;
        }

        MailAPI mailAPI = MMOMail.getInstance().getMailAPI();
        Mail mail = mailAPI.createMail(playerMail.getNickname(), letter, money, items);

        MailSendResult mailSendResult = mailAPI.sendMail(targetName, mail);
        MessageConfig messageConfig = messageConfig();
        switch (mailSendResult) {
            case NOT_FOUND_PLAYER -> player.sendMessage(messageConfig.getSendNotFoundPlayer());
            case SUCCESS -> player.sendMessage(messageConfig.getSendSuccess().replace("<player>", targetName));
            case ONLINE_AND_NOT_LOAD -> player.sendMessage(messageConfig.getSendOnlineAndNotLoad());
        }
        send = true;
        player.closeInventory();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof MailSendGUI)) {
            return;
        }
        event.setCancelled(true);
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || clickedInventory.getType() == InventoryType.PLAYER) {
            return;
        }
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();

        switch (slot) {
            case LETTER_SLOT -> {
                playerMail.openSign(PlayerMail.SignType.LETTER, this);
                player.closeInventory();
                player.sendMessage(messageConfig().getInputLetter());
            }
            case GOLD_SLOT -> {
                playerMail.openSign(PlayerMail.SignType.GOLD, this);
                player.closeInventory();
                player.sendMessage(messageConfig().getInputGold());
            }
            case ITEM_SLOT -> {
                playerMail.openSign(PlayerMail.SignType.ITEM, this);
                MailItemGUI.open(this);
            }
            case SEND_SLOT -> send();
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof MailSendGUI)) {
            return;
        }
        if (send || playerMail.isSign()) {
            return;
        }

        player.getInventory().addItem(items.toArray(new ItemStack[0]));
    }

    @Override
    public void onDrag(InventoryDragEvent inventoryDragEvent) {

    }

    private MessageConfig messageConfig() {
        return MessageConfig.getInstance();
    }
}