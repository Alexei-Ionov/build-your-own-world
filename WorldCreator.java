package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.util.*;


public class WorldCreator {
    private int width;
    private int height;
    private TETile[][] world;
    private Random rand;
    private int numOfPotentialRooms;
    private ArrayList<Room> roomsLst;
    private int numOfActualRooms;
    private User user;
    private int numOfLightSwitches;
    private boolean containsMasterKey;
    private boolean visibleWorld;

    private int canvasHeight;

    private static final int LIGHT_BREADTH = 20;
    private ArrayList<int[]> switchPos;

    private TETile[][] prevStateOfWorld;

    private int[] masterDoorPos;

    private HUD innerHud;
    private Monster monster;

    private boolean toggleMonsterPath;

    private HashSet<TETile> validTile;

    private boolean pathDrawn;

    private HashMap<ArrayList<Integer>, Room> hashMap; // maps pos to what room it belongs too


    public class Room {
        private int width;
        private int height;
        private int positionX;
        private int positionY;

        public Room(int posX, int posY, int w, int h) {
            width = w;
            height = h;
            positionX = posX;
            positionY = posY;
        }

    }

    public class Monster {
        private int x;
        private int y;
        private ArrayDeque<ArrayList<Integer>> path;
        public Monster(int x, int y) {
            this.x = x;
            this.y = y;
            path = new ArrayDeque<>();
        }
    }

    public class User {
        private int userX;
        private int userY;
        private boolean containsKey;
        private boolean containsPotion;

        private int health;
        private TETile prevTile;

        public User(int x, int y) {
            userX = x;
            userY = y;
            containsKey = false;
            containsPotion = false;
            health = 5;
            prevTile = Tileset.FLOOR;

        }
    }



    public WorldCreator(Long seed, String moves, int width, int height, int canvasHeight) {

        this.width = width;
        this.height = height;
        this.canvasHeight = canvasHeight;
        monster = null;
        pathDrawn = false;
        hashMap = new HashMap<>();

        validTile = new HashSet<>();
        createValidTile();
        containsMasterKey = false;
        visibleWorld = false;
        toggleMonsterPath = false;
        this.rand = new Random(seed);
        world = new TETile[this.width][this.height];
        roomsLst = new ArrayList<>();
        switchPos = new ArrayList<>();
        fillWorld();
        numOfPotentialRooms = this.rand.nextInt(100, 200);
        generateRooms(); //creates the foundation of the world including rooms + hallways
        numOfActualRooms = roomsLst.size();
        numOfLightSwitches = numOfActualRooms;
        connectRooms();
        genUser();
        genSwitches();
        genTreasureChest();
        genKey();
        genMasterDoor();
        innerHud = new HUD(width, canvasHeight, world);
        //if created from input string or loading!
        if (moves != null) {
            Move(moves);
        }
    }

    private void createValidTile() {
        validTile.add(Tileset.FLOOR);
        validTile.add(Tileset.TOGGLE_FLOOR);
        validTile.add(Tileset.KEY);
        validTile.add(Tileset.AVATAR);
    }
    private void genMasterDoor() {

        while (true) {
            int index = this.rand.nextInt(roomsLst.size());
            Room room = roomsLst.get(index);
            int[] pos = new int[]{room.positionX + (room.width / 2), room.positionY + room.height - 1};
            int x = pos[0];
            int y = pos[1];
            if (world[x][y] == Tileset.WALL) {
                world[x][y] = Tileset.LOCKED_DOOR;
                masterDoorPos = new int[]{x, y};
                int[][] dirr = {{1, 0}, {-1, 0}, {1, -1}, {-1, -1}};
                for (int[] dirxn : dirr) {
                    int newX = dirxn[0] + x;
                    int newY = dirxn[1] + y;
                    if (newX >= 0 && newX < this.width && newY >= 0 && newY < this.height) {
                        if (world[newX][newY] == Tileset.WALL) {
                            world[newX][newY] = Tileset.WALLFLAG;
                        } else {
                            world[newX][newY] = Tileset.TORCH;
                        }
                    }
                }
                break;
            }
        }
    }

    private void genKey() {
        while (true) {
            if (genTiles(Tileset.KEY)) {
                break;
            }
        }

    }
    public int[] returnMonsterPos() {
        if (monster == null) {
            return null;
        }
        return new int[]{monster.x, monster.y};
    }

