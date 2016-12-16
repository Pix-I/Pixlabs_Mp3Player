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
