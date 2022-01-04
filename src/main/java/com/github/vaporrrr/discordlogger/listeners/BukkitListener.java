package com.github.vaporrrr.discordlogger.listeners;

import com.github.vaporrrr.discordlogger.DiscordLogger;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class BukkitListener implements Listener {
    private final DiscordLogger discordLogger;
    private final JDA jda;

    public BukkitListener(DiscordLogger discordLogger, JDA jda) {
        this.discordLogger = discordLogger;
        this.jda = jda;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!discordLogger.getConfig().getBoolean("CommandLog.Enabled")) return;
        FileConfiguration config = discordLogger.getConfig();
        TextChannel channel = jda.getTextChannelById(config.getString("CommandLog.ChannelID"));
        if (channel == null) {
            discordLogger.getLogger().warning("Could not log commands because TextChannel not not found from CommandLog.ChannelID");
            return;
        }
        String timeZone = config.getString("CommandLog.TimeZone");
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Player player = event.getPlayer();

        String message = discordLogger.getConfig().getString("CommandLog.Format");
        message = message.replace("$time$", format.format(now));
        message = message.replace("$timezone$", timeZone);
        message = message.replace("$UUID$", player.getUniqueId().toString());
        message = message.replace("$username$", player.getName());
        message = message.replace("$message$", event.getMessage());
        channel.sendMessage(message).queue();
    }
}
