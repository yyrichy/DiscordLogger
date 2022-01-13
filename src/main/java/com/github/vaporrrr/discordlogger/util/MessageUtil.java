package com.github.vaporrrr.discordlogger.util;

import com.github.vaporrrr.discordlogger.DiscordLogger;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;

public class MessageUtil {
    public static <T> void sendMessageFromConfig(String channelKey, T content) {
        TextChannel channel = DiscordUtil.getJda().getTextChannelById(DiscordLogger.config().getString(channelKey));
        if (channel != null) {
            if (content instanceof String) {
                channel.sendMessage((String) content).queue();
                return;
            } else if (content instanceof MessageEmbed) {
                channel.sendMessage((MessageEmbed) content).queue();
                return;
            }
            throw new IllegalArgumentException("Content is not an instance of String or MessageEmbed.");
        }
        throw new RuntimeException("Unable to find Discord TextChannel from " + channelKey + ".");
    }
}
