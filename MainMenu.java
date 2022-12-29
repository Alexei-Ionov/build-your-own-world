package byow.Core;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.Color;
import java.awt.Font;
import java.util.HashSet;

public class MainMenu {
    private int width;
    private int height;

    private int topH;

    private int middleH1;
    private int middleW;

    private int middleH5;

    private int middleH2;
    private int middleH3;
    private int middleH4;
    private String intro;
    private int seedEntry;
    private int left;

    public static final String HOW_TO_PLAY_MSG = "How to Play the Game:";
    public static final String POINT_1 = "1.) Use the W, S, D, A keys to move Up, Down, Right, and Left respectively";
    public static final String POINT_2 = "2.) To turn on light switches, open chests, and etc, hover mouse over desired object and press 'o'";
    public static final String POINT_3 = "3.) To toggle the monster's path, press 't'";
    public static final String POINT_4 = "4.) To toggle dungeon visibility, press 'p' (only applicable once potion is acquired)";

    public MainMenu(int width, int height) {
        this.width = width;
        this.height = height;
        this.left = this.width / 10;
        this.topH = this.height - (this.height / 4);
        this.middleH1 = this.height / 2;
        this.middleW = this.width / 2;
        this.middleH2 = this.middleH1 - (this.height / 20);
        this.middleH3 = this.middleH2 - (this.height / 20);
        this.middleH4 = this.middleH3 - (this.height / 20);
        this.middleH5 = this.middleH4 - (this.height / 20);
        this.intro = "CS61B: THE GAME";
        this.seedEntry = this.width / 4;
        drawMenu();
    }
    private void drawString(String s, int x, int y, int size) {
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, size);
        StdDraw.setFont(font);
        StdDraw.text(x, y, s);
    }
    private void drawOptions() {
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.ITALIC, 20);
        StdDraw.setFont(font);
        String s1 = "New Game (N)";
        String s2 = "Load Game (L)";
        String s3 = "Quit (Q)";
        String s4 = "About the Game (A)";
        StdDraw.text(this.middleW, this.middleH1, s1);
        StdDraw.text(this.middleW, this.middleH2, s2);
        StdDraw.text(this.middleW, this.middleH3 , s3);
        StdDraw.text(this.middleW, this.middleH4 , s4);
        StdDraw.show();
    }
    private void drawMenu() {
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        drawString(this.intro, this.middleW, this.topH, 30);
        drawOptions();
        StdDraw.show();
    }
    public void drawEnterString() {
        StdDraw.clear(Color.BLACK);
        drawString("Type To Enter Seed Below:", this.middleW, this.topH, 30);
        drawString("To Finish Entering Seed, Type 's'", this.middleW, this.topH - 5, 30);
        StdDraw.show();
    }
    public void drawSeed(String s) {
        drawString(s, this.seedEntry, this.middleH1, 15);
        StdDraw.show();
        this.seedEntry += 1;
    }
    public String interactN() {
        String enter = "s";
        String strVal;
        HashSet<String> validSeedInput = new HashSet<>();
        for (Integer i = 0; i <= 9; i++) {
            validSeedInput.add(Integer.toString(i));
        }
        int seedCnt = 0;

        StringBuilder seedBuilder = new StringBuilder();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char val = StdDraw.nextKeyTyped();
                strVal = Character.toString(val).toLowerCase();
                if (strVal.equals(enter) && seedCnt != 0) {
                    break;
                }
                if (validSeedInput.contains(strVal)) {
                    this.drawSeed(strVal);
                    seedBuilder.append(strVal);
                    seedCnt += 1;
                }
            }
        }
        return seedBuilder.toString();
    }

    public void interactA() {
        String strVal;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char val = StdDraw.nextKeyTyped();
                strVal = Character.toString(val).toLowerCase();
                if (strVal.equals("a")) {
                    StdDraw.clear(Color.BLACK);
                    drawOptions();
                    StdDraw.show();
                    break;
                }
                }

        }
    }

    public void drawAboutInfo() {
        StdDraw.clear(Color.BLACK);
        drawString(HOW_TO_PLAY_MSG, this.middleW, this.topH, 30);
        drawString(POINT_1, this.middleW, this.middleH1, 15);
        drawString(POINT_2, this.middleW, this.middleH2, 15);
        drawString(POINT_3, this.middleW, this.middleH3, 15);
        drawString(POINT_4, this.middleW, this.middleH4, 15);
        drawString("Press 'a' to return to main menu", this.middleW, this.middleH5, 15);
        StdDraw.show();
    }


}
