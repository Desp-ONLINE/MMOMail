package com.binggre.mmomail.commands.arguments;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.command.annotations.ArgumentOption;
import com.binggre.mmomail.gui.MailSendGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@ArgumentOption(
        arg = "전송",
        description = "/메일 전송 <닉네임>",
        length = 2,
        onlyPlayer = true
)
public class SendArgument implements CommandArgument {

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        MailSendGUI.open(((Player) sender), args[1]);
        return true;
    }
}