    public ArrayDeque<ArrayList<Integer>> returnMonsterPath() {
        if (monsterExists()) {
            return monster.path;
        }
        return new ArrayDeque<>();
    }

    public int[] returnUserPos() {
        return new int[]{user.userX, user.userY};
    }

    private void genTreasureChest() {
        while (true) {
            if (genTiles(Tileset.CLOSED_TREASURE_CHEST)) {
                break;
            }
        }
    }

    private boolean genTiles(TETile tileToAdd) {
        int index = this.rand.nextInt(roomsLst.size());
        Room room = roomsLst.get(index);
        int[] pos = roomCorners(room);
        TETile tile = world[pos[0]][pos[1]];
        if (tile == Tileset.FLOOR) {
            if (tileToAdd == Tileset.LIGHT_SWITCH) {
                switchPos.add(pos);
            }
            world[pos[0]][pos[1]] = tileToAdd;
            return true;
        }
        return false;
    }

    private void genSwitches() {
        int cnt = 0;
        while (cnt < numOfLightSwitches) {
            if (genTiles(Tileset.LIGHT_SWITCH)) {
                cnt += 1;
            }
        }
    }

    private void genUser() {
        int index = this.rand.nextInt(roomsLst.size());
        Room userStartRoom = roomsLst.get(index);
        int[] userStartPos = middlePt(userStartRoom);
        user = new User(userStartPos[0], userStartPos[1]);
        world[user.userX][user.userY] = Tileset.AVATAR;
    }
    //need to update move method so to be able to move over things taht are more than jsut floor tiels

    private boolean inSight(int mouseX, int mouseY) {
        return Math.abs(mouseX - user.userX) <= 2 && Math.abs(mouseY - user.userY) <= 2;
    }
    private void queueSpawnMonster() {
        int x = masterDoorPos[0];
        int y = masterDoorPos[1] - 1;
        monster = new Monster(x, y);
        world[monster.x][monster.y] = Tileset.MONSTER;
    }
    public void monsterChase() {
        monster.path.clear();
        int x = monster.x;
        int y = monster.y;
        if (x < user.userX && validTile.contains(world[x + 1][y])) {
            monsterChaseHelper(x + 1, y);
        } else if (x > user.userX && validTile.contains(world[x - 1][y])) {
            monsterChaseHelper(x - 1, y);
        } else if (y > user.userY && validTile.contains(world[x][y - 1])) {
            monsterChaseHelper(x, y - 1);
        } else if (y < user.userY && validTile.contains(world[x][y + 1])) {
            monsterChaseHelper(x, y + 1);
        } else {
            findPath();
        }

    }

    private void damageUser() {
        user.health -= 1;
        if (user.health <= 0) {
            innerHud.GAME_OVER();
        }
    }

    private void flingUser() {
        int[][] dirr = {{2, 0}, {0, 2}, {-2, 0}, {0, -2}};

        for (int[] dirxn : dirr) {
            int newX = user.userX + dirxn[0];
            int newY = user.userY + dirxn[1];
            if (newX >= 0 && newX < this.width && newY >= 0 && newY < this.height) {
                TETile tile = world[newX][newY];
                if (tile == Tileset.FLOOR || tile == Tileset.TOGGLE_FLOOR) {
                    user.userX = newX;
                    user.userY = newY;
                    world[user.userX][user.userY] = Tileset.AVATAR;
                    break;
                }
            }

        }
    }

    public int returnUserHealth() {
        return user.health;
    }

    private void monsterChaseHelper(int x, int y) {
        world[monster.x][monster.y] = Tileset.FLOOR;
        monster.x = x;
        monster.y = y;
        if (world[x][y] == Tileset.AVATAR) {
            damageUser();
            flingUser();
        }
        world[monster.x][monster.y] = Tileset.MONSTER;

    }
    public void moveMonster(ArrayList<Integer> move) {
        int x = move.get(0);
        int y = move.get(1);
        monsterChaseHelper(x, y);
    }
    private void backtrack(HashMap<ArrayList<Integer>, ArrayList<Integer>> hashmap) {

        ArrayList<Integer> monsterPos = new ArrayList<>(Arrays.asList(monster.x, monster.y));
        ArrayList<Integer> parent = new ArrayList<>(Arrays.asList(user.userX, user.userY));
        ArrayDeque<ArrayList<Integer>> path = new ArrayDeque<>();
        while (!parent.equals(monsterPos)) {
            parent = hashmap.get(parent);
            if (toggleMonsterPath) {
                world[parent.get(0)][parent.get(1)] = Tileset.TOGGLE_FLOOR;
            }
            path.addFirst(parent);

        }
        monster.path = path;
    }

