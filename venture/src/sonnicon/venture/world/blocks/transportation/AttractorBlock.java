package sonnicon.venture.world.blocks.transportation;

import io.anuke.arc.util.Structs;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.util.TileUtil;

public class AttractorBlock extends RepulsorBlock{
    public AttractorBlock(String name){
        super(name);
    }

    @Override
    public void update(Tile tile){
        LogicEntity entity = tile.entity();
        entity.lastSignal = entity.nextSignal;
        entity.nextSignal = this.signal(tile);

        Tile targetTile = TileUtil.getNearbyDistance(tile, tile.rotation(), 2);
        int reverserotation = (tile.rotation() + 2) % 4;

        if(entity.nextSignal > 0 && entity.lastSignal == 0 &&
                tile.front() != null && targetTile != null){
            origin = tile;
            if(searchMoveTiles(targetTile, reverserotation)){
                moving = true;
                tilesSearch.stream().sorted(
                                // Order tiles to move front-to-back based on direction
                                Structs.comparing(t -> (reverserotation % 2 == 0 ? t.x : t.y) * (reverserotation > 1 ? 1 : -1)))
                        .forEachOrdered(t -> move(t, reverserotation));
                moving = false;
            }
        }
    }
}
