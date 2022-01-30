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
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;

public class CommandLogger extends Thread {
    private final StringBuilder message = new StringBuilder();
    @Getter
    private final ArrayDeque<String> queue;

    public CommandLogger(ArrayDeque<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (!interrupted()) {
                if (queue == null || queue.isEmpty()) continue;
                message.setLength(0);
                int interval = DiscordLogger.config().getInt("CommandLogger.IntervalInSeconds");
                if (interval < 2) {
                    DiscordLogger.warn("CommandLogger.IntervalInSeconds is set to below 2 seconds, overriding to a 2 second interval");
                    DiscordLogger.config().set("CommandLogger.IntervalInSeconds", 2);
                    interval = 2;
                }
                while (!queue.isEmpty()) {
                    String m = queue.getFirst();
                    if (message.length() + m.length() > Message.MAX_CONTENT_LENGTH) {
                        break;
                    }
                    message.append(m).append('\n');
                    queue.removeFirst();
                }
                try {
                    MessageUtil.sendMessageFromConfig("CommandLogger.ChannelID", message.toString());
                } catch (RuntimeException e) {
                    DiscordLogger.severe("Could not log commands to channel from key CommandLogger.ChannelID: " + e.getMessage());
                }
                Thread.sleep(TimeUnit.SECONDS.toMillis(interval));
            }
        } catch (InterruptedException ignored) {
        }
    }

    public void add(String message) {
        queue.add(message);
    }
}
