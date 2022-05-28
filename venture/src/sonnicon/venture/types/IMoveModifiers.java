package sonnicon.venture.types;

import io.anuke.mindustry.world.Tile;

public interface IMoveModifiers{

    default boolean shouldMove(Tile tile, int direction){
        return true;
    }

    default boolean bringWhenMoved(Tile tile, int direction){
        return false;
    }
}
