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

package com.pixlabs.web.controllers;

import com.pixlabs.web.model.OptionsView;
import com.pixlabs.web.model.PlayerView;
import com.pixlabs.web.player.advancedMp3.Mp3FrameView;
import com.pixlabs.web.player.player.Player;
import com.pixlabs.web.player.playlist.Playlist;
import com.pixlabs.web.player.playlist.PlaylistNotFoundException;
import com.pixlabs.web.player.playlist.Playlists;
import com.pixlabs.web.services.FileManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main controller of the project.
 */

@Controller
@ManagedBean
public class IndexController {

    private FileManager fileManager;
    private Playlists playlists;
    private Player player;
    private PlayerView playerView;
    private OptionsView optionsView;
    private Path imgPath = Paths.get("./images");

    @Inject
    @Qualifier("FileManager")
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Inject
    @Qualifier("Playlists")
    public void setPlaylists(Playlists playlists) {
        this.playlists = playlists;
    }

    @Inject
    @Qualifier("Player")
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Inject
    @Qualifier("PlayerView")
    public void setPlayerView(PlayerView playerView) {
        this.playerView = playerView;
    }

    @Inject
    @Qualifier("OptionsView")
    public void setOptionsView(OptionsView optionsView) {
        this.optionsView = optionsView;
    }

    //INDEX
    @RequestMapping(value = {"/", "/index", "/*"})
    public String getIndexPage(Model model) {

        Playlist lowerPlaylist = playlists.get();
        Playlist upperPlaylist = playlists.get();
        //ATTACH PLAYER IF A SONG IS PLAYING
        attachPlayer(model);
        attachPlayerPlaylist(model);
        model.addAttribute("repeat", player.getRepeat());

        //ATTACH DEFAULT PLAYLISTS (soundFiles + history)
        attachPlaylistTable(model, upperPlaylist, "upper");
        attachPlaylistTable(model, lowerPlaylist, "lower");


        return "Index";
    }

    private Model attachPlayer(Model model) {
        if (player.getCurrentSong() != null) {
            model.addAttribute("SoundWave", Mp3FrameView.getFrameView(player.getCurrentSong()));
            model.addAttribute("currentTitle", player.getCurrentSong().getTitle());
            model.addAttribute("currentArtist", player.getCurrentSong().getArtist());
            model.addAttribute("volume", player.getVolume());
            model.addAttribute("flow", player.getFlow());
        }
        return model;
    }


    // PLAYER

    private Model attachPlayerPlaylist(Model model) {
        if (player.getPlaylist() != null && !player.getPlaylist().isEmpty()) {
            model.addAttribute("history", playerView.getsHistory());
            model.addAttribute("current", playerView.getCurrent());
            model.addAttribute("toplay", playerView.getsToPlay());
        }
        return model;
    }

    private Model attachPlaylistTable(Model model, Playlist playlist, String attributeExt) {
        if (playlist == null) {
            playlist = playlists.get();
        }
        //Add select lists
        model.addAttribute("playlists", playlists.getAll());
        model.addAttribute("artists", playlists.getArtists());
        model.addAttribute("albums", playlists.getAlbums());
        model.addAttribute("genres", playlists.getGenres());

        model.addAttribute(attributeExt + "Playlist", playlist);

        return model;
    }

    @RequestMapping("/getPlayerPanel")
    public String getPlayerPanel(Model model) {
        attachPlayer(model);
        attachPlayerPlaylist(model);
        return "PlayerPanel";
    }

    @ResponseBody
    @RequestMapping("/repeat")
    public String repeat() {
        return player.setRepeat();
    }

    @ResponseBody
    @RequestMapping("/player/playsong/{songId}")
    public String playSong(@PathVariable String songId) {
        try {
            if (player != null)
                player.playNewSong(playlists.get().findById(Integer.parseInt(songId)));
        } catch (Exception ignored) {
            return "error";
        }
        return "success";
    }

    @RequestMapping("/player/playerTable")
    public String loadPlayerTable(Model model) {
        attachPlayerPlaylist(model);
        return "PlayerTablePanel";
    }

    @RequestMapping("/play")
    public String playSong(Model model) {
        model.addAttribute("playlistList", playlists.getAll());
        play();

        attachPlayer(model);
        attachPlayerPlaylist(model);

        return "PlayerPanel";
    }

    private void play() {
        if (player.getPlaylist().isEmpty())
            newRandomPl();
        player.play();

    }

    private void newRandomPl() {
        player.setPlaylist(
                playlists
                        .get()
                        .randomise());
    }

    @RequestMapping("/nextsong")
    public String next(Model model) {
        player.next();
        attachPlayer(model);
        attachPlayerPlaylist(model);
        return "PlayerPanel";
    }

    @RequestMapping("/prevsong")
    public String previous(Model model) {
        player.previous();

        attachPlayer(model);
        attachPlayerPlaylist(model);

        return "PlayerPanel";
    }