    public void findPath() {
        deletePath();
        pathDrawn = false;
        HashMap<ArrayList<Integer>, ArrayList<Integer>> hashmap = new HashMap<>(); // maps node to parent
        ArrayList<ArrayList<Integer>> queue = new ArrayList<>();
        int[][] dirr = NSEW_Dirr();
        ArrayList<ArrayList<Integer>> temp;
        ArrayList<Integer> start = new ArrayList<>(Arrays.asList(monster.x, monster.y));
        queue.add(start);


        while (!queue.isEmpty()) {

            temp = new ArrayList<>();
            for (ArrayList<Integer> node : queue) {
                int x = node.get(0);
                int y = node.get(1);
                for (int[] dirxn : dirr) {
                    int newX = x + dirxn[0];
                    int newY = y + dirxn[1];


                    ArrayList<Integer> newPos = new ArrayList<>(Arrays.asList(newX, newY));
                    TETile tile = world[newX][newY];

                    //Don't need to check if its in bounds, since that is already garunteed for us
                    if ((tile == Tileset.FLOOR || tile == Tileset.TOGGLE_FLOOR) && !hashmap.containsKey(newPos)) {
                        hashmap.put(newPos, node);
                        temp.add(newPos);
                    }
                    if (world[newX][newY] == Tileset.AVATAR) {
                        hashmap.put(newPos, node);
                        backtrack(hashmap);
                        pathDrawn = true;
                        return;


                    }

                }
            }
            queue = temp;
        }
    }
    private boolean monsterExists() {
        return monster != null;
    }

    private void userOptions() {
        int[] pos = mouseAt();
        if (inSight(pos[0], pos[1])) {
            TETile tile = world[pos[0]][pos[1]];

            if (tile == Tileset.LIGHT_SWITCH) {
                lightSwitchOn(pos[0], pos[1]);
                world[pos[0]][pos[1]] = Tileset.FLOOR;

            } else if (tile == Tileset.CLOSED_TREASURE_CHEST) {
                //perform opening of treasure chest + obtained the "key"
                if (user.containsKey) {
                    //open chest + fade
                    world[pos[0]][pos[1]] = Tileset.OPEN_FULL_TREASURE_CHEST;
                } else {
                    innerHud.needChestKeyMsg();
                }
            } else if (tile == Tileset.OPEN_FULL_TREASURE_CHEST) {
                world[pos[0]][pos[1]] = Tileset.OPEN_EMPTY_TREASURE_CHEST;
                innerHud.drawItemsGained();
                user.containsPotion = true;
                innerHud.spookyMSG();
                world[pos[0]][pos[1]] = Tileset.FLOOR;
                for (int[] pt : switchPos) {
                    world[pt[0]][pt[1]] = Tileset.FLOOR;
                }
                queueSpawnMonster();
                toggleMonsterPath = true;
                findPath();
                world[masterDoorPos[0]][masterDoorPos[1]] = Tileset.UNLOCKED_DOOR;
            } else if (tile == Tileset.LOCKED_DOOR) {
                innerHud.playMasterDoorMsg();
            } else if (tile == Tileset.UNLOCKED_DOOR) {
                innerHud.drawFinalMsg();
                }
        }
        //perform action of opening it + CONGRATS YOU WIN


    }

    private int[][] NSEW_Dirr() {
        return new int[][]{{1,0}, {-1, 0}, {0, 1}, {0, -1}};
    }

