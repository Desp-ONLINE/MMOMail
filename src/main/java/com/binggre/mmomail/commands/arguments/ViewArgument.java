package com.binggre.mmomail.commands.arguments;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.command.annotations.ArgumentOption;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@ArgumentOption(
        arg = "확인",
        description = "/메일 확인",
        length = 1
)
public class ViewArgument implements CommandArgument {

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return false;
    }
}
