package com.github.vaporrrr.discordlogger.threads;

import com.github.vaporrrr.discordlogger.DiscordLogger;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.TimerTask;

public class LogCommands extends TimerTask {
    private final StringBuilder message = new StringBuilder();
    private final ArrayList<String> queue = new ArrayList<>();
    private final DiscordLogger discordLogger;
    private final JDA jda;

    public LogCommands(DiscordLogger discordLogger, JDA jda) {
        this.discordLogger = discordLogger;
        this.jda = jda;
    }

    @Override
    public void run() {
        if (queue.size() == 0) return;
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
        channel.sendMessage(message.toString()).queue();
    }

    public void add(String m) {
        queue.add(m);
    }
}
