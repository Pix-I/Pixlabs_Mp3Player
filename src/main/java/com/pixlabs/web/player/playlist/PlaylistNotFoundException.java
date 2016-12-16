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

package com.pixlabs.web.player.playlist;

/**
 * Created by pix-i on 30/11/2016.
 */
public class PlaylistNotFoundException extends Exception {

    public PlaylistNotFoundException(String msg) {
        super(msg);
    }

    public PlaylistNotFoundException() {
        super();
    }

}
