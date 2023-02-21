package org.example.discord;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.example.model.exceptions.UserNotInVoiceException;

import java.util.List;
import java.util.StringJoiner;

public class MusicHandler {

    private AudioPlayerManager audioPlayerManager;
    private AudioManager audioManager;
    private AudioPlayer player;
    private TrackScheduler trackScheduler;

    public MusicHandler() {
        audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        player = audioPlayerManager.createPlayer();
        trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);
    }

    private String joinVoice(SlashCommandInteractionEvent event) {
        VoiceChannel connectedChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();
        audioManager = event.getGuild().getAudioManager();
        audioManager.openAudioConnection(connectedChannel);
        audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
        return connectedChannel.getAsMention();
    }

    public void leaveVoice() {
        audioManager.closeAudioConnection();
        audioManager = null;
    }

    public void playMusic(String query, SlashCommandInteractionEvent event) throws UserNotInVoiceException {
        if(null == audioManager) {
            joinVoice(event);
        } else {
            verifyCallingUserIsInSameVoice(event);
        }

        audioPlayerManager.loadItem(query, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                trackScheduler.queue(audioTrack);
                returnPlayingTrack(audioTrack.getInfo().title, event);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                for (AudioTrack track : audioPlaylist.getTracks()) {
                    trackScheduler.queue(track);
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    public String getMusicQueue() {
        List<AudioTrack> trackList = trackScheduler.getQueue();

        if(trackList.isEmpty()) {
            return "Queue is empty";
        }
        StringJoiner stringJoiner = new StringJoiner("\n");
        for(AudioTrack track : trackList) {
            stringJoiner.add(track.getInfo().title + " " + (track.getDuration()/1000)/60 + ":" + (track.getDuration()/1000)%60);
        }
        return stringJoiner.toString();
    }

    public void stopMusic(SlashCommandInteractionEvent event) throws UserNotInVoiceException {
        verifyCallingUserIsInSameVoice(event);
        trackScheduler.clearTrackList();
    }

    public boolean toggleRepeat() {
        return trackScheduler.toggleRepeat();
    }

    public void skipTrack(SlashCommandInteractionEvent event) throws UserNotInVoiceException {
        verifyCallingUserIsInSameVoice(event);
        trackScheduler.skipTrack();
    }

    private void returnPlayingTrack(String info, SlashCommandInteractionEvent event) {
        event.reply("**Track Queued : **" + info).queue();
    }

    private void verifyCallingUserIsInSameVoice(SlashCommandInteractionEvent event) throws UserNotInVoiceException {

        if(null == event.getMember().getVoiceState().getChannel() || null == audioManager) {
            throw new UserNotInVoiceException("user needs to be in the bots voice channel to execute this command");
        }
        if(null == event.getMember().getVoiceState().getChannel().asVoiceChannel() || audioManager.getConnectedChannel().asVoiceChannel() != event.getMember().getVoiceState().getChannel().asVoiceChannel()) {
            throw new UserNotInVoiceException("user needs to be in the bots voice channel to execute this command");
        }
    }
}
