package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public class MiniBoss {
    private int width;
    private int height;
    private TETile[][] miniWorld;
    public class MiniRoom {

        private int posX;
        private int posY;

        private int roomWidth;
        private int roomHeight;
        public MiniRoom() {
            roomHeight = 30;
            roomWidth = 30;
            posX = (width / 2) - (roomWidth / 2);
            posY = (height / 2) - (roomHeight / 2);

        }
    }



    public MiniBoss(int width, int height) {
        this.width = width;
        this.height = height;
        miniWorld = new TETile[this.width][this.height];
        fillMiniWorld();
        MiniRoom room = new MiniRoom();
        genMainRoom(room);
        renderMiniWorld();
    }
    private void fillMiniWorld() {
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                miniWorld[x][y] = Tileset.NOTHING;
            }
        }
    }

    private void genMainRoom(MiniRoom room) {
        int x = room.posX;
        int y = room.posY;

        for (int i = x; i < x + room.roomWidth; i++) {
            for (int j = y; j < y + room.roomHeight; j++) {
                if (i == x) {
                    miniWorld[i][j] = Tileset.WALL;
                } else if (i == x + room.roomWidth - 1) {
                    miniWorld[i][j] = Tileset.WALL;
                } else if (j == y) {
                    miniWorld[i][j] = Tileset.WALL;
                } else if (j == y + room.roomHeight - 1) {
                    miniWorld[i][j] = Tileset.WALL;
                } else {
                    miniWorld[i][j] = Tileset.FLOOR;
                }
            }
        }


    }
    public TETile[][] returnMiniWorld() {
        return miniWorld;
    }

    public void renderMiniWorld() {
        int width = miniWorld.length;
        int height = miniWorld[0].length;
        StdDraw.clear(new Color(0, 0, 0));
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                if (miniWorld[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                miniWorld[x][y].draw(x, y);
            }
        }
    }

}
