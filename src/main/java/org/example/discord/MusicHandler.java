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

    public void playMusic(String query, SlashCommandInteractionEvent event) {
        if(null == audioManager) {
            joinVoice(event);
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

    public void stopMusic() {
        trackScheduler.clearTrackList();
    }

    public boolean toggleRepeat() {
        return trackScheduler.toggleRepeat();
    }

    public void skipTrack(SlashCommandInteractionEvent event) {
        trackScheduler.skipTrack();
    }
    private void returnPlayingTrack(String info, SlashCommandInteractionEvent event) {
        event.reply("**Track Queued : **" + info).queue();
    }
}
