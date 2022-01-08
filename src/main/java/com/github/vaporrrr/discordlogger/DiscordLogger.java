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
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class DiscordLogger extends JavaPlugin {
    private final DiscordSRVListener discordSRV = new DiscordSRVListener(this);
    private final Timer t = new Timer();
    private CommandLogger commandLogger;

    @Override
    public void onEnable() {
        info("Enabling DiscordLogger");
        getConfig().options().copyDefaults(true);
        saveConfig();
        github.scarsz.discordsrv.DiscordSRV.api.subscribe(discordSRV);
        getCommand("dl-reload").setExecutor(new Reload(this));
    }

    @Override
    public void onDisable() {
        info("Disabling DiscordLogger");
    }

    public List<String> getStringList(String path) {
        return getConfig().getStringList(path);
    }

    public String getString(String path) {
        return getConfig().getString(path);
    }

    public boolean getBoolean(String path) {
        return getConfig().getBoolean(path);
    }

    public int getInt(String path) {
        return getConfig().getInt(path);
    }

    public void warning(String message) {
        getLogger().warning(message);
    }

    public void info(String message) {
        getLogger().info(message);
    }

    public void startCommandLogger() {
        commandLogger = new CommandLogger(this, DiscordUtil.getJda(), new ArrayList<>());
        t.scheduleAtFixedRate(commandLogger, 0, interval());
    }

    public void reloadCommandLogger() {
        if (commandLogger != null) {
            commandLogger.cancel();
            commandLogger = new CommandLogger(this, DiscordUtil.getJda(), commandLogger.getQueue());
            t.scheduleAtFixedRate(commandLogger, 0, interval());
        } else {
            startCommandLogger();
        }
    }

    private long interval() {
        return Math.max(getInt("CommandLogger.IntervalInSeconds"), 2) * 1000L;
    }

    public CommandLogger getCommandLogger() {
        return commandLogger;
    }
}
