package com.pixlabs.web.model;

import com.pixlabs.web.player.advancedMp3.Mp3FileAdvanced;
import com.pixlabs.web.player.player.Player;
import com.pixlabs.web.player.playlist.Playlists;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.LinkedList;


/**
 * Created by pix-i on 02/12/2016.
 */

/**
 * This is a helper class, to help display the currently played song and the songs that'll be played next/previously.
 */


@Component("PlayerView")
@Qualifier("PlayerView")
public class PlayerView {

    private Player player;
    private Playlists playlists;


    /**
     * @return the song that's being played/paused by the player.
     */
    public String getCurrent() {
        String current = "";
        if (player.getCurrentSong() != null)
            current = player.getCurrentSong().getTitle();
        return current;
    }

    /**
     * @return The songs that have been played recently by the player.
     */

    public LinkedList<String> getsHistory() {
        LinkedList<String> sHistory = new LinkedList<>();
        LinkedList<Mp3FileAdvanced> history = new LinkedList<>();
        int historySize = playlists.getHistory().getSonglist().size();
        for (int i = historySize - 2; i >= 0 && i > historySize - 5; i--) {
            history.add(playlists.getHistory().getSonglist().get(i));
        }

        for (int i = history.size() - 1; i >= 0; i--) {
            sHistory.add(history.get(i).getTitle());
        }
        return sHistory;
    }

    /**
     *
     * @return The next few songs that'll be played by the player.
     */
    public LinkedList<String> getsToPlay() {
        LinkedList<String> sToPlay = new LinkedList<>();
        int plSize = player.getPlaylist().size();
        for (int i = player.getPlayIndex() + 1; i < plSize && i < player.getPlayIndex() + 3 && i > -1; i++) {
            sToPlay.add(player.getPlaylist().get(i).getTitle());
        }


        return sToPlay;
    }

    @Inject
    @Qualifier("Player")
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Inject
    @Qualifier("Playlists")
    public void setPlaylists(Playlists playlists) {
        this.playlists = playlists;
    }
}
