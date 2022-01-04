package com.github.vaporrrr.discordlogger.commands;

import com.github.vaporrrr.discordlogger.DiscordLogger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {
    private final DiscordLogger discordLogger;

    public Reload(DiscordLogger discordLogger) {
        this.discordLogger = discordLogger;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("dl.admin.reload") && !commandSender.isOp()) {
            commandSender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use that command.");
            return true;
        }
        discordLogger.reloadConfig();
        commandSender.sendMessage("Config reloaded.");
        return true;
    }
}
