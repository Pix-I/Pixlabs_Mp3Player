package com.pixlabs.web.utils;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.pixlabs.web.player.advancedMp3.Mp3FileAdvanced;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by pix-i on 24/11/2016.
 */

/**
 * A helper class that returns lists of Mp3AdvancedFiles found in some directories.
 */


public class Mp3Finder {

    /**
     * @param path Path of the directory that should be looked into.
     * @return a linkedlist containing all the Mp3 files found in the directory.
     * @throws NotDirectoryException The given path was not a directory.
     */

    public static LinkedList<Mp3FileAdvanced> mp3InDirectory(Path path) throws NotDirectoryException {
        if (!Files.isDirectory(path))
            throw new NotDirectoryException("The chosen path does not represent a directory");
        LinkedList<Mp3FileAdvanced> list = new LinkedList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.mp3")) {
            for (Path entry : stream) {
                //       if(!entry.startsWith("."))
                list.add(new Mp3FileAdvanced(entry.toFile()));
            }

        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * @param path Path of the directory that should be looked into.
     * @return a linkedlist containing all the Mp3 files found in the directory and subdirectories.
     */

    public static LinkedList<Mp3FileAdvanced> mp3InDirectories(Path path) {
        Iterator it = FileUtils.iterateFiles(new File(path.toString()), new String[]{"mp3"}, true);
        LinkedList<Mp3FileAdvanced> mp3List = new LinkedList<>();
        while (it.hasNext()) {
            File file = (File) it.next();
            try {
                mp3List.add(new Mp3FileAdvanced(file));
            } catch (InvalidDataException | IOException | UnsupportedTagException e) {
                e.printStackTrace();
            }
        }
        return mp3List;
    }


}