    private void lightSwitchOn(int lightPosX, int lightPosY) {
        prevStateOfWorld = world.clone();

        HashSet<ArrayList<Integer>> visited = new HashSet<>();
        ArrayList<Integer> newPos = new ArrayList<>(Arrays.asList(lightPosX, lightPosY));
        visited.add(newPos);
        ArrayList<ArrayList<Integer>> queue = new ArrayList<>(List.of(newPos));
        ArrayList<ArrayList<Integer>> temp;

        int[][] dirr = NSEW_Dirr();

        int currDistance = 0;

        while (!queue.isEmpty() && currDistance < LIGHT_BREADTH) {
            temp = new ArrayList<>();
            for (ArrayList<Integer> node : queue) {
                int x = node.get(0);
                int y = node.get(1);
                for (int[] dirxn : dirr) {
                    int newX = x + dirxn[0];
                    int newY = y + dirxn[1];
                    newPos = new ArrayList<>(Arrays.asList(newX, newY));
                    if (newX >= 0 && newX < this.width && newY >= 0 && newY < this.height) {
                        TETile tile = world[newX][newY];
                        if (tile != Tileset.NOTHING && !visited.contains(newPos)) {
                            world[newX][newY].draw(newX, newY);
                            temp.add(newPos);
                            visited.add(newPos);
                        }
                    }

                }
            }
            StdDraw.show();
            StdDraw.pause(500);
            queue = temp;
            currDistance += 1;
        }
        world = prevStateOfWorld;
    }
    private int[] mouseAt () {
        Double mousePosX = StdDraw.mouseX();
        Double mousePosY = StdDraw.mouseY();
        int[] pos = findTile(mousePosX, mousePosY);
        return pos;
    }
    private int[] findTile (Double mousePosX, Double mousePosY){
        int x = (int) Math.floor(mousePosX);
        int y = (int) Math.floor(mousePosY);
        return new int[]{x, y};
    }
    private void moveHelper(int x, int y) {
        world[user.userX][user.userY] = Tileset.FLOOR;
        if (toggleMonsterPath && user.prevTile == Tileset.TOGGLE_FLOOR) {
            world[user.userX][user.userY] = Tileset.TOGGLE_FLOOR;
        }
        user.userX += x;
        user.userY += y;
        world[user.userX][user.userY] = Tileset.AVATAR;

    }
    public void Move (String moves) {
        for (int index = 0; index < moves.length(); index++) {
            String move = Character.toString(moves.charAt(index));
            if (move.equals("w") && user.userY < this.height - 1 && validTile.contains(world[user.userX][user.userY + 1])) {
                TETile nextTile = world[user.userX][user.userY + 1];
                if (nextTile == Tileset.KEY) {
                    user.containsKey = true;
                }
                moveHelper(0, 1);
                user.prevTile = nextTile;


            } else if (move.equals("s") && user.userY > 0 && validTile.contains(world[user.userX][user.userY - 1])) {
                TETile nextTile = world[user.userX][user.userY - 1];
                if (nextTile == Tileset.KEY) {
                    user.containsKey = true;
                }
                moveHelper(0, -1);
                user.prevTile = nextTile;

            } else if (move.equals("d") && user.userX < this.width - 1 && validTile.contains(world[user.userX + 1][user.userY])) {
                TETile nextTile = world[user.userX + 1][user.userY];
                if (nextTile == Tileset.KEY) {
                    user.containsKey = true;
                }
                moveHelper(1, 0);
                user.prevTile = nextTile;

            } else if (move.equals("a") && user.userX > 0 && validTile.contains(world[user.userX - 1][user.userY])) {
                TETile nextTile = world[user.userX - 1][user.userY];
                if (nextTile == Tileset.KEY) {
                    user.containsKey = true;
                }
                moveHelper(-1, 0);
                user.prevTile = nextTile;

            } else if (move.equals("o"))
                //user pressed "o"
                userOptions();
            if (moves.equals("p") && user.containsPotion) {
                visibleWorld = !visibleWorld;
                drinkPotion();
            }
            if (moves.equals("t") && !monster.path.isEmpty()) {
                toggleMonsterPath = ! toggleMonsterPath;
                togglePath();
            }

        }
    }

    private void deletePath() {
        for (ArrayList<Integer> node : monster.path) {
            if (world[node.get(0)][node.get(1)] == Tileset.AVATAR) {
                continue;
            }
            world[node.get(0)][node.get(1)] = Tileset.FLOOR;
        }
    }
    private void togglePath() {
        for (ArrayList<Integer> node : monster.path) {
            if (world[node.get(0)][node.get(1)] != Tileset.AVATAR) {
                if (toggleMonsterPath) {
                    world[node.get(0)][node.get(1)] = Tileset.TOGGLE_FLOOR;
                } else {
                    world[node.get(0)][node.get(1)] = Tileset.FLOOR;
                }
            }
        }
    }
    private void drinkPotion() {
        innerHud.drawVisibility();
    }
    public boolean returnVisibleWorld() {
        return true;
    }
    public TETile[][] returnWorld () {
        return world;
    }
    private void fillWorld () {
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }
    private int[] middlePt (Room room){
        return new int[]{room.positionX + Math.floorDiv(room.width, 2), room.positionY + Math.floorDiv(room.height, 2)};
    }
    private void generateRooms () {
        for (int i = 0; i < numOfPotentialRooms; i++) {
            int[] vals = genVals();
            Room room = new Room(vals[0], vals[1], vals[2], vals[3]);
            if (validateSpace(room)) {
                roomsLst.add(room);
                addRoom(room);
            }

        }
    }

