/*
 * DiscordLogger
 * Copyright 2022 (C) vaporrrr
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.vaporrrr.discordlogger;

import com.github.vaporrrr.discordlogger.commands.Reload;
import com.github.vaporrrr.discordlogger.listeners.DiscordSRVListener;
import com.github.vaporrrr.discordlogger.threads.CommandLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayDeque;
import java.util.Timer;

public class DiscordLogger extends JavaPlugin {
    private FileConfiguration config;
    private final DiscordSRVListener discordSRV = new DiscordSRVListener();
    private final Timer t = new Timer();
    private CommandLogger commandLogger;

    public DiscordLogger() {
        super();
    }

    @Override
    public void onEnable() {
        info("Enabling DiscordLogger");
        getConfig().options().copyDefaults(true);
        saveConfig();
        config = getConfig();
        github.scarsz.discordsrv.DiscordSRV.api.subscribe(discordSRV);
        getCommand("dl-reload").setExecutor(new Reload());
    }

    @Override
    public void onDisable() {
        info("Disabling DiscordLogger");
    }

    public static DiscordLogger getPlugin() {
        return getPlugin(DiscordLogger.class);
    }

    public CommandLogger getCommandLogger() {
        return commandLogger;
    }

    public static FileConfiguration config() {
        return getPlugin().config;
    }

    public void startCommandLogger() {
        commandLogger = new CommandLogger(new ArrayDeque<>());
        t.scheduleAtFixedRate(commandLogger, 0, interval());
    }

    public void reloadCommandLogger() {
        if (commandLogger != null) {
            commandLogger.cancel();
            commandLogger = new CommandLogger(commandLogger.getQueue());
            t.scheduleAtFixedRate(commandLogger, 0, interval());
        } else {
            startCommandLogger();
        }
    }

    private long interval() {
        int interval = config().getInt("CommandLogger.IntervalInSeconds");
        if (interval < 2) {
            warn("CommandLogger.IntervalInSeconds is set to less than 2 seconds. This is not allowed to prevent rate limiting. Setting interval to 2.");
            interval = 2;
            config().set("CommandLogger.IntervalInSeconds", interval);
        }
        return interval * 1000L;
    }

    public static void info(String message) {
        getPlugin().getLogger().info(message);
    }

    public static void warn(String message) {
        getPlugin().getLogger().warning(message);
    }

    public static void severe(String message) {
        getPlugin().getLogger().severe(message);
    }
}
