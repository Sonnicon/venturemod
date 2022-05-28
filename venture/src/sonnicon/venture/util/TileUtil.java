package sonnicon.venture.util;

import io.anuke.arc.func.Cons;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.world.blocks.transportation.BracketBlock;

import java.util.ArrayList;
import java.util.Collection;

public class TileUtil{
    private static ArrayList<Tile> tiles = new ArrayList<>();

    public static void getMultiblockTiles(Tile tile, Cons<Tile> cons) {
        // Ensure we are using the actual block
        tile = tile.link();

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
    }

    public static void getMultiblockTiles(Tile tile, Collection<Tile> result) {
        getMultiblockTiles(tile, result::add);
    }

    public static Tile getNearbyDistance(Tile tile, int direction, int distance){
        if(direction % 2 == 0){
            return Vars.world.tile(tile.x + (-direction+1) * distance, tile.y);
        }
        return Vars.world.tile(tile.x, tile.y + (-direction+2) * distance);
    }
}
