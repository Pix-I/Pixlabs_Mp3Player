package com.pixlabs.web.player.advancedMp3;


import javazoom.jl.decoder.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by pix-i on 05/12/2016.
 */
public class PixPlayer {

    private boolean paused = true;
    /**
     * The MPEG audio bitstream.
     */
    private Bitstream bitstream;
    /**
     * The MPEG audio decoder.
     */
    private Decoder decoder;
    /**
     * The AudioDevice the audio samples are written to.
     */
    private AudioDevice audio;
    /**
     * Has the player been closed?
     */
    private boolean closed = false;
    /**
     * Has the player setPlayed back all frames from the stream?
     */
    private int lastPosition = 0;
    /**
     * Listener for the playback process
     */
    private PlaybackListener listener;

    private boolean playing = false;

    private Mp3FileAdvanced current;

    private long timeStarted;
    private long timeToFinish;

    /**
     * Creates a new <code>player</code> instance.
     */
    private PixPlayer(InputStream stream) throws JavaLayerException {
        this(stream, null);

    }

    private PixPlayer(InputStream stream, AudioDevice device) throws JavaLayerException {
        bitstream = new Bitstream(stream);
        if (device != null) audio = device;
        else audio = FactoryRegistry.systemRegistry().createAudioDevice();
        audio.open(decoder = new Decoder());
    }

    /**
     * Creates a new player instance
     *
     * @param mp3File file to be played
     * @throws FileNotFoundException if the file can't be found.
     * @throws JavaLayerException
     */
    public PixPlayer(Mp3FileAdvanced mp3File) throws FileNotFoundException, JavaLayerException {
        this(new FileInputStream(mp3File.getFilename()));
        current = mp3File;
    }

    public PixPlayer(Mp3FileAdvanced mp3File, AudioDevice device) throws FileNotFoundException, JavaLayerException {
        this(new FileInputStream(mp3File.getFilename()), device);
        current = mp3File;
    }

    /**
     * @return the position the player was at when it stopped.
     */
    public int getLastPosition() {
        return lastPosition;
    }

    /**
     *  Plays a file
     * @return true if the whole file was played.
     * @throws JavaLayerException
     */
    public boolean play() throws JavaLayerException {
        return play(Integer.MAX_VALUE);
    }


    /**
     * Plays a number of MPEG audio frames.
     *
     * @param frames The number of frames to play.
     * @return true if the last frame was setPlayed, or false if there are
     * more frames.
     */
    public boolean play(int frames) throws JavaLayerException {
        boolean ret = true;
        playing = true;
        Date date = new Date();
        timeStarted = date.getTime();

        // report to listener
        if (listener != null) listener.playbackStarted(createEvent(PlaybackEvent.STARTED));

        paused = false;
        while (frames-- > 0 && ret) {
            ret = decodeFrame();
        }
        // last frame, ensure all data flushed to the audio device.
        AudioDevice out = audio;
        if (out != null) {
            out.flush();
            synchronized (this) {
                close();
            }

            // report to listener
            if (listener != null) listener.playbackFinished(createEvent(out, PlaybackEvent.STOPPED));
        }
        playing = false;
        return paused;
    }


    /**
     * Closes this player. Any audio currently playing is stopped
     * immediately.
     */
    public synchronized void close() {
        AudioDevice out = audio;
        if (out != null) {
            closed = true;
            audio = null;
            // this may fail, so ensure object state is set up before
            // calling this method.
            lastPosition = out.getPosition();
            out.close();
            try {
                bitstream.close();
                paused = true;
            } catch (BitstreamException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Decodes a single frame.
     *
     * @return true if there are no more frames to decode, false otherwise.
     */
    private boolean decodeFrame() throws JavaLayerException {
        try {
            AudioDevice out = audio;
            if (out == null) return false;

            Header h = bitstream.readFrame();
            if (h == null) return false;

            // sample buffer set when decoder constructed
            SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, bitstream);

            synchronized (this) {
                out = audio;
                if (out != null) {
                    out.write(output.getBuffer(), 0, output.getBufferLength());
                }
            }

            bitstream.closeFrame();
        } catch (RuntimeException ex) {
            throw new JavaLayerException("Exception decoding audio frame", ex);
        }
        return true;
    }


    /**
     * skips over a single frame
     *
     * @return false    if there are no more frames to decode, true otherwise.
     */
    private boolean skipFrame() throws JavaLayerException {
        Header h = bitstream.readFrame();
        if (h == null) return false;
        bitstream.closeFrame();
        return true;
    }

    /**
     * Plays a range of MPEG audio frames
     *
     * @param start The first frame to play
     * @param end   The last frame to play
     * @return true if the last frame was setPlayed, or false if there are more frames.
     */
    public boolean play(final int start, final int end) throws JavaLayerException {
        boolean ret = true;
        int offset = start;
        while (offset-- > 0 && ret) ret = skipFrame();
        return play(end - start);
    }

    /**
     * Constructs a <code>PlaybackEvent</code>
     */
    private PlaybackEvent createEvent(int id) {
        return createEvent(audio, id);
    }

    /**
     * Constructs a <code>PlaybackEvent</code>
     */
    private PlaybackEvent createEvent(AudioDevice dev, int id) {
        return new PlaybackEvent(this, id, dev.getPosition());
    }

    /**
     * gets the <code>PlaybackListener</code>
     */
    public PlaybackListener getPlayBackListener() {
        return listener;
    }

    /**
     * sets the <code>PlaybackListener</code>
     */
    public void setPlayBackListener(PlaybackListener listener) {
        this.listener = listener;
    }

    public int getPosition() {

        return audio.getPosition();
    }

    /**
     * closes the player and notifies <code>PlaybackListener</code>
     */
    public int stop() {
        if (listener != null)
            listener.playbackFinished(createEvent(PlaybackEvent.STOPPED));
        close();
        return lastPosition;
    }

    /**
     *
     * @return true if a song is being played at the moment of request.
     */

    public boolean isPlaying() {
        return playing;
    }

    /**
     *
     * @return the song being currently played.
     */
    public Mp3FileAdvanced getCurrent() {
        return current;
    }


    /**
     *
     * @return a value of gain.
     */
    public float getVolume() {
        if (audio != null)
            return audio.getVolume();
        else return 0;
    }

    /**
     *
     * @param newVolume a value to set a new gain.
     */
    public void setVolume(float newVolume) {
        if (audio instanceof JavaSoundAudioDevice) {
            JavaSoundAudioDevice jsAudio = (JavaSoundAudioDevice) audio;
            jsAudio.setLineGain(newVolume);
        }
    }

    /**
     *
     * @return The time left until the song finishes.
     */
    public long getTimeLeft() {
        return (timeStarted + current.getLengthInMilliseconds()) - new Date().getTime();
    }
}
