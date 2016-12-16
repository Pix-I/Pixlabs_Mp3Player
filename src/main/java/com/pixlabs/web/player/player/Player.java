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
