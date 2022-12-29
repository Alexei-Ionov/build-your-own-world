package byow.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "You", "npc_wizzard.png");
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "Wall", "Wall_front.png");
    public static final TETile WALLFLAG = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "Wall with a flag", "wall_flag_red.png");
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "Muddy Floor", "floor_mud_e.png");
    public static final TETile SKULL = new TETile('·', new Color(128, 192, 128), Color.black,
            "Skull", "skull.png");
    public static final TETile CLOSED_TREASURE_CHEST = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "Closed chest", "chest_golden_closed.png");
    public static final TETile OPEN_FULL_TREASURE_CHEST = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "Open full chest", "chest_open_full.png");
    public static final TETile OPEN_EMPTY_TREASURE_CHEST = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "Open empty chest", "chest_open_empty.png");


    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "Nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "Grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "Water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "Flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door", "door_closed.png");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "Sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "Mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "Tree");
    public static final TETile LIGHT_SWITCH = new TETile('⚡', Color.yellow, Color.black, "Light switch");

    public static final TETile KEY = new TETile('♛', Color.yellow, Color.black, "Key", "Key.png");
    public static final TETile TORCH = new TETile('♛', Color.yellow, Color.black, "torch", "torch_8.png");
    public static final TETile MONSTER = new TETile('♛', Color.yellow, Color.black, "Monster", "monster_demon.png");


    public static final TETile TOGGLE_FLOOR = new TETile('.', new Color(128, 100, 250), Color.black,
            "Monster Path");

    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "Unlocked Door", "door_open.png");



}