    // PLAYLIST
    @RequestMapping("/playlists/loadUpper/{category}/{id}")
    public String loadUpperPlaylist(Model model, @PathVariable String category, @PathVariable String id) {

        Playlist upperPlaylist = parsePlaylist(category, id);
        attachPlaylistTable(model, upperPlaylist, "upper");
        return "UpperPlaylistPanel";
    }

    @RequestMapping("/playlists/loadLower/{category}/{id}")
    public String loadLowerPlaylist(Model model, @PathVariable String category, @PathVariable String id) {
        Playlist lowerPlaylist = parsePlaylist(category, id);
        attachPlaylistTable(model, lowerPlaylist, "lower");

        return "LowerPlaylistPanel";
    }

    @RequestMapping("/playlists/search/{playlistValue}/{searchBoxValue}")
    public String searchBox(Model model, @PathVariable String playlistValue, @PathVariable String searchBoxValue) {
        searchBoxValue = searchBoxValue.replace('_', ' ');
        searchBoxValue = searchBoxValue.replace("U238183", "_");
        Playlist returnPlaylist = playlists.search(searchBoxValue);
        if (playlistValue.equals("upper")) {
            attachPlaylistTable(model, returnPlaylist, "upper");
            return "UpperPlaylistPanel";
        } else {
            attachPlaylistTable(model, returnPlaylist, "lower");
            return "LowerPlaylistPanel";
        }

    }

    /**
     * @param model      Spring model
     * @param tableId    The id of the table, either upper/lower
     * @param playlistId The id of the active playlist..
     * @param songId     The id of the song that has to be added.
     * @return Either the lower or the upper playlist panel template.
     */

    @RequestMapping("/playlists/addSong/{tableId}/{playlistId}/{songId}")
    public String addSong(Model model, @PathVariable String tableId, @PathVariable String playlistId, @PathVariable String songId) {

        Playlist playlist = parsePlaylist("playlistSelect", playlistId);
        if (!playlist.getName().equals("history") && !playlist.getName().equals("soundfiles"))
            playlist.addSong(playlists.get().findById(Integer.parseInt(songId)));

        if (tableId.equals("lower")) {
            attachPlaylistTable(model, playlist, "lower");
            return "LowerPlaylistPanel";
        } else {
            attachPlaylistTable(model, playlist, "upper");
            return "UpperPlaylistPanel";
        }
    }


    /**
     * Clears or delete a playlist if that one is empty.
     *
     * @param model    Spring model
     * @param selected The select filter
     * @param playlist The playlist name.
     * @return The lowerplaylistpanel.
     */
    @RequestMapping("/playlists/clear/{selected}/{playlist}")
    public String clear(Model model, @PathVariable String selected, @PathVariable String playlist) {
        playlist = playlist.replace('_', ' ');
        playlist = playlist.replace("U238183", "_");
        Playlist activePlaylist = playlists.get();
        switch (selected) {
            case "playlistSelect":
                if (!selected.equals("Playlists") || !selected.equals("history") || !selected.equals("soundfiles"))
                    try {
                        activePlaylist = playlists.get(playlist);
                        if (activePlaylist.isEmpty()) {
                            playlists.remove(activePlaylist);
                            activePlaylist = playlists.get();
                        } else {
                            activePlaylist.clear();
                        }

                    } catch (PlaylistNotFoundException e) {
                        e.printStackTrace();
                    }
                break;
        }

        attachPlaylistTable(model, activePlaylist, "lower");
        return "LowerPlaylistPanel";
    }

    /**
     * Creates a new playlist with a random name.
     * @param model spring model
     * @return lower playlist panel template.
     */

    @RequestMapping("/playlists/create")
    public String createPlaylist(Model model) {

        attachPlaylistTable(model, playlists.create(), "lower");
        return "LowerPlaylistPanel";
    }

    private Playlist parsePlaylist(String category, String id) {

        id = id.replace('_', ' ');
        id = id.replace("U238183", "_");


        Playlist playlist = playlists.get();
        switch (category) {
            case "artistSelect":
                playlist = playlists.getArtist(id);
                break;
            case "genreSelect":
                playlist = playlists.getGenre(id);
                break;
            case "playlistSelect":
                if (!id.equals("Playlists"))
                    try {
                        playlist = playlists.get(id);

                    } catch (PlaylistNotFoundException e) {
                        e.printStackTrace();
                    }
                break;
            case "albumSelect":
                playlist = playlists.getAlbum(id);
                break;
            default:
                playlist = playlists.get();
        }
        return playlist;


    }

    /**
     * Copies a selected playlist.
     * @param model spring model
     * @param selected select filter
     * @param id Name of the playlist.
     * @return The new playlist incorporated into a lower playlist panel.
     */
    @RequestMapping("/playlists/copy/{selected}/{id}")
    public String copy(Model model, @PathVariable String selected, @PathVariable String id) {
        Playlist playlist = playlists.copy(parsePlaylist(selected, id));
        playlists.add(playlist);
        attachPlaylistTable(model, playlist, "lower");
        return "LowerPlaylistPanel";
    }

