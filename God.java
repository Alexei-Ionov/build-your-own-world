package byow.Core;

public class God {

    private WorldCreator worldGenerator;

    private int resizedWidth;
    private int resizedHeight;
    public God() {
        worldGenerator = null;
    }

    public void createWorld(Long seed, String moves, int width, int height) {
        resizedWidth = width;
        //height resized for the actual world, since we are adding the HUD bar
        resizedHeight = (height - height / 15);
        worldGenerator = new WorldCreator(seed, moves, resizedWidth, resizedHeight, height);
    }

    public WorldCreator returnWorldCreator() {
        return worldGenerator;
    }

}


