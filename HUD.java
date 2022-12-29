package byow.Core;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.Color;
import java.awt.Font;

public class HUD {
    private Integer topH;
    private int left;
    private Integer middle;
    private int right;
    private int width;
    private int height;

    public static final String HEART = "♡";
    public static final String MESSAGE = "To Save & Quit Press ':q'";
    public static final String SPOOKY_MSG1 = "Oh No... What is Happening?";
    public static final String SPOOKY_MSG2 = "M U A H A H A H A H A H";
    public static final String NEED_CHEST_KEY_MSG = "It seems like you need a Key to open this Chest...";
    public static final String MASTER_DOOR_MSG = "Now is not the time...";
    public static final String SPOOKY_MSG3 = "You will never get out of here alive!";

    public static final String POTION_MSG = "You have found a Visibility Potion";
    public static final String POTION_MSG2 = "You can now toggle dungeon visibility by pressing 'p'";
    public static final int TEXT_SIZE = 20;
    public static final int BIG_TEXT = 40;
    public static final Double IMAGE_SCALING_FACTOR = 10.0;
    public static final String POTION = "Visibility: ON";
    public static final String FINAL_MSG1 = "NOOOOO HOW IS THIS POSSIBLE!?";

    public static final String FINAL_MSG2 = "Well, congrats on making it... for now";
    public static final String FINAL_MSG3 = "CONGRATS";
    public static final String FINAL_MSG4 = "YOU WON";
    private int topM;
    private TETile[][] world;
    private int middleH;

    private int lastHeartPos;

    private boolean containsPotion;

    public HUD(int width, int height, TETile[][] world) {

        //creates the HUD bar at the top of the screen
        this.width = width;
        this.height = height;
        this.topH = height - height / 30;
        this.topM = this.topH - (this.topH / 10);
        this.middle = width / 2;
        containsPotion = false;
        left = width / 50;
        this.world = world;
        right = this.width - (this.width / 10);
        middleH = height / 2;

    }
    //user starts off with 5 health
    public void drawHealth(int health) {
        int posX = left;
        for (int i = 0; i < health; i++) {
            SetUp();
            StdDraw.text(posX, topH, HEART);
            posX += 1;
        }
        lastHeartPos = posX;
    }
    public void drawVisibility() {
        SetUp();
        StdDraw.text(lastHeartPos + 10, topH, POTION);
    }
    public void drawMessage() {
        SetUp();
        StdDraw.text(this.middle, topH, MESSAGE);
    }

    private void spookyMsgSetUp() {
        StdDraw.setPenColor(Color.PINK);
        Font font = new Font("Monaco", Font.BOLD + Font.ITALIC, BIG_TEXT);
        StdDraw.setFont(font);
    }
    private void spookyMSGHelper(String message) {
        StdDraw.clear(Color.BLACK);
        spookyMsgSetUp();
        StdDraw.text(this.middle, middleH, message);
        StdDraw.show();
        StdDraw.pause(3000);

    }
    public void spookyMSG() {
        spookyMSGHelper(SPOOKY_MSG1);
        spookyMSGHelper(SPOOKY_MSG2);
        spookyMSGHelper(SPOOKY_MSG3);
        flash();
    }
    private void flash() {
        boolean flip = true;
        for (int i = 0; i < 10; i++) {
            if (flip) {
                StdDraw.clear(Color.BLACK);
            } else {
                StdDraw.clear(Color.DARK_GRAY);
            }
            flip = !flip;
            StdDraw.show();
            StdDraw.pause(200);
        }
    }

    public void needChestKeyMsg() {
        needKeyMSG(NEED_CHEST_KEY_MSG);
    }
    public void playMasterDoorMsg() {
        needKeyMSG(MASTER_DOOR_MSG);
    }

    private void needKeyMSG(String message) {
        StdDraw.clear(Color.BLACK);
        spookyMsgSetUp();
        StdDraw.text(this.middle, middleH, message);
        StdDraw.show();
        StdDraw.pause(5000);

    }
    public void drawSequence(boolean visible, int health) {
        drawHealth(health);
        drawMessage();
        drawPointedAt();
        if (visible) {
            drawVisibility();
        }
    }
    private void drawPointedAt() {
        SetUp();
        Double mousePosX = StdDraw.mouseX();
        Double mousePosY = StdDraw.mouseY();
        int[] pos = findTile(mousePosX, mousePosY);
        if (pos[0] < 0 || pos[0] >= world.length || pos[1] < 0 || pos[1] >= world[0].length) {
            StdDraw.text(right, topH, "Out of this World");
        } else {
            TETile tile = world[pos[0]][pos[1]];
            StdDraw.text(right, topH, tile.description());
        }
    }
    private int[] findTile(Double mousePosX, Double mousePosY) {
        int x = (int) Math.floor(mousePosX);
        int y = (int) Math.floor(mousePosY);
        return new int[]{x, y};

    }
    private void SetUp() {
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, TEXT_SIZE);
        StdDraw.setFont(font);
    }

    public void drawItemsGained() {
        StdDraw.clear(Color.BLACK);
        containsPotion = true;
        SetUp();
        StdDraw.picture(this.middle, this.middleH, "flask_big_blue.png", IMAGE_SCALING_FACTOR, IMAGE_SCALING_FACTOR);
        StdDraw.text(this.middle, this.topH, POTION_MSG);
        StdDraw.text(this.middle, this.topM, POTION_MSG2);
        StdDraw.show();
        StdDraw.pause(5000);

    }
    public void drawFinalMsg() {
        finalMsgSet(Color.PINK, FINAL_MSG1, FINAL_MSG2);
        WIN();

    }
    private void finalMsgSet(Color color, String msg1, String msg2) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(color);
        Font font = new Font("Monaco", Font.BOLD, BIG_TEXT);
        StdDraw.setFont(font);
        StdDraw.text(this.middle, this.topH, msg1);
        StdDraw.text(this.middle, this.middleH, msg2);
        StdDraw.show();
        StdDraw.pause(5000);


    }
    private void WIN() {
        finalMsgSet(Color.YELLOW, FINAL_MSG3, FINAL_MSG4);
        System.exit(0);
    }
    public void GAME_OVER() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.RED);
        Font font = new Font("Monaco", Font.BOLD, BIG_TEXT);
        StdDraw.setFont(font);
        StdDraw.text(this.middle, this.middleH, "GAME OVER");
        StdDraw.show();
        StdDraw.pause(5000);
        System.exit(0);



    }


}
