package com.pixlabs.web.player.advancedMp3;

/**
 * Created by pix-i on 05/12/2016.
 */
public class PlaybackEvent {


    public static int STOPPED = 1;
    public static int STARTED = 2;

    private PixPlayer source;
    private int frame;
    private int id;

    public PlaybackEvent(PixPlayer source, int id, int frame) {
        this.id = id;
        this.source = source;
        this.frame = frame;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public PixPlayer getSource() {
        return source;
    }

    public void setSource(PixPlayer source) {
        this.source = source;
    }

}
