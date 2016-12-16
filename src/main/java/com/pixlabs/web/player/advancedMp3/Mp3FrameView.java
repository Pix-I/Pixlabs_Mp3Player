package com.pixlabs.web.player.advancedMp3;

import javazoom.jl.decoder.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * Created by pix-i on 05/12/2016.
 */
public class Mp3FrameView {


    private static Mp3FileAdvanced mp3file;

    private long length;
    private double interval;


    public Mp3FrameView() {
    }

    public static LinkedList<Integer> getFrameView(Mp3FileAdvanced mp3) {
        mp3file = mp3;

        try {
            return getFrameView(new FileInputStream(Paths.get(mp3file.getFilename()).toFile()));
        } catch (JavaLayerException | FileNotFoundException e) {
            e.printStackTrace();
        }


        return null;
    }


    private static LinkedList<Integer> getFrameView(InputStream stream) throws JavaLayerException {
        Decoder decoder = new Decoder();
        LinkedList<Integer> audioList = new LinkedList<>();


        try {
            Bitstream mpegStream = new Bitstream(stream);
            for (int i = 0; i < 1500; i++) {


                Header h = mpegStream.readFrame();
                if (h != null) {

                    SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, mpegStream);

                    short[] buffer = output.getBuffer();
                    int info = 0;
                    for (short s = 0; s < buffer.length; s += buffer.length / 5) {
                        info += buffer[s];
                    }
                    audioList.add(info);
                    mpegStream.closeFrame();
                    for (int j = 0; j < (mp3file.getFrameCount() / 1500) - 1; j++) {
                        mpegStream.readFrame();
                        mpegStream.closeFrame();
                    }
                }
            }
            mpegStream.close();
        } catch (RuntimeException e) {
            throw new JavaLayerException("Exception decoding audio frame", e);
        }
        return audioList;
    }


}
