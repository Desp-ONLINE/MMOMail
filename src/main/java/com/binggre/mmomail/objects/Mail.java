package com.binggre.mmomail.objects;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Mail {

    private final String sender;
    private final String title;
    private final String content;
    private final double money;
    private final MailItem mailItem;
    private final LocalDateTime date;

    public Mail(String sender, String title, double money, String content) {
        this.sender = sender;
        this.title = title;
        this.content = content;
        this.money = money;
        this.date = LocalDateTime.now();
        this.mailItem = new MailItem();
    }
}