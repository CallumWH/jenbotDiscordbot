package org.example.model;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;

public class UserMusicRequest {

    private User user;
    private AudioTrack track;

    private Channel channelContext;
}
