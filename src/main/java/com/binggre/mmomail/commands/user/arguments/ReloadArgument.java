package com.binggre.mmomail.commands.user.arguments;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.mmomail.config.GUIConfig;
import com.binggre.mmomail.config.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadArgument implements CommandArgument {

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        MessageConfig.getInstance().init();
        GUIConfig.getInstance().init();
        return true;
    }

    @Override
    public String getArg() {
        return "리로드";
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "- config를 리로드합니다.";
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getPermissionMessage() {
        return "";
    }

    @Override
    public boolean onlyPlayer() {
        return false;
    }
}
