package sonnicon.venture.types;

import io.anuke.mindustry.world.Tile;

public interface OnMove{
    void beforeMoved(Tile tile, int direction);

    void afterMoved(Tile tile, int direction);
}
