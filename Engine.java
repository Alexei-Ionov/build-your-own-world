package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayDeque;
import java.util.ArrayList;

import java.lang.Math;
public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 100;
    public static final int HEIGHT = 60;
    public static final String LOAD = "l";
    public static final String QUIT = "q";
    public static final String NEW_GAME = "n";
    public static final String ABOUT = "a";
    public static final String WordFile = "WorldFile.txt";

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard(God god) {
        /*
        steps I need to take:
        1.) upon calling interact w/ keyboard: I need the program to draw a main menu
        2.) once the main menu is drawn, program should wait to get user input (i.e. l or :q or n)
            * if l --> load up prior world, render it, then wait for user input
            * if n --> ask user to input seed... creates world once "s" is inputted following the seed
            * if :q --> save game and quit

         */
        MainMenu menu = new MainMenu(WIDTH, HEIGHT);
        String strVal = null;
        boolean pressed = false;
        while (!pressed) {
            if (StdDraw.hasNextKeyTyped()) {
                char val = StdDraw.nextKeyTyped();
                strVal = Character.toString(val).toLowerCase();
                if (validKeys(strVal)) {
                    pressed = true;
                }
            }
        }
        switch (strVal) {
            case NEW_GAME:
                menu.drawEnterString();
                String seed = menu.interactN();
                god.createWorld(Long.parseLong(seed), null, WIDTH, HEIGHT);
                //ter.render frame here
                inputToMoves(god, seed, "");

            case LOAD:
                String[] info = LoadWork();
                if (info == null) {
                    System.exit(0);
                }
                String prevSeed = info[0];
                String moves = info[1];
                god.createWorld(Long.parseLong(prevSeed), moves, WIDTH, HEIGHT);
                inputToMoves(god, prevSeed, moves);

            case QUIT:
                System.exit(0);

            case ABOUT:
                menu.drawAboutInfo();
        }
    }

    private void inputToMoves(God god, String seed, String moves) {
        WorldCreator worldGen = god.returnWorldCreator();
        TETile[][] res = worldGen.returnWorld();
        ArrayDeque<ArrayList<Integer>> monsterPath;
        HUD hud = new HUD(WIDTH, HEIGHT, res);

        int[] monsterPos;
        int[] userPos;
        int userHealth;
        boolean visibleWorld;
        String strVal;
        String colon = ":";
        boolean prevIsColon = false;

        boolean monsterStarted = false;
        int cnt = 0;
        int timer = 0;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char val = StdDraw.nextKeyTyped();
                strVal = Character.toString(val).toLowerCase();
                if (validMove(strVal)) {
                    worldGen.Move(strVal);
                    moves += strVal;
                    res = worldGen.returnWorld();

                }
                if (strVal.equals("q") && prevIsColon) {
                    //quit and save
                    SaveWork(seed, moves);
                    System.exit(0);
                }
                prevIsColon = strVal.equals(colon);
            }
            visibleWorld = worldGen.returnVisibleWorld();
            monsterPos = worldGen.returnMonsterPos();
            userPos = worldGen.returnUserPos();
            if (monsterPos != null) {
                int distanceX = Math.abs(monsterPos[0] - userPos[0]);
                int distanceY = Math.abs(monsterPos[1] - userPos[1]);

                if (distanceY + distanceX < 5 && cnt % 50 == 0) {
                    worldGen.monsterChase();

                } else if (timer >= 250) {
                    worldGen.findPath();
                    timer = 0;
                }
            }
            monsterPath = worldGen.returnMonsterPath();
            if (!monsterPath.isEmpty() && (!monsterStarted || cnt >= 50)) {
                ArrayList<Integer> move = monsterPath.poll();
                worldGen.moveMonster(move);
                monsterStarted = true;
                cnt = 0;
            }
            userHealth = worldGen.returnUserHealth();
            ter.renderWorld(res, worldGen.returnUserPos(), hud, visibleWorld, monsterPos, userHealth);
            cnt += 1;
            timer += 1;
        }
    }
    private boolean validKeys(String key) {
        return key.equals(LOAD) || key.equals(NEW_GAME) || key.equals(ABOUT) || key.equals(QUIT);
    }



    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, running both of these:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString (String input, God god) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        // If the first char of input string is 'n', THEN that must mean we are generating a NEW world
        //if it is some other letter then that must mean we are logging into a pre-existing world
        String inputL = input.toLowerCase();
        int index = 0;
        StringBuilder seed = new StringBuilder();
        if (Character.toString(inputL.charAt(0)).equals("n")) {
            //skips the n
            index += 1;
            String letter = Character.toString(inputL.charAt(index));

            while (!letter.equals("s")) {
                seed.append(letter);
                index += 1;
                letter = Character.toString(inputL.charAt(index));
            }
            //skips the s
            index += 1;
        }
        String strSeed = seed.toString();
        StringBuilder strMoves = new StringBuilder();


        if (Character.toString(inputL.charAt(0)).equals("l")) { // we are loading in a prev world
            //search txt.file
                String[] info = LoadWork();
                //cannot load into a world that has not been started. system exits
                if (info == null) {
                    System.exit(0);
                }
                strSeed = info[0];
                String Moves = info[1];
                for (int idx = 0; idx < Moves.length(); idx++) {
                    strMoves.append(Character.toString(Moves.charAt(idx)));
                }
            }
        Long longSeed = Long.parseLong(strSeed);
        // at this point, if im loading in a previous world, my MOVES list will contain all the previous moves I've made
        // I also have refernece to my seed, regardles of whetehr im loading in or starting anew
        //now I need to gather all the moves
        for (int j = index; j < inputL.length(); j++) {
            String move = Character.toString(input.charAt(j));
            if (move.equals(":") && j < inputL.length() - 1) {
                String nextVal = Character.toString(input.charAt(j + 1));
                if (nextVal.equals("q")) {
                    String moves = strMoves.toString();
                    SaveWork(strSeed, moves);
                    return null;
                }
            } else if (validMove(move)) {
                strMoves.append(move);
            }
        }
        String moves = strMoves.toString();
        god.createWorld(longSeed, moves, WIDTH, HEIGHT);
        WorldCreator worldGen = god.returnWorldCreator();
        TETile[][] res = worldGen.returnWorld();
        int[] monsterPos = worldGen.returnMonsterPos();
        int userHealth = worldGen.returnUserHealth();
        HUD hud = new HUD(WIDTH, HEIGHT, res);
        ter.initialize(WIDTH, HEIGHT);
        ter.renderWorld(res, worldGen.returnUserPos(), hud, true, monsterPos, userHealth);
        return res;
    }
    private boolean validMove(String move) {
        return move.equals("w") || move.equals("s") || move.equals("d") || move.equals("a") || move.equals("o") || move.equals("p") || move.equals("t");
    }

    private void SaveWork(String seed, String moves) {
        Out out = new Out(WordFile);
        In in = new In(WordFile);
        while (in.hasNextLine()) {
            if (in.isEmpty()) {
                break;
            }
            in.readLine();
        }
        out.println(seed + "," + moves);
    }
    private String[] LoadWork() {
        In in = new In(WordFile);
        String line = null;
        while (in.hasNextLine()) {
            if (in.isEmpty()) {
                break;
            }
            line = in.readLine();
        }
        // now I have the info from the most previous world!
        if (line != null) {
            String[] info = line.split("[,]+");
            return info;
        }
        //cannot load a world wihout having saved one first!
        return null;
    }
}

