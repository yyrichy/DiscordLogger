package com.github.vaporrrr.discordlogger.util;

import com.github.vaporrrr.discordlogger.DiscordLogger;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.PlaceholderUtil;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

public class Placeholder {
    public static String replacePlaceholders(String message, PlayerCommandPreprocessEvent event) {
        String timeZone = DiscordLogger.config().getString("CommandLogger.TimeZone");
        String finalTimeZone = timeZone;
        if (Arrays.stream(TimeZone.getAvailableIDs()).noneMatch(t -> t.equals(finalTimeZone))) {
            DiscordLogger.warn("CommandLogger.TimeZone does not match a valid TimeZone ID, setting to UTC");
            DiscordLogger.config().set("CommandLogger.TimeZone", "UTC");
            timeZone = "UTC";
        }
        String pattern = "HH:mm:ss yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));

        String date = simpleDateFormat.format(new Date());

        message = message.replace("%time%", date);
        message = message.replace("%timezone%", timeZone);
        message = message.replace("%UUID%", event.getPlayer().getUniqueId().toString());
        message = message.replace("%username_escape_markdown%", DiscordUtil.escapeMarkdown(event.getPlayer().getName()));
        message = message.replace("%message_escape_markdown%", DiscordUtil.escapeMarkdown(event.getMessage()));
        message = PlaceholderUtil.replacePlaceholdersToDiscord(message, event.getPlayer());
        return message;
    }
}
