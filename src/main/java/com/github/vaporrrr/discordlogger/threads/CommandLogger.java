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

package com.github.vaporrrr.discordlogger.threads;

import com.github.vaporrrr.discordlogger.DiscordLogger;
import com.github.vaporrrr.discordlogger.util.MessageUtil;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.text.SimpleDateFormat;
import java.util.*;

public class CommandLogger extends TimerTask {
    private final StringBuilder message = new StringBuilder();
    private final ArrayDeque<String> queue;

    public CommandLogger(ArrayDeque<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        if (queue == null || queue.isEmpty()) return;
        message.setLength(0);
        while (!queue.isEmpty()) {
            String m = queue.getFirst();
            if (message.length() + m.length() > Message.MAX_CONTENT_LENGTH) {
                break;
            }
            message.append(m).append('\n');
            queue.removeFirst();
        }
        try {
            MessageUtil.sendMessageFromConfig(DiscordLogger.config().getString("CommandLogger.ChannelID"), message.toString());
        } catch (RuntimeException e) {
            DiscordLogger.severe("Could not log commands to channel from key CommandLogger.ChannelID");
            e.printStackTrace();
        }
    }

    public void processCommand(PlayerCommandPreprocessEvent event) {
        String timeZone = DiscordLogger.config().getString("CommandLogger.TimeZone");
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Player player = event.getPlayer();

        List<String> message = DiscordLogger.config().getStringList("CommandLogger.Format");
        for (ListIterator<String> iterator = message.listIterator(); iterator.hasNext(); ) {
            String line = iterator.next();
            line = line.replace("$time$", format.format(now));
            line = line.replace("$timezone$", timeZone);
            line = line.replace("$UUID$", player.getUniqueId().toString());
            line = line.replace("$username$", escapeMarkdown(player.getName()));
            line = line.replace("$message$", escapeMarkdown(event.getMessage()));
            iterator.set(line);
        }
        queue.add(String.join("\n", message));
    }

    public ArrayDeque<String> getQueue() {
        return queue;
    }

    private String escapeMarkdown(String message) {
        return message.replace("_", "\\_").replace("*", "\\*").replace("`", "\\`").replace("~", "\\~").replace("|", "\\|").replace(">", "\\>");
    }
}
