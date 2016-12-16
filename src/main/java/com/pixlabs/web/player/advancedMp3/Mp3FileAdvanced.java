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

package com.pixlabs.web.player.advancedMp3;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * An extension to the Mp3file by mp3agic. It gives you default information if the Id3v2 tag isn't availlable.
 *
 */
public class Mp3FileAdvanced extends Mp3File {

    private static int idCounter = 0;
    private int id;
    private Path imgPath = Paths.get("./images");
    private int nPlayed = 0;

    /**
     * @param path that'll make a mp3FileAdvanced object.
     * @throws InvalidDataException
     * @throws IOException             if the file doesn't exist.
     * @throws UnsupportedTagException If it's not an mp3File.
     */
    public Mp3FileAdvanced(Path path) throws InvalidDataException, IOException, UnsupportedTagException {
        super(path.toFile());
        this.id = idCounter++;
    }

    /**
     * @param file that'll make a mp3FileAdvanced object.
     * @throws InvalidDataException
     * @throws IOException             if the file doesn't exist.
     * @throws UnsupportedTagException If it's not an mp3File.
     */
    public Mp3FileAdvanced(File file) throws InvalidDataException, IOException, UnsupportedTagException {
        super(file);
        this.id = idCounter++;
    }

    /**
     * @param string that'll make a mp3FileAdvanced object.
     * @throws InvalidDataException
     * @throws IOException             if the file doesn't exist.
     * @throws UnsupportedTagException If it's not an mp3File.
     */
    public Mp3FileAdvanced(String string) throws InvalidDataException, IOException, UnsupportedTagException {
        super(string);
        this.id = idCounter++;
    }


    /**
     *
     * @return Artist, '__' if the id3v2tag is empty.
     */
    public String getArtist() {
        if (this.hasId3v2Tag() && this.getId3v2Tag().getAlbumImage() != null) {
            return this.getId3v2Tag().getArtist();
        } else {
            return "__";
        }
    }

    /**
     *
     * @return The title of the mp3file.
     */

    public String getTitle() {
        if (this.hasId3v2Tag() && this.getId3v2Tag().getTitle() != null && !this.getId3v2Tag().getTitle().equals("")) {
            return this.getId3v2Tag().getTitle();
        } else {
            String title = this.getFilename();
            for (int i = title.length() - 1; i > 0; i--) {
                if (title.charAt(i) == '/') {
                    return title.substring(i + 1, title.length());
                }
            }
            return title;
        }
    }


    /**
     * The id is to make them easier to iterate trough, thus making them easier to find.
     * @return returns the Id of the mp3File.
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return an image in the form of a byte array.
     */
    public byte[] getAlbumCover() {
        if (hasId3v2Tag() && getId3v2Tag().getAlbumImage() != null) {
            return getId3v2Tag().getAlbumImage();
        }
        Path defaultCoverPath = imgPath.resolve("default_cover.png");
        try {
            return Files.readAllBytes(defaultCoverPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int getnPlayed() {
        return nPlayed;
    }

    /**
     *
     * @return Returns the genre description of the file.
     */

    public String getGenre() {
        if (hasId3v2Tag() && getId3v2Tag().getGenreDescription() != null) {
            return getId3v1Tag().getGenreDescription();

        }
        return "__";
    }

    /**
     * @return Adds 1 to the counter of the number of times this has been played.
     */
    public Mp3FileAdvanced setPlayed() {
        nPlayed++;
        return this;
    }

    /**
     * @return Returns the album name.
     */

    public String getAlbum() {
        if (hasId3v2Tag() && getId3v2Tag().getAlbum() != null) {
            return getId3v1Tag().getAlbum();

        }
        return "__";
    }
}
