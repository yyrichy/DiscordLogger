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
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.TimerTask;

public class LogCommands extends TimerTask {
    private final StringBuilder message = new StringBuilder();
    private final ArrayList<String> queue;
    private final DiscordLogger discordLogger;
    private final JDA jda;

    public LogCommands(DiscordLogger discordLogger, JDA jda, ArrayList<String> queue) {
        this.discordLogger = discordLogger;
        this.jda = jda;
        this.queue = queue;
    }

    @Override
    public void run() {
        if (queue == null || queue.isEmpty()) return;
        message.setLength(0);
        FileConfiguration config = discordLogger.getConfig();
        TextChannel channel = jda.getTextChannelById(config.getString("CommandLog.ChannelID"));
        if (channel == null) {
            discordLogger.getLogger().warning("Could not log commands because TextChannel not not found from CommandLog.ChannelID");
            return;
        }
        while (!queue.isEmpty()) {
            String m = queue.get(0);
            if (message.length() + m.length() > 2000) {
                break;
            }
            message.append(m).append('\n');
            queue.remove(0);
        }
        if (discordLogger.getConfig().getBoolean("CommandLog.DisableMentions")) {
            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.denyMentions(Message.MentionType.EVERYONE, Message.MentionType.HERE, Message.MentionType.ROLE, Message.MentionType.USER);
            messageBuilder.append(message.toString());
            channel.sendMessage(messageBuilder.build()).queue();
        } else {
            channel.sendMessage(message.toString()).queue();
        }
    }

    public void add(String m) {
        queue.add(m);
    }

    public ArrayList<String> getQueue() {
        return queue;
    }
}
