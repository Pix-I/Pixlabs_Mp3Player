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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by pix-i on 30/11/2016.
 */


public interface Playlists extends Iterable<Playlist> {

    Playlist create();

    Playlist create(String name);

    Playlist create(String name, String... songs);

    Playlist create(LinkedList<Mp3FileAdvanced> playlist);

    Playlist create(String name, LinkedList<Mp3FileAdvanced> playlist);


    Playlist create(String... songs);

    Playlist get();

    Playlist get(String name) throws PlaylistNotFoundException;

    Playlist get(int id) throws PlaylistNotFoundException;

    Playlist copy(Playlist playlist);


    void remove(int id);

    void remove(String name);

    void remove(Playlist playlist);


    Playlist search(String text);

    Playlist search(Playlist playlist, String text);

    Playlist getHistory();

    Playlists add(Playlist copy);

    int getIndex(Playlist playlist);

    ArrayList<Playlist> getAll();


    void addSoundFiles(Path path);

    ArrayList<Playlist> getArtists();

    ArrayList<Playlist> getGenres();

    ArrayList<Playlist> getAlbums();

    Playlist getArtist(String artist);

    Playlist getGenre(String artist);

    Playlist getAlbum(String artist);

    void resetSoundFiles(Path path);

    void addSong(Mp3FileAdvanced mp3FileAdvanced);
}
