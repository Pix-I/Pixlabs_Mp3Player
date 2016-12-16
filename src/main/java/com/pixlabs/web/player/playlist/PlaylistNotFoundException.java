package com.pixlabs.web.player.playlist;

/**
 * Created by pix-i on 30/11/2016.
 */
public class PlaylistNotFoundException extends Exception {

    public PlaylistNotFoundException(String msg) {
        super(msg);
    }

    public PlaylistNotFoundException() {
        super();
    }

}
