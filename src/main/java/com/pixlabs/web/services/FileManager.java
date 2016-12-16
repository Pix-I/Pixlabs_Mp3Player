package com.pixlabs.web.services;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * Created by pix-i on 24/11/2016.
 */
public interface FileManager {



    void store(MultipartFile file);

    void store(MultipartFile[] files);

    void deleteAll();

    Path getSoundPath();

    void setNewSoundPath(Path path);

    void setNewMediaPath(Path path);

    Path getMediaPath();
}
