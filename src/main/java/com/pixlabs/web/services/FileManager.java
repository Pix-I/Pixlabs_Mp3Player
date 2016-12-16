/*
 *     This program, the Pixlabs_Player is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
