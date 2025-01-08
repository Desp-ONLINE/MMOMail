package com.binggre.mmomail.gui;

import com.binggre.binggreapi.utils.ItemManager;
import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.objects.Mail;
import com.binggre.mmomail.objects.PlayerMail;
import com.binggre.mmomail.repository.PlayerRepository;
import com.binggre.mmomail.util.MailUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class MailCheckGUI {

    private static final PlayerRepository playerRepository = MMOMail.getInstance().getPlayerRepository();

    private final Player player;
    private final Merchant merchant;

    public static void open(Player player) {
        MailCheckGUI mailCheckGUI = new MailCheckGUI(player);
        player.openMerchant(mailCheckGUI.merchant, true);
    }

    private MailCheckGUI(Player player) {
        this.player = player;
        this.merchant = create();
    }

    private Merchant create() {
        TextComponent title = Component.text("메일함");
        Merchant merchant = Bukkit.createMerchant(title);

        PlayerMail playerMail = playerRepository.get(player.getUniqueId());
        List<MerchantRecipe> recipes = new ArrayList<>();

        ItemStack receiveButton = ItemManager.create(Material.OAK_SIGN, "§a수령");
        for (Mail mail : playerMail.getMails()) {

            MerchantRecipe recipe = new MerchantRecipe(receiveButton, 0);

            String mailTitle = "§f보낸 사람 : " + mail.getSender();
            ItemStack firstItem = ItemManager.create(Material.PAPER, mailTitle, List.of("§f골드 : " + mail.getMoney()));
            ItemStack letterItem = ItemManager.create(Material.WRITABLE_BOOK, "§e편지", MailUtil.splitLetter(mail.getLetter(), 15));
            List<ItemStack> itemStacks = mail.getItemStacks();
            if (itemStacks.isEmpty()) {
                recipe.addIngredient(firstItem);
            } else {
                firstItem = firstItem.withType(Material.CHEST);
                List<String> listLore = new ArrayList<>();
                for (ItemStack itemStack : itemStacks) {
                    int amount = itemStack.getAmount();
                    listLore.add("§7" + ItemManager.getDisplayName(itemStack) + " x" + amount);
                }
                ItemManager.addLore(firstItem, listLore);
                recipe.addIngredient(firstItem);
            }
            recipe.addIngredient(letterItem);
            recipes.add(recipe);
        }
        merchant.setRecipes(recipes);

        return merchant;
    }
}