    /**
     * Plays a certain playlist.
     * @param model spring model
     * @param selected select filter
     * @param id Name of the playlist.
     * @return The template associated to "playSong".
     */
    @RequestMapping("/playlists/play/{selected}/{id}")
    public String playPlaylist(Model model, @PathVariable String selected, @PathVariable String id) {

        player.setPlaylist(parsePlaylist(selected, id));

        return playSong(model);
    }

    /**
     * Sets a new volume.
     * @param volume New volume
     */
    // OPTIONS
    @RequestMapping("/volume/{volume}")
    @ResponseBody
    public String volume(@PathVariable float volume) {
        player.volumeControl(volume);
        return "success";
    }

    /**
     * To be implemented.
     */
    @RequestMapping("/removeFromCurrent/songId")
    @ResponseBody
    public String removeSong() {
        return "success";
    }


    /**
     * Sets a new MediaPath/SoundPath or loads some mp3files into the playlists object.
     *
     * @param model   Spring model
     * @param folder  Number of the column of folders the click originated from.
     * @param type    The object that was dragged onto the folder.
     * @param newPath The folder id.
     * @return a new folderview.
     */
    @RequestMapping(value = {"/newPath/*/{type}/{newPath}/{folder}", "/newPath/{type}/{newPath}/{folder}"})
    public String newPathCurrent(Model model,
                                 @PathVariable String folder,
                                 @PathVariable String type,
                                 @PathVariable String newPath) {

        switch (type) {
            case "MediaPathDrag":
                optionsView.setNewMediaPath(newPath, folder);
                break;
            case "SoundPathDrag":
                optionsView.setNewSoundsPath(newPath, folder);
                break;
            case "Mp3Drag":
                optionsView.loadMp3(newPath, folder);
                break;
        }
        return addOptionAttributes(model);
    }

    /**
     * Sends back a new folderview from a given folder
     *
     * @param model   Spring model
     * @param folder  level the click originated from
     * @param newPath folder id
     * @return a new folderview.
     */
    @RequestMapping(value = {"/paths/{newPath}/{folder}", "/paths/*/{newPath}/{folder}"})
    public String goPathCurrent(Model model, @PathVariable String folder, @PathVariable String newPath) {
        optionsView.navigateToFolder(newPath, folder);

        return addOptionAttributes(model);
    }

    private String addOptionAttributes(Model model) {

        model.addAttribute("folderView", optionsView.getFolderView());
        model.addAttribute("soundFolderName", optionsView.getSoundFolderName());
        model.addAttribute("mediaFolderName", optionsView.getMediaFolderName());

        return "OptionsPanel";
    }

    @RequestMapping("/testMp3DirectorySearch")
    @ResponseBody
    public String test() {
        optionsView.addSongsFromCurrent();
        return "Success";
    }

    /**
     * Switches from the Playlistpanel to the OptionsPanel
     */
    @RequestMapping("/options")
    public String options(Model model) {
        optionsView.reset();

        return addOptionAttributes(model);
    }

    /**
     * Uploads some files to a folder
     * @param files Files that'll be uploaded.
     * @return A 'json' object that indicates that the upload was successful.
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String storeFile(@RequestParam("files[]") MultipartFile[] files) {
        fileManager.store(files);
        return "{\"success\":true,\"error\":\"\"}";
    }

    /**
     * Returns the time until the song finished playing and 1 if the song is paused.
     * @return Time until the song finishes.
     */
    @RequestMapping("/getTimeLeft")
    @ResponseBody
    public String getTimeLeft() {
        long timeLeft = 1;
        String song = "__STOPPED__";
        if (!player.getFlow().equals("STOP") && player.getCurrentSong() != null) {
            timeLeft = player.getTimeLeft();
            song = player.getCurrentSong().getTitle();
        }


        return "{\"timeLeft\":" + timeLeft + ",\"song\":\"" + song + "\"}";
    }

    /**
     * Loads the cover image of the song being currently played.
     * @return Byte array of an image.
     */

    @RequestMapping(value = "/img/loadCover/*")
    @ResponseBody
    public byte[] albumCover() {
        if (player.getCurrentSong() == null) {
            Path defaultCoverPath = imgPath.resolve("default_cover.png");
            try {
                return Files.readAllBytes(defaultCoverPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return player.getCurrentSong().getAlbumCover();
    }


    @RequestMapping("/img/albumCover")
    public String loadCover() {
        return "CoverImage";
    }

    /**
     * Returns the githublogo
     * @return Githublogo
     */
    @RequestMapping("/img/githubLogo")
    @ResponseBody
    public byte[] getGithubLogo() {
        Path img = imgPath.resolve("github.png");
        try {
            return Files.readAllBytes(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds an array of ints so that the soundwave can be constructed on the clients browser. +  songInfo
     * @param model Spring model
     * @return a soundWave template.
     */
    @RequestMapping("/img/soundWave")
    public String soundWave(Model model) {
        try {
            model.addAttribute("draw", Mp3FrameView.getFrameView(player.getCurrentSong()));
            model.addAttribute("currentTitle", player.getCurrentSong());
            model.addAttribute("currentArtist", player.getCurrentSong().getArtist());
        } catch (Exception ignored) {
            System.out.println("Exception at soundwave");
        }

        return "SoundWave";
    }


}
