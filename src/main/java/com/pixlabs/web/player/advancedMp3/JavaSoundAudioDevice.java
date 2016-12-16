/*
 * 11/26/04		Buffer size modified to support JRE 1.5 optimizations.
 *              (CPU usage < 1% under P4/2Ghz, RAM < 12MB).
 *              jlayer@javazoom.net
 * 11/19/04		1.0 moved to LGPL.
 * 06/04/01		Too fast playback fixed. mdm@techie.com
 * 29/01/00		Initial version. mdm@techie.com
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */

package com.pixlabs.web.player.advancedMp3;

import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.JavaLayerException;

import javax.sound.sampled.*;

/**
 * The <code>JavaSoundAudioDevice</code> implements an audio
 * device by using the JavaSound API, and allows to change the gain (volume) of the output source.
 *
 * @author Mat McGowan & Pix-I
 *
 * @since 0.0.8
 */
class JavaSoundAudioDevice extends AudioDeviceBase {
    private static float gain = 0;
    private SourceDataLine source = null;
    private AudioFormat fmt = null;
    private byte[] byteBuf = new byte[4096];

    private AudioFormat getAudioFormat() {
        if (fmt == null) {
            Decoder decoder = getDecoder();
            fmt = new AudioFormat(decoder.getOutputFrequency(),
                    16,
                    decoder.getOutputChannels(),
                    true,
                    false);
        }
        return fmt;
    }


    private DataLine.Info getSourceLineInfo() {
        AudioFormat fmt = getAudioFormat();
        //DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt, 4000);

        return new DataLine.Info(SourceDataLine.class, fmt);
    }



    protected void openImpl()
            throws JavaLayerException {
    }

    // createSource fix.
    private void createSource() throws JavaLayerException {
        Throwable t = null;
        try {
            Line line = AudioSystem.getLine(getSourceLineInfo());
            if (line instanceof SourceDataLine) {
                source = (SourceDataLine) line;
                //source.open(fmt, millisecondsToBytes(fmt, 2000));
                source.open(fmt);
                /*
                if (source.isControlSupported(FloatControl.Type.MASTER_GAIN))
                {
					FloatControl c = (FloatControl)source.getControl(FloatControl.Type.MASTER_GAIN);
                    c.setValue(c.getMaximum());
                }*/
                source.start();

                FloatControl volControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
                volControl.setValue(Math.min(Math.max(gain, volControl.getMinimum()), volControl.getMaximum()));
            }
        } catch (RuntimeException | LinkageError | LineUnavailableException ex) {
            t = ex;
        }
        if (source == null) throw new JavaLayerException("cannot obtain source audio line", t);
    }



    protected void closeImpl() {
        if (source != null) {
            source.close();
        }
    }

    protected void writeImpl(short[] samples, int offs, int len)
            throws JavaLayerException {
        if (source == null)
            createSource();

        byte[] b = toByteArray(samples, offs, len);
        source.write(b, 0, len * 2);
    }

    private byte[] getByteArray(int length) {
        if (byteBuf.length < length) {
            byteBuf = new byte[length + 1024];
        }
        return byteBuf;
    }

    private byte[] toByteArray(short[] samples, int offs, int len) {
        byte[] b = getByteArray(len * 2);
        int idx = 0;
        short s;
        while (len-- > 0) {
            s = samples[offs++];
            b[idx++] = (byte) s;
            b[idx++] = (byte) (s >>> 8);
        }
        return b;
    }

    protected void flushImpl() {
        if (source != null) {
            source.drain();
        }
    }

    public int getPosition() {
        int pos = 0;
        if (source != null) {
            pos = (int) (source.getMicrosecondPosition() / 1000);
        }
        return pos;
    }

    @Override
    public float getVolume() {
        return gain;
    }


    void setLineGain(float newGain) {
        if (source != null) {
            gain = newGain;
            FloatControl volControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
            volControl.setValue(Math.min(Math.max(newGain, volControl.getMinimum()), volControl.getMaximum()));
        }
    }


}
