package com.binggre.mmomail.commands.user.arguments;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.mmomail.gui.MailSendGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SendArgument implements CommandArgument {

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        MailSendGUI.open(((Player) sender), args[1]);
        return true;
    }

    @Override
    public String getArg() {
        return "전송";
    }

    @Override
    public int length() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "[닉네임]";
    }

    @Override
    public String getPermission() {
        return "mmomail.send";
    }

    @Override
    public String getPermissionMessage() {
        return "§c권한이 없습니다.";
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }
}