    private ArrayList<Room> connectRoomsBFs(Room room, int connections, HashSet<Room> visitedRooms) {
        ArrayList<Integer> startingPos = new ArrayList<>(Arrays.asList(room.positionX, room.positionY));
        ArrayList<ArrayList<Integer>> localQueue = new ArrayList<>(Arrays.asList(startingPos));
        HashSet<ArrayList<Integer>> visitedTiles = new HashSet<>();
        visitedTiles.add(startingPos);
        ArrayList<ArrayList<Integer>> temp;
        ArrayList<Room> roomsConnectedTo = new ArrayList<>();
        int[][] dirr = NSEW_Dirr();

        HashSet<Room> localVisitedRooms = new HashSet<>();
        localVisitedRooms.add(room);

        while (!localQueue.isEmpty()) {
            temp = new ArrayList<>();
            for (ArrayList<Integer> node : localQueue) {
                for (int[] dirxn : dirr) {
                    int newX = node.get(0) + dirxn[0];
                    int newY = node.get(1) + dirxn[1];
                    if (newX >= 0 && newX < this.width && newY >= 0 && newY < this.height) {
                        ArrayList<Integer> currPos = new ArrayList<>(Arrays.asList(newX, newY));
                        if (!visitedTiles.contains(currPos)) {
                            if (hashMap.containsKey(currPos)) {
                                Room otherRoom = hashMap.get(currPos);
                                if (!otherRoom.equals(room) && !localVisitedRooms.contains(otherRoom) && !visitedRooms.contains(otherRoom)) {
                                    roomsConnectedTo.add(otherRoom);
                                    connections -= 1;
                                    localVisitedRooms.add(otherRoom);
                                }
                            }
                            temp.add(currPos);
                            visitedTiles.add(currPos);
                        }
                        if (connections == 0) {
                            return roomsConnectedTo;
                        }
                    }
                }
            }
            localQueue = temp;
        }
        return roomsConnectedTo;
    }

