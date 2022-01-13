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
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.text.SimpleDateFormat;
import java.util.*;

public class CommandLogger extends TimerTask {
    private final String COMMAND_LOGGER_NAME = "CommandLogger";
    private final StringBuilder message = new StringBuilder();
    private final ArrayList<String> queue;
    private final JDA jda;

    public CommandLogger(JDA jda, ArrayList<String> queue) {
        this.jda = jda;
        this.queue = queue;
    }

    @Override
    public void run() {
        if (queue == null || queue.isEmpty()) return;
        message.setLength(0);
        TextChannel channel = jda.getTextChannelById(DiscordLogger.config().getString(COMMAND_LOGGER_NAME + ".ChannelID"));
        if (channel == null) {
            DiscordLogger.warn("Could not log commands because TextChannel not not found from " + COMMAND_LOGGER_NAME + ".ChannelID");
            return;
        }
        while (!queue.isEmpty()) {
            String m = queue.get(0);
            if (message.length() + m.length() > Message.MAX_CONTENT_LENGTH) {
                break;
            }
            message.append(m).append('\n');
            queue.remove(0);
        }
        channel.sendMessage(message.toString()).queue();
    }

    public void processCommand(PlayerCommandPreprocessEvent event) {
        if (!DiscordLogger.config().getBoolean(COMMAND_LOGGER_NAME + ".Enabled")) return;
        String timeZone = DiscordLogger.config().getString(COMMAND_LOGGER_NAME + ".TimeZone");
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Player player = event.getPlayer();

        List<String> message = DiscordLogger.config().getStringList(COMMAND_LOGGER_NAME + ".Format");
        for (ListIterator<String> iterator = message.listIterator(); iterator.hasNext(); ) {
            String line = iterator.next();
            line = line.replace("$time$", format.format(now));
            line = line.replace("$timezone$", timeZone);
            line = line.replace("$UUID$", player.getUniqueId().toString());
            line = line.replace("$username$", player.getName());
            line = line.replace("$message$", escapeMarkdown(event.getMessage()));
            iterator.set(line);
        }
        queue.add(String.join("\n", message));
    }

    public ArrayList<String> getQueue() {
        return queue;
    }

    private String escapeMarkdown(String message) {
        return message.replace("_", "\\_").replace("*", "\\*").replace("`", "\\`").replace("~", "\\~").replace("|", "\\|").replace(">", "\\>");
    }
}
