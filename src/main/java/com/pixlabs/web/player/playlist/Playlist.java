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

package com.pixlabs.web.player.playlist;

import com.pixlabs.web.player.advancedMp3.Mp3FileAdvanced;

import java.util.LinkedList;

/**
 * Created by pix-i on 27/11/2016.
 */
public interface Playlist extends Iterable<Mp3FileAdvanced> {
    LinkedList<Mp3FileAdvanced> getSonglist();

    Playlist setSonglist(LinkedList<Mp3FileAdvanced> playlist);

    int size();

    Mp3FileAdvanced get(int i);

    Playlist name(String name);

    Playlist addSong(Mp3FileAdvanced mp3File);

    Boolean isEmpty();

    Playlist addSongs(LinkedList<Mp3FileAdvanced> songs);

    Playlist addSongs(String... songs);

    Playlist removeSong(Mp3FileAdvanced mp3File);

    String getName();

    Mp3FileAdvanced findById(int id);

    Playlist insertSong(Mp3FileAdvanced mp3FileAdvanced, int index);

    int getRating();

    Playlist setRating(int i);

    Playlist randomise();

    Playlist randomise(int iterations);

    Playlist clear();

}
