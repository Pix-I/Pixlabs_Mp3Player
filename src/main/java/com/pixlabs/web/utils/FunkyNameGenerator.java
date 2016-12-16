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

package com.pixlabs.web.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by pix-i on 07/12/2016.
 */
public class FunkyNameGenerator {

    private static List<String> possibilities1 = new LinkedList<>();

    private static List<String> possibilities2 = new LinkedList<>();

    private static List<String> possibilities3 = new LinkedList<>();

    private static boolean init = false;

    public static void init() {
        possibilities1.add("Random");
        possibilities1.add("Funky");
        possibilities1.add("Hellish");
        possibilities1.add("Blue");
        possibilities1.add("White");
        possibilities1.add("Yellow");
        possibilities1.add("Tragic");
        possibilities1.add("Magic");
        possibilities1.add("Peaceful");
        possibilities1.add("Raging");
        possibilities1.add("Sleeping");
        possibilities1.add("Hopeless");
        possibilities1.add("Courageous");
        possibilities1.add("Tired");
        possibilities1.add("Lost");

        possibilities2.add("Fire");
        possibilities2.add("Water");
        possibilities2.add("Ninja");
        possibilities2.add("Samurai");
        possibilities2.add("Kid");
        possibilities2.add("Concert");
        possibilities2.add("Earth");
        possibilities2.add("Planet");
        possibilities2.add("Sea");
        possibilities2.add("Mountain");
        possibilities2.add("Country");


        possibilities3.add("Cat");
        possibilities3.add("Dog");
        possibilities3.add("Weasel");
        possibilities3.add("Lion");
        possibilities3.add("Tiger");
        possibilities3.add("Kangaroo");
        possibilities3.add("Panda");
        possibilities3.add("Zoo");
        possibilities3.add("Kitty");
        possibilities3.add("Puppy");
        possibilities3.add("Dolphin");

        init = true;


    }

    public static String getRandomName() {
        if (!init)
            init();
        Random random = new Random();
        return possibilities1.get(random.nextInt(possibilities1.size() - 1))
                + possibilities2.get(random.nextInt(possibilities2.size() - 1))
                + possibilities3.get(random.nextInt(possibilities3.size() - 1));
    }


    public static boolean isInit() {
        return init;
    }
}
