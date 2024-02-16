package org.example.discord;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackScheduler extends AudioEventAdapter {

    private AudioPlayer player;
    private boolean repeat = false;
    private List<AudioTrack> trackQueue = new ArrayList<>();

    private MusicHandler musicHandler;

    public TrackScheduler(AudioPlayer player, MusicHandler musicHandler) {
        this.musicHandler = musicHandler;
        this.player = player;
    }

    public void queue(AudioTrack audioTrack, boolean skip) {
        if (trackQueue.isEmpty()) {
            player.playTrack(audioTrack);
            musicHandler.nowPlaying(audioTrack.getInfo().title);
        }

        if(skip) {
            List<AudioTrack> temp = new ArrayList<>();
            temp.add(audioTrack);
            if(!trackQueue.isEmpty()) {
                temp.add(trackQueue.get(0).makeClone());
                trackQueue.remove(0);
            }
            temp.addAll(trackQueue);
            trackQueue = temp;
            playNext();
        } else {
            trackQueue.add(audioTrack);
        }
    }

    public void shuffle() {
        AudioTrack topTrack = trackQueue.get(0);
        Collections.shuffle(trackQueue);
        int topTrackIndex = trackQueue.indexOf(topTrack);
        Collections.swap(trackQueue, topTrackIndex, 0);
    }

    public List<AudioTrack> getQueue() {
        return trackQueue;
    }

    private void playNext() {
        AudioTrack topTrack = trackQueue.get(0);
        if (!repeat) {
            player.playTrack(topTrack);
            musicHandler.nowPlaying(topTrack.getInfo().title);
        } else {
            player.playTrack(topTrack.makeClone());
        }

    }

    public void clearTrackList() {
        trackQueue.clear();
        player.stopTrack();
    }

    public void skipTrack() {
        player.stopTrack();

        if (!trackQueue.isEmpty()) {
            trackQueue.remove(0);
        }

        if (!trackQueue.isEmpty()) {
            playNext();
        }
    }

    public boolean toggleRepeat() {
        if (repeat) {
            return repeat = false;
        } else {
            return repeat = true;
        }
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // A track started playing
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        System.out.println("Audio ended - " + endReason);
        if (endReason == AudioTrackEndReason.FINISHED) {

            if (!repeat) {
                trackQueue.remove(track);
            }
            if (!trackQueue.isEmpty()) {
                playNext();
            }
        }

        if (endReason == AudioTrackEndReason.LOAD_FAILED) {
            System.out.println(endReason);
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        System.out.println(exception.fillInStackTrace());
        retryPlayTrack(track);
        // An already playing track threw an exception (track end event will still be received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any audio, might want to just start a new track
    }

    public void retryPlayTrack(AudioTrack track) {
        player.playTrack(track.makeClone());
        System.out.println("attempting to retry playing audio track [" + track.getInfo() + "]");
    }

}
