package sonnicon.venture.util;

import io.anuke.arc.func.Cons;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.world.blocks.transportation.BracketBlock;

import java.util.ArrayList;

public class TileUtil{
    private static ArrayList<Tile> tiles = new ArrayList<>();

    public static void getLinkedTiles(Tile tile, Cons<Tile> cons, int direction){
        if(tile.block() instanceof BracketBlock){
            tiles.clear();
            for(Tile t : getLinkedTiles(tile, tiles, direction)){
                cons.get(t);
            }
        }else{
            if(tile.block().isMultiblock()){
                int size = tile.block().size;
                int offsetx = -(size - 1) / 2;
                int offsety = -(size - 1) / 2;

                for(int dx = 0; dx < size; dx++){
                    for(int dy = 0; dy < size; ++dy){
                        Tile other = Vars.world.tile(tile.x + dx + offsetx, tile.y + dy + offsety);
                        if(other != null && other.link() == tile){
                            cons.get(other);
                        }
                    }
                }
            }else{
                cons.get(tile);
            }
        }
    }

    public static ArrayList<Tile> getLinkedTiles(Tile tile, ArrayList<Tile> tmpArray, int direction) {
        if(tile.block() instanceof BracketBlock){
            return ((BracketBlock) tile.block()).getLinkedTiles(tile, tmpArray, direction);
        }else{
            tmpArray.clear();
            getLinkedTiles(tile, tmpArray::add, direction);
            return tmpArray;
        }
    }

    public static Tile getNearbyDistance(Tile tile, int direction, int distance){
        if(direction % 2 == 0){
            return Vars.world.tile(tile.x + (-direction+1) * distance, tile.y);
        }
        return Vars.world.tile(tile.x, tile.y + (-direction+2) * distance);
    }
}
