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
import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayDeque;

public class DiscordLogger extends JavaPlugin {
    private final Config config;
    private final DiscordSRVListener discordSRV = new DiscordSRVListener();
    @Getter
    private CommandLogger commandLogger;

    public DiscordLogger() {
        super();
        InputStream is = getClassLoader().getResourceAsStream("config.yml");
        config = LightningBuilder
                .fromFile(new File(getDataFolder(), "config.yml"))
                .addInputStream(is)
                .setDataType(DataType.SORTED)
                .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                .setReloadSettings(ReloadSettings.MANUALLY)
                .createConfig()
                .addDefaultsFromInputStream(is);
    }

    public static DiscordLogger getPlugin() {
        return getPlugin(DiscordLogger.class);
    }

    public static Config config() {
        return getPlugin().config;
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

    @Override
    public void onEnable() {
        info("Enabling DiscordLogger");
        github.scarsz.discordsrv.DiscordSRV.api.subscribe(discordSRV);
        getCommand("dl-reload").setExecutor(new Reload());
    }

    @Override
    public void onDisable() {
        info("Disabling DiscordLogger");
    }

    public void reloadConfig() {
        config().forceReload();
    }

    public void startCommandLogger() {
        getPlugin().commandLogger = new CommandLogger(new ArrayDeque<>());
        getPlugin().commandLogger.start();
    }

    public void reloadCommandLogger() {
        if (getPlugin().commandLogger == null) {
            startCommandLogger();
        } else {
            if (!getPlugin().commandLogger.isInterrupted()) getPlugin().commandLogger.interrupt();
            getPlugin().commandLogger = new CommandLogger(getPlugin().commandLogger.getQueue());
            getPlugin().commandLogger.start();
        }
    }
}
