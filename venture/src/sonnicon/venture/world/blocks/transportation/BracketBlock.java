package sonnicon.venture.world.blocks.transportation;

import io.anuke.arc.collection.Array;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;

public class BracketBlock extends Block{
    public BracketBlock(String name){
        super(name);
        solid = true;
        destructible = true;
        controllable = false;
        hasShadow = false;
    }

    public Array<Tile> getLinkedTiles(Tile tile, Array<Tile> tmpArray){
        tmpArray.clear();
        addNearby(tile, tmpArray);
        return tmpArray;
    }

    protected void addNearby(Tile tile, Array<Tile> tmpArray){
        if(tmpArray.contains(tile)) return;
        tmpArray.add(tile);
        for(Tile t : tile.entity().proximity()){
            if(t.block() instanceof BracketBlock){
                ((BracketBlock) t.block()).addNearby(t, tmpArray);
            }
        }
    }
}
