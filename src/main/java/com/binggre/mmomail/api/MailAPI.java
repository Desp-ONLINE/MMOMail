package com.binggre.mmomail.api;

import com.binggre.mmomail.objects.Mail;
import org.bukkit.inventory.ItemStack;

import java.time.format.DateTimeFormatter;
import java.util.List;

public interface MailAPI {

    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    Mail createMail(String sender, String letter, double money, List<ItemStack> itemStacks);

    MailSendResult sendMail(String targetNickname, Mail mail);
}