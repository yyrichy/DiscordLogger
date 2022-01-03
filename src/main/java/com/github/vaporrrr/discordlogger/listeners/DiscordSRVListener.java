package com.github.vaporrrr.discordlogger.listeners;

import com.github.vaporrrr.discordlogger.DiscordLogger;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.util.DiscordUtil;

public class DiscordSRVListener {
    private final DiscordLogger discordLogger;

    public DiscordSRVListener(DiscordLogger discordLogger) {
        this.discordLogger = discordLogger;
    }

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        discordLogger.getLogger().info("Discord ready!");
        discordLogger.getServer().getPluginManager().registerEvents(new BukkitListener(discordLogger, DiscordUtil.getJda()), discordLogger);
    }
}
