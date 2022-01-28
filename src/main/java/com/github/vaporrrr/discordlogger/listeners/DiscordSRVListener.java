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

package com.github.vaporrrr.discordlogger.listeners;

import com.github.vaporrrr.discordlogger.DiscordLogger;
import com.github.vaporrrr.discordlogger.util.MessageUtil;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;

public class DiscordSRVListener {
    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        DiscordLogger.info("Discord ready!");
        DiscordLogger.getPlugin().getServer().getPluginManager().registerEvents(new BukkitListener(), DiscordLogger.getPlugin());
        DiscordLogger.getPlugin().startCommandLogger();
    }

    @Subscribe
    public void accountsLinked(AccountLinkedEvent event) {
        if (!DiscordLogger.config().getBoolean("Link.Enabled")) return;
        try {
            MessageUtil.sendMessageFromConfig("Link.ChannelID", (event.getPlayer().getName() != null ? event.getPlayer().getName() + " " : "") + "(" + event.getPlayer().getUniqueId() + ") linked their Discord: "
                    + event.getUser().getAsTag() + " (" + event.getUser().getId() + ")");
        } catch (RuntimeException e) {
            DiscordLogger.severe("Could not log account linked event: " + e.getMessage());
        }
    }

    @Subscribe
    public void accountsUnlinked(AccountUnlinkedEvent event) {
        if (!DiscordLogger.config().getBoolean("Unlink.Enabled")) return;
        try {
            MessageUtil.sendMessageFromConfig("Unlink.ChannelID", (event.getPlayer().getName() != null ? event.getPlayer().getName() + " " : "") + "    (" + event.getPlayer().getUniqueId() + ") unlinked their Discord: "
                    + (event.getDiscordUser() != null ? event.getDiscordUser().getAsTag() : "<Discord tag unavailable>") + " (" + event.getDiscordId() + ")");
        } catch (RuntimeException e) {
            DiscordLogger.severe("Could not log account unlinked event: " + e.getMessage());
        }
    }
}
