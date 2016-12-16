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
