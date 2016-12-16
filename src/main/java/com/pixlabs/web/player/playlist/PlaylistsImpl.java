package com.pixlabs.web.player.playlist;

import com.pixlabs.web.player.advancedMp3.Mp3FileAdvanced;
import com.pixlabs.web.services.FileManager;
import com.pixlabs.web.utils.Mp3Finder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by pix-i on 30/11/2016.
 */


@Component("Playlists")
@ManagedBean
public class PlaylistsImpl implements Playlists {


    private ArrayList<Playlist> playlists = new ArrayList<>();
    private FileManager fileManager;
    private ArrayList<Playlist> artists;
    private ArrayList<Playlist> genres;
    private ArrayList<Playlist> albums;
    private LinkedList<String> artistList;
    private LinkedList<String> genreList;
    private LinkedList<String> albumList;

    public PlaylistsImpl() {
        create("history");
    }


    /**
     * Creates a new empty playlist with a random name.
     *
     * @return the newly created playlist.
     */
    @Override
    public Playlist create() {
        playlists.add(new PlaylistImpl());
        return playlists.get(playlists.size() - 1);
    }

    /**
     * Creates an empty playlist with a given name.
     *
     * @param name Name of the playlist to be created.
     * @return Created playlist.
     */
    @Override
    public Playlist create(String name) {
        playlists.add(new PlaylistImpl().name(name));
        return playlists.get(playlists.size() - 1);
    }

    /**
     * Create a playlist with a given name and songs.
     * @param name Name of the playlist.
     * @param songs Songs to be added to the playlist.
     * @return The created playlist.
     */

    @Override
    public Playlist create(String name, String... songs) {
        playlists.add(new PlaylistImpl()
                .name(name)
                .addSongs(songs));
        return playlists.get(playlists.size() - 1);
    }

    /**
     * Creates a new playlist with some Mp3files in it and a random name.
     * @param songs Files to be added to the playlist.
     * @return The created playlist.
     */
    @Override
    public Playlist create(String... songs) {
        playlists.add(new PlaylistImpl()
                .addSongs(songs));

        return playlists.get(playlists.size() - 1);
    }

    /**
     * Creates a new playlist with some Mp3files in it and a random name.
     * @param mp3Files Files to be added to the playlist.
     * @return The created playlist.
     */
    @Override
    public Playlist create(LinkedList<Mp3FileAdvanced> mp3Files) {
        playlists.add(new PlaylistImpl().
                setSonglist(mp3Files));

        return playlists.get(playlists.size() - 1);
    }

    /**
     * Creates a new playlist and returns it.
     * @param name Name of the playlist to be created.
     * @param mp3Files List of files to be added to the playlist.
     * @return The created playlist.
     */
    @Override
    public Playlist create(String name, LinkedList<Mp3FileAdvanced> mp3Files) {
        playlists.add(new PlaylistImpl().
                name(name).
                setSonglist(mp3Files));

        return playlists.get(playlists.size() - 1);
    }

    /**
     *
     * @return The soundfiles playlist.
     */
    @Override
    public Playlist get() {
        for (Playlist pl : playlists) {
            if (pl.getName().equals("soundfiles")) {
                return pl;
            }
        }
        return null;
    }

    /**
     * Returns a playlist with the given name
     * @param name Name of the playlist
     * @return Playlist object
     * @throws PlaylistNotFoundException if the playlist wasn't found.
     */

    @Override
    public Playlist get(String name) throws PlaylistNotFoundException {
        for (Playlist pl : playlists) {
            if (pl.getName().contains(name)) {
                return pl;
            }
        }
        for (Playlist pl : playlists) {
            if (pl.toString().contains(name)) {
                return pl;
            }
        }

        throw new PlaylistNotFoundException("Playlist not found:" + name);
    }

    /**
     * Returns a playlist with the given id
     * @param id Id of the playlist
     * @return Playlist object
     * @throws PlaylistNotFoundException if the playlist wasn't found.
     */
    @Override
    public Playlist get(int id) throws PlaylistNotFoundException {
        if (id < playlists.size()) {
            return playlists.get(id);
        }
        throw new PlaylistNotFoundException();
    }

