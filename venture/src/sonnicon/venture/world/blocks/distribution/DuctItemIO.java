package sonnicon.venture.world.blocks.distribution;

import io.anuke.mindustry.type.Item;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.types.DuctIOType;

public class DuctItemIO extends DuctIO{
    public DuctItemIO(String name){
        super(name);
        hasItems = true;
        itemCapacity = 5;
        type = DuctIOType.item;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        if(tile.entity().enabled() && tile.entity().timer.get(timerDump, dumpTime) && tile.entity().items.total() > 0){
            tryDump(tile);
        }
    }

    @Override
    public boolean acceptItem(Item item, Tile tile, Tile source){
        DuctIOEntity entity = tile.entity();
        if(entity.ductNetwork == null) return false;
        return entity.enabled() && entity.input && tile.getTeam() == source.getTeam() && entity.ductNetwork.canAcceptItems(tile);
    }

    @Override
    public void handleItem(Item item, Tile tile, Tile source){
        DuctIO.DuctIOEntity entity = tile.entity();
        if(!acceptItem(item, tile, source)) return;
        entity.ductNetwork.handleItem(item, tile);
    }
}
