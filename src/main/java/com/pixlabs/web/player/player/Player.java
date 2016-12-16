package com.pixlabs.web.player.player;

import com.pixlabs.web.player.advancedMp3.Mp3FileAdvanced;
import com.pixlabs.web.player.playlist.Playlist;

/**
 * Created by pix-i on 20/11/2016.
 */

// Should just play the song or stop it.

public interface Player extends Runnable {


    int getPlayIndex();


    Playlist getPlaylist();

    Player setPlaylist(Playlist playlist);

    Repeat getRepeat();

    void setRepeat(Repeat repeat);

    String setRepeat();

    void next();

    void previous();

    void volumeControl(float volume);

    void play();

    long getTimeLeft();

    Mp3FileAdvanced getCurrentSong();


    Player playNewSong(Mp3FileAdvanced mp3FileAdvanced);

    float getVolume();

    String getFlow();
}
