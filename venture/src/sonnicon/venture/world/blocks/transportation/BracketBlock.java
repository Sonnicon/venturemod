package sonnicon.venture.world.blocks.transportation;

import io.anuke.arc.collection.Array;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;

public class BracketBlock extends Block{
    public BracketBlock(String name){
        super(name);
        health = 50;
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

    @Override
    public boolean canReplace(Block other) {
        return other instanceof BracketBlock && other != this;
    }

    protected void addNearby(Tile tile, Array<Tile> tmpArray){
        if(tmpArray.contains(tile)) return;
        tmpArray.add(tile);
        for(Tile t : tile.entity().proximity()){
            if((!rotate && t.block() instanceof BracketBlock) || (rotate && t == tile.front() && addNearbyFront(tile, t))){
                addAsNearby(t, tmpArray);
            }
        }
    }

    protected boolean addNearbyFront(Tile tile, Tile front){
        return true;
    }

    protected void addAsNearby(Tile tile, Array<Tile> tmpArray){
        if(tile.block() instanceof BracketBlock)
            ((BracketBlock)tile.block()).addNearby(tile, tmpArray);
        else if(!tmpArray.contains(tile))
            tmpArray.add(tile);
    }
}
