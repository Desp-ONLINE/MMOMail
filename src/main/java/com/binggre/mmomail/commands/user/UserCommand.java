package com.binggre.mmomail.commands.user;

import com.binggre.binggreapi.command.BetterCommand;
import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.mmomail.commands.user.arguments.SendArgument;
import com.binggre.mmomail.commands.user.arguments.ViewArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserCommand extends BetterCommand implements TabCompleter {

    @Override
    public String getCommand() {
        return "메일";
    }

    @Override
    public boolean isSingleCommand() {
        return false;
    }

    @Override
    public List<CommandArgument> getArguments() {
        return List.of(
                new SendArgument(),
                new ViewArgument()
        );
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return super.argsMap.keySet().stream().toList();
        }
        return List.of();
    }
}