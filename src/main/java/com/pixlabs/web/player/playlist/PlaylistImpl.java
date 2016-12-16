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

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.pixlabs.web.player.advancedMp3.Mp3FileAdvanced;
import com.pixlabs.web.utils.FunkyNameGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by pix-i on 27/11/2016.
 */

/**
 * An object to facilitate the storing of Mp3FileAdvanced objects.
 */

public class PlaylistImpl implements Playlist {
    private LinkedList<Mp3FileAdvanced> playlist;
    private String name;
    private int rating;


    /**
     * Creates a new instance of the playlist, with a random name.
     */
    public PlaylistImpl() {
        this.playlist = new LinkedList<>();
        getNewRandomName();
        this.rating = 0;
    }

    private Playlist getNewRandomName() {
        if (!FunkyNameGenerator.isInit())
            FunkyNameGenerator.init();
        this.name = FunkyNameGenerator.getRandomName();
        return this;
    }


    /**
     * Replaces the songlist with a new set of songlist.
     */
    @Override
    public Playlist setSonglist(LinkedList<Mp3FileAdvanced> playlist) {
        this.playlist = playlist;
        return this;
    }

    @Override
    public LinkedList<Mp3FileAdvanced> getSonglist() {
        return playlist;
    }

    @Override
    public int size() {
        return playlist.size();
    }

    /**
     * Gets the song at the "i"-de index
     *
     * @param i index of the song
     */
    @Override
    public Mp3FileAdvanced get(int i) {
        return playlist.get(i);
    }

    @Override
    public Playlist name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Playlist setRating(int i) {
        this.rating = i;
        return this;
    }

    /**
     * Adds one song to the playlist.
     *
     * @param mp3File Song to be added
     */
    @Override
    public Playlist addSong(Mp3FileAdvanced mp3File) {
        playlist.add(mp3File);
        return this;
    }

    @Override
    public Boolean isEmpty() {
        return playlist.isEmpty();
    }

    /**
     * Adds a list of songs to the playlist
     * @param songs List to be added
     */
    @Override
    public Playlist addSongs(LinkedList<Mp3FileAdvanced> songs) {
        playlist.addAll(songs);
        return this;
    }

    @Override
    public Playlist addSongs(String... songs) {
        for (String s : songs) {
            if (Files.exists(Paths.get(s))) {
                if (s.endsWith(".mp3")) {
                    try {
                        playlist.add(new Mp3FileAdvanced(s));
                    } catch (IOException | UnsupportedTagException | InvalidDataException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return this;
    }

    /**
     * Removes the first occurrence of the song
     * @param mp3File Song to be removed
     */

    @Override
    public Playlist removeSong(Mp3FileAdvanced mp3File) {
        playlist.remove(mp3File);
        return this;
    }

    @Override
    public String getName() {
        return name;
    }


    /**
     * Finds a song by its id
     * @param id the id of the song
     */
    @Override
    public Mp3FileAdvanced findById(int id) {
        for (Mp3FileAdvanced m : playlist) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    /**
     * Inserts a song into the playlist at a certain index
     * @param mp3FileAdvanced The song to be inserted
     * @param index The index at which the song needs to be inserted
     */

    @Override
    public Playlist insertSong(Mp3FileAdvanced mp3FileAdvanced, int index) {
        if (index <= playlist.size()) {
            playlist.add(index, mp3FileAdvanced);
        }
        return null;
    }

    @Override
    public int getRating() {
        return rating;
    }


    /**
     * Radomises the playlist
     * @return the playlist object this was used on.
     */
    @Override
    public Playlist randomise() {
        Random random = new Random(31283);
        LinkedList<Mp3FileAdvanced> newPl = new LinkedList<>();
        for (int i = 0; i < playlist.size(); i++) {
            int rSong = random.nextInt(playlist.size());
            newPl.add(playlist.get(rSong));
            playlist.remove(rSong);
        }
        playlist = newPl;
        return this;
    }

    /**
     * Randomises the songs in the playlist
     * @param iterations The number of times the randomisation needs to happpen.
     * @return the playlist object this was used on.
     */

    @Override
    public Playlist randomise(int iterations) {
        for (int i = 0; i < iterations; i++) {
            randomise();
        }
        return this;
    }


    /**
     * Clears the songlist of the playlist
     * @return The playlist object.
     */
    @Override
    public Playlist clear() {
        this.playlist = new LinkedList<>();
        return this;
    }


    @Override
    public String toString() {
        return name + ": size:" + size();
    }

    @Override
    public Iterator<Mp3FileAdvanced> iterator() {
        return playlist.iterator();
    }
}
