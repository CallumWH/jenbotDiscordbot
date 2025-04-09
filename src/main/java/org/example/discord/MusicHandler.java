package org.example.discord;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.example.model.AudioSourceManagers;
import org.example.model.exceptions.UserNotInVoiceException;

import java.util.List;
import java.util.StringJoiner;

public class MusicHandler {

    public static final String BOT_ID = "1077005717996838912";
    public static final int QUEUE_PAGE_SIZE = 20;

    private AudioPlayerManager audioPlayerManager;
    private AudioManager audioManager;
    private AudioPlayer player;
    private TrackScheduler trackScheduler;

    public MusicHandler() {
        audioPlayerManager = new DefaultAudioPlayerManager();
        YoutubeAudioSourceManager youtubeAudioSourceManager = new dev.lavalink.youtube.YoutubeAudioSourceManager();
        youtubeAudioSourceManager.useOauth2(YOUTUBE_TOKEN, true);
        //AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        audioPlayerManager.registerSourceManager(youtubeAudioSourceManager);
        player = audioPlayerManager.createPlayer();
        trackScheduler = new TrackScheduler(player, this);
        player.addListener(trackScheduler);
    }

    public String joinVoice(SlashCommandInteractionEvent event) {
        VoiceChannel connectedChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();
        audioManager = event.getGuild().getAudioManager();
        audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
        try {
            audioManager.openAudioConnection(connectedChannel);
        } catch (Exception e) {
            System.out.println(e);
        }
        return connectedChannel.getAsMention();
    }

    public void leaveVoice() {
        audioManager.closeAudioConnection();
        audioManager = null;
    }

    public void shuffle() {
        trackScheduler.shuffle();
    }

    public void playMusic(String query, SlashCommandInteractionEvent event, boolean skip) throws UserNotInVoiceException {
        if (null == audioManager) {
            joinVoice(event);
        } else {
            verifyCallingUserIsInSameVoice(event);
        }

        audioPlayerManager.loadItem(query, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                trackScheduler.queue(audioTrack, skip);
                returnQueuedTrack(audioTrack.getInfo().title, event);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                event.reply("**Playlist Queued :" + audioPlaylist.getName() + "**\n**Length :" + audioPlaylist.getTracks().size() + "**").queue();
                for (AudioTrack track : audioPlaylist.getTracks()) {
                    trackScheduler.queue(track, skip);
                }
            }

            @Override
            public void noMatches() {
                System.out.println("no matches");
            }

            @Override
            public void loadFailed(FriendlyException e) {
                System.out.println(e);
            }
        });
    }

    public void jumpQueue() {

    }

    public String getMusicQueue(int page) {
        List<AudioTrack> trackList = trackScheduler.getQueue();
        if(page < 1) {
            page = 1;
        }
        if((page - 1) * QUEUE_PAGE_SIZE > trackList.size()) {
            page = (int) Math.ceil((double) trackList.size() / (double) QUEUE_PAGE_SIZE);
        }

        if (trackList.isEmpty()) {
            return "Queue is empty";
        }
        StringJoiner stringJoiner = new StringJoiner("\n");

        for (int i = (page - 1) * QUEUE_PAGE_SIZE; i < trackList.size(); i++) {
            AudioTrack currentTrack = trackList.get(i);
            if (i >= QUEUE_PAGE_SIZE * page) {
                break;
            }
            stringJoiner.add(currentTrack.getInfo().title + " " + (currentTrack.getDuration() / 1000) / 60 + ":" + (currentTrack.getDuration() / 1000) % 60);
        }
        stringJoiner.add("Page " + page + "/" + (int) Math.ceil((double) trackList.size() / (double) QUEUE_PAGE_SIZE));
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

    private void returnQueuedTrack(String info, SlashCommandInteractionEvent event) {
        event.reply("**Track Queued : **" + info).queue(null, (exception) -> System.out.println(exception));
    }

    private void verifyCallingUserIsInSameVoice(SlashCommandInteractionEvent event) throws UserNotInVoiceException {

        if (null == event.getMember().getVoiceState().getChannel() || null == audioManager) {
            throw new UserNotInVoiceException("user needs to be in the bots voice channel to execute this command");
        }
        if (null == event.getMember().getVoiceState().getChannel().asVoiceChannel() || audioManager.getConnectedChannel().asVoiceChannel() != event.getMember().getVoiceState().getChannel().asVoiceChannel()) {
            throw new UserNotInVoiceException("user needs to be in the bots voice channel to execute this command");
        }
    }

    public void checkChannelPopulation(GuildVoiceUpdateEvent event) {
        if (null != audioManager) {
            if (event.getMember().getId().equals("1077005717996838912")) {
                return;
            }
            if (event.getGuild().getMemberById(BOT_ID).getVoiceState().getChannel().asVoiceChannel().getMembers().size() < 2) {
                event.getGuild().getTextChannelById("493537895404273674").sendMessage("**No users remain in channel : " + audioManager.getConnectedChannel().getAsMention() + " Disconnecting**").queue();
                leaveVoice();
            }
        }
    }

    public void nowPlaying(String trackName) {
        audioManager.getJDA().getTextChannelById("493537895404273674").sendMessage("**Now playing...**\n" + trackName).queue();
    }
}
