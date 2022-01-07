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
import com.github.vaporrrr.discordlogger.threads.LogCommands;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Timer;

public class DiscordLogger extends JavaPlugin {
    private final DiscordSRVListener discordSRV = new DiscordSRVListener(this);
    private final Timer t = new Timer();
    private LogCommands logCommands;

    @Override
    public void onEnable() {
        getLogger().info("Enabling DiscordLogger");
        getConfig().options().copyDefaults(true);
        saveConfig();
        github.scarsz.discordsrv.DiscordSRV.api.subscribe(discordSRV);
        getCommand("dl-reload").setExecutor(new Reload(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling DiscordLogger");
    }

    public void startLogCommands() {
        logCommands = new LogCommands(this, DiscordUtil.getJda(), new ArrayList<>());
        t.scheduleAtFixedRate(logCommands, 0, interval());
    }

    public void reloadLogCommands() {
        if (logCommands != null) {
            logCommands.cancel();
            logCommands = new LogCommands(this, DiscordUtil.getJda(), logCommands.getQueue());
            t.scheduleAtFixedRate(logCommands, 0, interval());
        } else {
            startLogCommands();
        }
    }

    private long interval() {
        return Math.max(getConfig().getInt("CommandLog.IntervalInSeconds"), 2) * 1000L;
    }

    public LogCommands getLogCommands() {
        return logCommands;
    }
}
