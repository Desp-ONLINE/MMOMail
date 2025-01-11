package com.binggre.mmomail.commands.user.arguments;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.mmomail.gui.MailCheckGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ViewArgument implements CommandArgument {

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        MailCheckGUI.open(((Player) sender));
        return true;
    }

    @Override
    public String getArg() {
        return "확인";
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getPermission() {
        return "mmomail.view";
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
