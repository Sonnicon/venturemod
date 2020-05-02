package sonnicon.venture.types;

import io.anuke.mindustry.world.Tile;

public interface SometimesMove{
    default boolean shouldMove(Tile tile, int direction){return true;}
}
