package sonnicon.venture.types;

import io.anuke.mindustry.world.Tile;

public interface IMoved{

    void beforeMoved(Tile tile, int direction);

    void afterMoved(Tile tile, int direction);
}
