package org.example.discord;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.List;

public class TrackScheduler extends AudioEventAdapter {

    private AudioPlayer player;
    private boolean repeat = false;
    private List<AudioTrack> trackQueue = new ArrayList<>();

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
    }

    public void queue(AudioTrack audioTrack) {
        if (trackQueue.isEmpty()) {
            player.playTrack(audioTrack);
        }
        trackQueue.add(audioTrack);
    }

    public List<AudioTrack> getQueue() {
        return trackQueue;
    }

    private void playNext() {
        if(!repeat) {
            player.playTrack(trackQueue.get(0));
        } else {
            player.playTrack(trackQueue.get(0).makeClone());
        }

    }

    public void clearTrackList() {
        trackQueue.clear();
        player.stopTrack();
    }

    public void skipTrack() {
        trackQueue.remove(player.getPlayingTrack());
        player.stopTrack();

        if(!trackQueue.isEmpty()) {
            playNext();
        }
    }

    public boolean toggleRepeat() {
        if(repeat) {
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

            if(!repeat) {
                trackQueue.remove(track);
            }
            if (!trackQueue.isEmpty()) {
                playNext();
            }
        }

        if(endReason == AudioTrackEndReason.LOAD_FAILED) {
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
        // An already playing track threw an exception (track end event will still be received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any audio, might want to just start a new track
    }

}
