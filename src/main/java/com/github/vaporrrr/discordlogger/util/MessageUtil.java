package com.github.vaporrrr.discordlogger.util;

import com.github.vaporrrr.discordlogger.DiscordLogger;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.PermissionException;
import github.scarsz.discordsrv.util.DiscordUtil;

public class MessageUtil {
    public static <T> void sendMessageFromConfig(String channelKey, T content) {
        String channelID = DiscordLogger.config().getString(channelKey);
        if (channelID == null || channelID.isEmpty()) {
            throw new IllegalArgumentException(channelKey + " is not set in config");
        }
        TextChannel channel = DiscordUtil.getJda().getTextChannelById(channelID);
        if (channel == null) {
            throw new RuntimeException("Unable to find Discord TextChannel from " + channelKey);
        }
        if (!channel.canTalk()) {
            throw new PermissionException("Cannot talk in channel #" + channel.getName());
        }
        if (content instanceof String) {
            channel.sendMessage((String) content).queue();
        } else if (content instanceof MessageEmbed) {
            channel.sendMessage((MessageEmbed) content).queue();
        } else {
            throw new IllegalArgumentException("Content is not an instance of String or MessageEmbed");
        }
    }
}
