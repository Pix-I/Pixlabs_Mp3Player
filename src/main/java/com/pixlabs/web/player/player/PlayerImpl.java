/*
 *     This program, the Pixlabs_Player is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pixlabs.web.player.player;

import com.pixlabs.web.player.advancedMp3.Mp3FileAdvanced;
import com.pixlabs.web.player.advancedMp3.PixPlayer;
import com.pixlabs.web.player.playlist.Playlist;
import com.pixlabs.web.player.playlist.PlaylistImpl;
import com.pixlabs.web.player.playlist.Playlists;
import javazoom.jl.decoder.JavaLayerException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by pix-i on 27/11/2016.
 */


/**
 * The players role is to regulate the flow of the songs.
 */

@Component("Player")
@ManagedBean
public class PlayerImpl implements Player {

    private final Executor service = Executors.newSingleThreadExecutor();
    private Mp3FileAdvanced currentSong;
    // Setter, getter
    private Repeat repeat = Repeat.NONE;
    //setter and getter
    private Playlist playlist;
    private Playlists playlists;
    private boolean switching = false;
    private int playIndex = 0;
    //
    private PixPlayer player;
    private Flow flow = Flow.STOP;


    public PlayerImpl() {
        playlist = new PlaylistImpl();
        playlist.name("player playlist");
    }

    @Inject
    @Qualifier("Playlists")
    public void setPlaylists(Playlists playlists) {
        this.playlists = playlists;
    }

    /**
     * @return the index of the song being currently played on the player playlist.
     */

    @Override
    public int getPlayIndex() {
        return this.playIndex;
    }


    @Override
    public Playlist getPlaylist() {
        return playlist;
    }


    /**
     * Stops the player and sets a new playlist
     *
     * @param playlist A playlist the player will be able to play.
     * @return The player object itself
     */
    @Override
    public Player setPlaylist(Playlist playlist) {
        flow = Flow.STOP;
        if (player != null && player.isPlaying())
            player.stop();

        if (!playlist.isEmpty()) {
            playIndex = 0;
            currentSong = playlist.get(0);
        }
        this.playlist = playlists.copy(playlist);
        return this;
    }

    @Override
    public Repeat getRepeat() {
        return repeat;
    }

    @Override
    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
    }

    @Override
    public String setRepeat() {
        switch (repeat) {
            case NONE:
                repeat = Repeat.ALL;
                break;
            case ALL:
                repeat = Repeat.ONE;
                break;
            case ONE:
                repeat = Repeat.NONE;
                break;
        }
        return repeat.toString();

    }

    /**
     * Sets a new gain for the audio output.
     * @param volume
     */
    @Override
    public void volumeControl(float volume) {
        if (player != null)
            player.setVolume(volume);
    }

    /**
     * Changes the flow of the player from STOP to NEXT, and from other flows to STOP
     * If the flow was stop, it'll make a new thread that'll start playing the playlist.
     */
    public void play() {
        switch (flow) {
            case STOP:
                flow = Flow.NEXT;
                service.execute(this);
                break;
            default:
                flow = Flow.STOP;
                if (player != null)
                    player.stop();
                break;
        }
    }


    @Override
    public long getTimeLeft() {
        return player.getTimeLeft();
    }

    private void playPl() throws FileNotFoundException, JavaLayerException {
        do {


            // If end of playlist
            if (playIndex > playlist.size() - 1) {
                if (repeat == Repeat.ALL) {
                    playIndex = 0;
                } else {
                    flow = Flow.STOP;
                    playIndex = 0;
                    return;
                }
            }
            //if the playIndex goes below 0, play first song
            if (playIndex < 1) {
                playIndex = 0;
            }

            currentSong = playlist.get(playIndex);
            playCurrentSong();
            if (repeat == Repeat.ONE && flow != Flow.STOP)
                flow = Flow.CURRENT;

            switch (flow) {
                case NEXT:
                    playIndex++;
                    break;
                case PREVIOUS:
                    if (player.getLastPosition() < 2000) {
                        playIndex--;
                    }
                    flow = Flow.NEXT;
                    break;
                case CURRENT:
                    flow = Flow.NEXT;
                    break;
            }

        } while (flow != Flow.STOP);
    }

    /**
     * Plays the next song.
     */
    @Override
    public void next() {
        if (player != null && player.isPlaying()) {
            flow = Flow.NEXT;
            player.stop();
        } else {
            playIndex++;
        }
    }

    /**
     * Plays the previous song if the player has been playing for less than a certain amount of time.
     */

    @Override
    public void previous() {
        if (player != null && player.isPlaying()) {
            flow = Flow.PREVIOUS;

            player.stop();
        } else {
            playIndex--;
        }

    }


    private boolean playCurrentSong() {
        try {
            if (player != null && player.getCurrent().equals(currentSong) && flow != Flow.PREVIOUS) {
                int startF = player.getLastPosition();
                player = new PixPlayer(currentSong);
                return player.play(startF - 25, Integer.MAX_VALUE);
            } else {
                playlists.getHistory().addSong(currentSong);
                player = new PixPlayer(currentSong.setPlayed());
                return player.play();
            }
        } catch (JavaLayerException | FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     */

    @Override
    public void run() {
        try {
            playPl();
        } catch (FileNotFoundException | JavaLayerException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return the song that's being played.
     */

    @Override
    public Mp3FileAdvanced getCurrentSong() {
        return currentSong;
    }

    /**
     * Places a song inbetween the currently selected song and the previous one.
     * @param mp3FileAdvanced Song to be played.
     * @return returns the player instance.
     */
    @Override
    public Player playNewSong(Mp3FileAdvanced mp3FileAdvanced) {
        playlist.insertSong(mp3FileAdvanced, playIndex);
        currentSong = playlist.get(playIndex);
        if (flow == Flow.STOP) {
            play();
        } else {
            player.stop();
            flow = Flow.PREVIOUS;
        }


        return this;
    }

    @Override
    public float getVolume() {
        if (player != null)
            return player.getVolume();
        else
            return 0;
    }
//
//    @Override
//    public void playNewPlaylist(Playlist activePlaylist) {
//        flow = Flow.STOP;
//        if (player != null && player.isPlaying()) {
//            player.stop();
//        }
//        playlist = activePlaylist;
//        playIndex = 0;
//        play();
//
//    }

    @Override
    public String getFlow() {
        return flow.toString();
    }

}