    private void connectRooms() {
        int randInt = this.rand.nextInt(roomsLst.size());

        Room randomStartingRoom = roomsLst.get(randInt);
        ArrayList<Room> globalQueue = new ArrayList<>(Collections.singletonList(randomStartingRoom));
        ArrayList<Room> temp;
        HashSet<Room> visitedRooms = new HashSet<>();
        visitedRooms.add(randomStartingRoom);


        while (!globalQueue.isEmpty()) {
            Room currRoom = globalQueue.remove(0);
            temp = connectRoomsBFs(currRoom, 1, visitedRooms);
            for (Room newRoom : temp) {
                createHallway(currRoom, newRoom);
            }
            visitedRooms.add(currRoom);
            if (visitedRooms.size() == roomsLst.size() - 1) { // for connections == 1, if we are on the last node then we should break early to not search whole map for no reason.
                break;
            }
            globalQueue = temp;
        }
    }
    private void createHallway (Room prevRoom, Room currRoom){
        int[] prevRoomPos = middlePt(prevRoom);
        int[] currRoomPos = middlePt(currRoom);
        int randInt = this.rand.nextInt(2);
        int prevX = prevRoomPos[0];
        int prevY = prevRoomPos[1];
        int lengthX = currRoomPos[0] - prevX;
        int lengthY = currRoomPos[1] - prevY;
        int dirxnX = 1;
        int dirxnY = 1;
        if (lengthX < 0) {
            dirxnX *= -1;
        }
        if (lengthY < 0) {
            dirxnY *= -1;
        }
        switch (randInt) {
            case 0:
                drawHorizontal(prevX, prevY, lengthX, dirxnX);
                addCorner(currRoomPos[0], prevY);
                drawVertical(prevX + lengthX, prevY + dirxnY, lengthY, dirxnY);
            case 1:
                drawVertical(prevX, prevY, lengthY, dirxnY);
                addCorner(prevX, currRoomPos[1]);
                drawHorizontal(prevX + dirxnX, prevY + lengthY, lengthX, dirxnX);
        }
    }
    private void drawVertical ( int x, int y, int length, int dirxn){
        // if we are trying to add hallway upwards, dirxnUp will equal 1
        //if going downwards, dirxnUp will be -1!
        length = Math.abs(length);
        for (int i = 0; i < length; i++) {
            if (world[x][y + (i * dirxn)] == Tileset.FLOOR) {
                continue;
            }
            world[x][y + (i * dirxn)] = Tileset.FLOOR;
            world[x + 1][y + (i * dirxn)] = Tileset.WALL;
            world[x - 1][y + (i * dirxn)] = Tileset.WALL;
        }
    }
    private void drawHorizontal ( int x, int y, int length, int dirxn){
        // dirxn = 1 --> going right
        // dirxn = -1 --> going left
        length = Math.abs(length);
        for (int i = 0; i < length; i++) {
            if (world[x + (i * dirxn)][y] == Tileset.FLOOR) {
                continue;
            }
            world[x + (i * dirxn)][y] = Tileset.FLOOR;
            world[x + (i * dirxn)][y + 1] = Tileset.WALL;
            world[x + (i * dirxn)][y - 1] = Tileset.WALL;
        }
    }
    private void addCorner ( int x, int y){
        world[x][y] = Tileset.FLOOR;
        int[][] dirr = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
        for (int[] dirxn : dirr) {
            int newX = x + dirxn[0];
            int newY = y + dirxn[1];
            if (newX < this.width && newX >= 0 && newY < this.height && newY >= 0 && world[newX][newY] == Tileset.NOTHING) {
                world[newX][newY] = Tileset.WALL;
            }
        }
    }
    private boolean validateSpace (Room room){
        if (room.positionX < 0 || room.positionX >= this.width || room.positionY < 0 || room.positionY >= this.height) {
            return false;
        }

        for (int y = room.positionY; y < room.positionY + room.height; y++) {
            if (y >= this.height || (world[room.positionX][y] != Tileset.NOTHING)) {
                return false;
            }
        }
        int topY = room.positionY + room.height - 1;
        for (int x = room.positionX; x < room.positionX + room.width; x++) {
            if (x >= this.width || (world[x][topY] != Tileset.NOTHING)) {
                return false;
            }
        }

        int rightX = room.positionX + room.width - 1;

        for (int y = topY; y >= room.positionY; y--) {
            if (world[rightX][y] != Tileset.NOTHING) {
                return false;
            }
        }

        for (int x = rightX; x >= room.positionX; x--) {
            if (world[x][room.positionY] != Tileset.NOTHING) {
                return false;
            }
        }
        return true;
    }
    private void addRoom (Room room){
        int x = room.positionX;
        int y = room.positionY;

        for (int i = x; i < x + room.width; i++) {
            for (int j = y; j < y + room.height; j++) {
                if (i == x) {
                    world[i][j] = Tileset.WALL;
                } else if (i == x + room.width - 1) {
                    world[i][j] = Tileset.WALL;
                } else if (j == y) {
                    world[i][j] = Tileset.WALL;
                } else if (j == y + room.height - 1) {
                    world[i][j] = Tileset.WALL;
                } else {
                    world[i][j] = Tileset.FLOOR;
                }
                ArrayList<Integer> pos = new ArrayList<>(Arrays.asList(i, j));
                hashMap.put(pos, room);
            }
        }

    }

    private int[] genVals () {
        int posX = this.rand.nextInt(this.width);
        int posY = this.rand.nextInt(this.height);
        int width = this.rand.nextInt(4, 15);
        int height = this.rand.nextInt(4, 15);
        int[] values = new int[]{posX, posY, width, height};
        return values;

    }
    private int[] roomCorners (Room room){
        int rand = this.rand.nextInt(5);
        return switch (rand) {
            case 0 -> new int[]{room.positionX + 1, room.height - 1}; // top left
            case 1 -> new int[]{room.width - 1, room.height - 1}; // top right
            case 2 -> new int[]{room.positionX + 1, room.positionY + 1}; // bot left
            case 3 -> new int[]{room.width - 1, room.positionY + 1};
            case 4 -> middlePt(room);
            default -> null;
        };
    }

}



