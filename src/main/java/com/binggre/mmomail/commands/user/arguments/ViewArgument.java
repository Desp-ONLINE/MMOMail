package com.binggre.mmomail.commands.user.arguments;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.command.annotations.ArgumentOption;
import com.binggre.mmomail.gui.MailCheckGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@ArgumentOption(
        arg = "확인",
        description = "/메일 확인",
        length = 1,
        onlyPlayer = true
)
public class ViewArgument implements CommandArgument {

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        MailCheckGUI.open(((Player) sender));
        return true;
    }
}
