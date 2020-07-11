package sonnicon.venture.world.blocks.transportation;

import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;

import java.util.ArrayList;

public class BracketBlock extends Block{
    public BracketBlock(String name){
        super(name);
        health = 50;
        solid = true;
        destructible = true;
        controllable = false;
        hasShadow = false;
    }

    public ArrayList<Tile> getLinkedTiles(Tile tile, ArrayList<Tile> tmpArray, int direction){
        tmpArray.clear();
        addNearby(tile, tmpArray, direction);
        return tmpArray;
    }

    @Override
    public boolean canReplace(Block other) {
        return other instanceof BracketBlock && other != this;
    }

    protected void addNearby(Tile tile, ArrayList<Tile> tmpArray, int direction){
        if(tmpArray.contains(tile)) return;
        tmpArray.add(tile);
        for(Tile t : tile.entity().proximity()){
            if((rotate && t == tile.front() && t != tile.getNearbyLink(direction)) ? addNearbyFront(tile, t) : t.block() instanceof BracketBlock) addAsNearby(t, tmpArray, direction);

        }
    }

    protected boolean addNearbyFront(Tile tile, Tile front){
        return true;
    }

    protected void addAsNearby(Tile tile, ArrayList<Tile> tmpArray, int direction){
        if(tile.block() instanceof BracketBlock) ((BracketBlock)tile.block()).addNearby(tile, tmpArray, direction);
        else if(!tmpArray.contains(tile)) tmpArray.add(tile);
    }
}