    /**
     * Copies a playlist
     * @param playlist Playlist to be copied
     * @return Copy of the playlist.
     */

    @Override
    public Playlist copy(Playlist playlist) {
        return new PlaylistImpl().addSongs(playlist.getSonglist());
    }

    /**
     * Removes a playlist from the playlists Arraylist.
     * @param id of the playlist to be removed.
     */
    @Override
    public void remove(int id) {
        if (id > 1)
            playlists.remove(id);
    }

    /**
     * Removes a playlist from the playlists Arraylist.
     * @param name playlist to be removed.
     */
    @Override
    public void remove(String name) {
        if (!(name.equals("history") || name.equals("soundfiles"))) {
            try {
                playlists.remove(get(name));
            } catch (PlaylistNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Removes a playlist from the playlists Arraylist.
     * @param playlist playlist to be removed.
     */
    @Override
    public void remove(Playlist playlist) {
        if (!playlist.getName().equals("history") && !playlist.getName().equals("soundfiles")) {
            playlists.remove(playlist);
        }
    }

    /**
     * Looks for a song in all the soundfiles.
     * @param text text that'll be looked for.
     * @return a SearchList playlist.
     */

    @Override
    public Playlist search(String text) {
        Playlist playlist;
        try {
            playlist = get("SearchList");
            playlist.clear();
        } catch (PlaylistNotFoundException e) {
            playlist = new PlaylistImpl().name("SearchList");
            playlists.add(playlist);
        }
        text = text.toLowerCase();
        for (Mp3FileAdvanced song : get()) {
            if (song.getTitle().toLowerCase().startsWith(text))
                playlist.addSong(song);

        }

        if (playlist.isEmpty()) {
            return get();
        }

        return playlist;
    }

    /**
     * Sets the Searchlist playlist to a new playlist containing all the songs that resulted from a search.
     * @param playlist Playlist that has to be searched trough
     * @param text String that'll be used in the search.
     * @return a Searchlist playlist object containing the results of the search.
     */
    @Override
    public Playlist search(Playlist playlist, String text) {
        text = text.toLowerCase();
        Playlist returnPlaylist = new PlaylistImpl().name("SearchList");
        for (Mp3FileAdvanced song : playlist) {
            if (song.getTitle().toLowerCase().startsWith(text))
                returnPlaylist.addSong(song);
        }
        if (returnPlaylist.isEmpty()) {
            return get();
        }

        return returnPlaylist;
    }

    /**
     * Returns the history playlist.
     * @return playlist object containing all the played songs.
     */
    @Override
    public Playlist getHistory() {
        for (Playlist pl : playlists) {
            if (pl.getName().equals("history")) {
                return pl;
            }
        }
        return null;
    }

    /**
     * Adds a playlist to the playlists array
     * @param playlist Playlist object to be added
     * @return This object to make it easier to chain.
     */

    @Override
    public Playlists add(Playlist playlist) {
        playlists.add(playlist);
        return this;
    }

    /**
     *
     * @param playlist a playlist object.
     * @return an int representing the index of a playlist object in the playlists array.
     */
    @Override
    public int getIndex(Playlist playlist) {
        for (int i = 0; i < playlists.size(); i++) {
            if (playlist.equals(playlists.get(i)))
                return i;
        }

        return 0;
    }

    /**
     * Returns all the playlists.
     * @return Arraylist of playlist objects.
     */
    @Override
    public ArrayList<Playlist> getAll() {
        return playlists;
    }

    @Inject
    @Qualifier("FileManager")
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
        create("soundfiles");
        try {
            LinkedList<Mp3FileAdvanced> soundFiles = Mp3Finder.mp3InDirectory(fileManager.getSoundPath());
            get("soundfiles").addSongs(soundFiles);
            indexFiles(get().getSonglist());
        } catch (NotDirectoryException | PlaylistNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the soundfiles in a directory to the soundfiles playlist.
     * @param path path of the directory.
     */

    @Override
    public void addSoundFiles(Path path) {
        try {
            LinkedList<Mp3FileAdvanced> soundFiles = Mp3Finder.mp3InDirectory(path);
            get("soundfiles").addSongs(soundFiles);
        } catch (NotDirectoryException | PlaylistNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @return a list of all the artists.
     */
    @Override
    public ArrayList<Playlist> getArtists() {
        return artists;
    }

    /**
     *
     * @return a list of all the genres
     */
    @Override
    public ArrayList<Playlist> getGenres() {
        return genres;
    }

    /**
     *
     * @return a list of all the albums.
     */
    @Override
    public ArrayList<Playlist> getAlbums() {
        return albums;
    }

    private void initIndexLists() {
        if (artists == null) {
            artists = new ArrayList<>();
            artistList = new LinkedList<>();
        }
        if (genres == null) {
            genres = new ArrayList<>();
            genreList = new LinkedList<>();
        }
        if (albums == null) {
            albums = new ArrayList<>();
            albumList = new LinkedList<>();
        }
    }

    private Playlists indexFiles(LinkedList<Mp3FileAdvanced> toIndex) {
        initIndexLists();
        toIndex.forEach(this::indexFile);
        return this;
    }

    private void indexFile(Mp3FileAdvanced aToIndex) {

        if (artistList.contains(aToIndex.getArtist())) {
            for (int j = 0; j < artistList.size(); j++) {
                if (artistList.get(j).equals(aToIndex.getArtist()))
                    artists.get(j).addSong(aToIndex);
            }
        } else {
            artists.add(new PlaylistImpl()
                    .name(aToIndex.getArtist())
                    .addSong(aToIndex));
            artistList.add(aToIndex.getArtist());
        }
        if (genreList.contains(aToIndex.getGenre())) {
            for (int j = 0; j < genreList.size(); j++) {
                if (genreList.get(j).equals(aToIndex.getGenre()))
                    genres.get(j).addSong(aToIndex);
            }
        } else {
            genres.add(new PlaylistImpl()
                    .name(aToIndex.getGenre())
                    .addSong(aToIndex));
            genreList.add(aToIndex.getGenre());
        }
        if (albumList.contains(aToIndex.getAlbum())) {
            for (int j = 0; j < albumList.size(); j++) {
                if (albumList.get(j).equals(aToIndex.getAlbum()))
                    albums.get(j).addSong(aToIndex);
            }
        } else {
            albums.add(new PlaylistImpl()
                    .name(aToIndex.getAlbum())
                    .addSong(aToIndex));
            albumList.add(aToIndex.getAlbum());
        }

    }

    /**
     * Returns an artist playlist
     * @param artist Name of the album
     * @return Playlist object containing all the songs from an given artist.
     */

    @Override
    public Playlist getArtist(String artist) {
        if (artistList.contains(artist)) {
            for (int i = 0; i < artistList.size(); i++) {
                if (artistList.get(i).equals(artist)) {
                    return artists.get(i);
                }
            }
        }
        return get();


    }

    /**
     * Returns an genre playlist
     * @param genre Name of the genre
     * @return Playlist object containing all the songs from an genre.
     */
    @Override
    public Playlist getGenre(String genre) {
        if (genreList.contains(genre)) {
            for (int i = 0; i < genreList.size(); i++) {
                if (genreList.get(i).equals(genre)) {
                    return genres.get(i);
                }
            }
        }
        return get();


    }

    /**
     * Returns an album playlist
     * @param album Name of the album
     * @return Playlist object containing all the songs from an album.
     */
    @Override
    public Playlist getAlbum(String album) {

        for (int i = 0; i < albumList.size(); i++) {
            if (albumList.get(i).equals(album)) {
                return albums.get(i);
            }
        }

        return get();


    }

    /**
     * Clears the soundfiles and adds all the songs in the given directory and subdirectories.
     * @param path The new path where the program will fetch the soundfiles
     */

    @Override
    public void resetSoundFiles(Path path) {
        get().clear().addSongs(Mp3Finder.mp3InDirectories(path));
        indexFiles(get().getSonglist());

    }

    /**
     * Adds a song to the playlists soundfiles.
     * @param mp3FileAdvanced Song to be added.
     */

    @Override
    public void addSong(Mp3FileAdvanced mp3FileAdvanced) {
        get().addSong(mp3FileAdvanced);
        indexFile(mp3FileAdvanced);
    }


    @Override
    public Iterator<Playlist> iterator() {

        return playlists.iterator();
    }
}
