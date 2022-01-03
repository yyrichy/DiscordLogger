package com.github.vaporrrr.discordlogger;

import com.github.vaporrrr.discordlogger.listeners.DiscordSRVListener;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscordLogger extends JavaPlugin {
    private final DiscordSRVListener discordSRV = new DiscordSRVListener(this);

    @Override
    public void onEnable() {
        getLogger().info("Enabling DiscordLogger");
        getConfig().options().copyDefaults(true);
        saveConfig();
        github.scarsz.discordsrv.DiscordSRV.api.subscribe(discordSRV);
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling DiscordLogger");
    }
}
