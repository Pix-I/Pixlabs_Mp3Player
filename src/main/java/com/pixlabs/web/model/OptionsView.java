package com.pixlabs.web.model;

import com.pixlabs.web.player.playlist.Playlists;
import com.pixlabs.web.services.FileManager;
import com.pixlabs.web.utils.Mp3Finder;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by pix-i on 08/12/2016.
 */

/**
 * The folderview is a class destined to return an array containing lists of folders.
 * These lists will be displayed to the client, so that he can interact with them.
 */


@Component("OptionsView")
@Qualifier("OptionsView")
public class OptionsView {

    private final String MEDIAPATH = "MEDIAPATH__";
    private final String SOUNDPATH = "SOUNDPATH__";
    private final String PARENT = "PARENT__";
    private final String FILE = "F__";
    private FileManager fileManager;
    private Path currentFolderPath;
    private Playlists playlists;

    @Inject
    @Qualifier("FileManager")
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }


    /**
     * Resets the currentFolderPath to the soundPath from the Filemanager
     */
    public void reset() {

        currentFolderPath = Paths.get(FilenameUtils.normalize(fileManager.getSoundPath().toString())).toAbsolutePath().getParent();
    }

    private String getFileName(Path entry) {
        String file = entry.toString();
        for (int i = file.length() - 2; i > 0; i--) {
            if ('/' == file.charAt(i)) {
                file = file.substring(i);
                break;
            }
        }
        return file;

    }

    /**
     * Adds songs from the current folder path
     */
    public void addSongsFromCurrent() {
        Mp3Finder.mp3InDirectories(currentFolderPath);
    }


    public ArrayList<LinkedList<String>> getFolderView(Path path) {

        int depth = 3;
        ArrayList<LinkedList<String>> folderView = new ArrayList<>();
        Path folder = path.toAbsolutePath();
        Path child = folder.getParent();
        if (child.toString().equals("/")) {
            depth--;
        }
        for (int i = depth - 1; i >= 0; i--) {
            LinkedList<String> folderList = new LinkedList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {

                for (Path entry : stream) {
                    entry = entry.toAbsolutePath();
                    entry = Paths.get(FilenameUtils.normalize(entry.toString()));
                    if (entry.equals(Paths.get(FilenameUtils.normalize(
                            fileManager.getMediaPath().toAbsolutePath().toString())))) {
                        folderList.add(MEDIAPATH + getFileName(entry));
                    } else if (entry.equals(Paths.get(FilenameUtils.normalize(
                            fileManager.getSoundPath().toAbsolutePath().toString())))) {
                        folderList.add(SOUNDPATH + getFileName(entry));
                    } else if (entry.equals(child)) {
                        folderList.add(PARENT + getFileName(entry));
                    } else if (!Files.isHidden(entry) && Files.isDirectory(entry)) {
                        folderList.add(getFileName(entry));
                    } else if (i == depth - 1 && !Files.isHidden(entry)) {
                        folderList.add(FILE + getFileName(entry));
                    }
                }

            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
            folderView.add(0, folderList);
            child = folder;
            folder = folder.getParent();
            if (folder == null) {
                break;
            }
        }
        return folderView;
    }


    /**
     * @return a new Arraylist of linkedlists to display the folderview to the client.
     */
    public ArrayList<LinkedList<String>> getFolderView() {
        return getFolderView(currentFolderPath);
    }


    /**
     *  Sets a new soundpath, given information about a new path from the controller.
     * @param newPath Foldername.
     * @param folder Level compared to the folder where the folderview originated from.
     */

    public void setNewSoundsPath(String newPath, String folder) {
        if (!newPath.startsWith("F__"))
            fileManager.setNewSoundPath(parsePath(newPath, folder));

    }

    /**
     *  Sets a new mediapath, given information about a new path from the controller.
     * @param newPath Foldername.
     * @param folder Level compared to the folder where the folderview originated from.
     */
    public void setNewMediaPath(String newPath, String folder) {
        if (!newPath.startsWith("F__"))
            fileManager.setNewMediaPath(parsePath(newPath, folder));
    }

    private Path parsePath(String newPath, String folder) {
        if (!newPath.startsWith("F__"))
            return currentFolderPath;
        Path nPath = currentFolderPath.toAbsolutePath();
        int folderLevel = Integer.parseInt(folder);
        for (; 2 - folderLevel > 0; folderLevel++) {
            System.out.println((2 - folderLevel));
            nPath = nPath.getParent();
        }
        return nPath.resolve(erasePathInfo(newPath));
    }


    private String erasePathInfo(String path) {
        if (path.contains(PARENT)) {
            path = path.replace(PARENT, "");
        } else if (path.startsWith(MEDIAPATH)) {
            path = path.replace(MEDIAPATH, "");
        } else if (path.startsWith(SOUNDPATH)) {
            path = path.replace(SOUNDPATH, "");
        } else if (path.startsWith(FILE)) {
            path = path.replace(FILE, "");
        }
        return path;

    }

    /**
     * Sets a new current folder, so that a new folderview can be generated from this folder.
     * Needs to be changed, to make the server read the original folder from the client.
     * @param newPath Foldername.
     * @param folder Level compared to the folder where the folderview originated from.
     */
    public void navigateToFolder(String newPath, String folder) {
        try {
            currentFolderPath = parsePath(newPath, folder).toAbsolutePath();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    /**
     * Loads mp3 from some folder information that'll be parsed into a path into the
     * playlists songlist.
     * @param newPath Foldername.
     * @param folder Level compared to the folder where the folderview originated from.
     */
    public void loadMp3(String newPath, String folder) {
        playlists.addSoundFiles(parsePath(newPath, folder));
    }

    @Inject
    @Qualifier("Playlists")
    public void setPlaylists(Playlists playlists) {
        this.playlists = playlists;
    }

    public String getSoundFolderName() {
        return fileManager.getSoundPath().toString();
    }

    public String getMediaFolderName() {
        return fileManager.getMediaPath().toString();
    }

}
