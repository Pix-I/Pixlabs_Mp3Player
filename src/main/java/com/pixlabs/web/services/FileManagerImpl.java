package com.pixlabs.web.services;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.pixlabs.web.player.advancedMp3.Mp3FileAdvanced;
import com.pixlabs.web.player.playlist.Playlists;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by pix-i on 24/11/2016.
 */


/**
 * Manages the files for this program.
 */


@Service("FileManager")
@ManagedBean
public class FileManagerImpl implements FileManager {


    private Path soundPath;
    private Path mediaPath;
    private Playlists playlists;

    public FileManagerImpl() {
        init();
    }

    @Inject
    @Qualifier("Playlists")
    public void setPlaylists(Playlists playlists) {
        this.playlists = playlists;
    }

    @Override
    public Path getSoundPath() {
        return this.soundPath;
    }

    @Override
    public void setNewSoundPath(Path path) {
        if (!path.toAbsolutePath().equals(soundPath.toAbsolutePath()) &&
                !path.toAbsolutePath().equals(mediaPath.toAbsolutePath()) &&
                Files.isDirectory(path)) {
            soundPath = path;
            playlists.resetSoundFiles(path);
        }

    }

    @Override
    public void setNewMediaPath(Path path) {
        if (!path.toAbsolutePath().equals(soundPath.toAbsolutePath())
                && !path.toAbsolutePath().equals(mediaPath.toAbsolutePath())
                && Files.isDirectory(path))
            this.mediaPath = path;
    }

    @Override
    public Path getMediaPath() {
        return mediaPath;
    }

    /**
     *
     */
    private void init() {
        this.soundPath = Paths.get("./music");
        this.mediaPath = Paths.get("./media");
        if (!Files.exists(mediaPath)) {
            try {
                Files.createDirectory(Paths.get("./media"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            mediaPath = Paths.get("./media");
        }
        if (!Files.exists(soundPath)) {
            try {
                Files.createDirectory(Paths.get("./music"));
                soundPath = Paths.get("./music");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Stores a file into the dedicated folder. (Either music/other media)
     *
     * @param file File to be stored.
     */

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            if (file.getOriginalFilename().endsWith(".mp3")) {
                storeMp3(file);
            } else {
                storeOthers(file);
            }

        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        } catch (UnsupportedTagException | InvalidDataException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores an array of files.
     *
     * @param files Array of files.
     */
    @Override
    public void store(MultipartFile[] files) {
        for (MultipartFile file : files
                ) {
            store(file);
        }
    }

    private void storeMp3(MultipartFile file) throws IOException, InvalidDataException, UnsupportedTagException {
        Path toCopy = this.soundPath.resolve(indexFilename(file, soundPath));
        Files.copy(file.getInputStream(), toCopy);
        playlists.addSong(new Mp3FileAdvanced(toCopy.toFile()));
    }

    private void storeOthers(MultipartFile file) throws IOException {
        Path toCopy = this.mediaPath.resolve(indexFilename(file, mediaPath));
        Files.copy(file.getInputStream(), toCopy);
    }


    private String indexFilename(MultipartFile file, Path path) {
        String newFilename = file.getOriginalFilename();
        int index = 1;
        String tempName = removeExtension(newFilename);
        while (Files.exists(path.resolve(newFilename))) {
            newFilename = tempName + "(" + 1 + ")" + getExtension(file.getOriginalFilename());
            if (Files.exists(path.resolve(newFilename))) {
                newFilename = tempName + "(" + ++index + ")" + getExtension(file.getOriginalFilename());
            }
        }
        return newFilename;
    }

    private String getExtension(String file) {
        for (int i = file.length() - 1; i > 0; i--) {
            if (file.charAt(i) == '.') {
                return file.substring(i, file.length());
            }
        }
        return file;
    }


    private String removeExtension(String file) {
        for (int i = file.length() - 1; i > 0; i--) {
            if (file.charAt(i) == '.') {
                return file.substring(0, i);
            }
        }
        return file;
    }

    /**
     * Deletes everything in the soundPath
     */

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(soundPath.toFile());
    }

}
