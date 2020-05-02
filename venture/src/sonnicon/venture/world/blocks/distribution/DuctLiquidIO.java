package sonnicon.venture.world.blocks.distribution;

import io.anuke.mindustry.type.Liquid;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.types.DuctIOType;

public class DuctLiquidIO extends DuctIO{
    protected final int timerFlow = timers++;

    public DuctLiquidIO(String name){
        super(name);
        hasLiquids = true;
        outputsLiquid = true;
        type = DuctIOType.liquid;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        if(tile.entity().enabled() && tile.entity.liquids.total() > 0.001f && tile.entity.timer.get(timerFlow, 1)){
            tile.entity().liquids.each((liquid, v) -> tryDumpLiquid(tile, liquid));
        }
    }

    @Override
    public boolean acceptLiquid(Tile tile, Tile source, Liquid liquid, float amount){
        DuctIO.DuctIOEntity entity = tile.entity();
        if(entity.ductNetwork == null) return false;
        return entity.enabled() && entity.input && tile.getTeam() == source.getTeam() && entity.ductNetwork.canAcceptLiquids(tile, liquid, amount);
    }


    @Override
    public void handleLiquid(Tile tile, Tile source, Liquid liquid, float amount){
        DuctIO.DuctIOEntity entity = tile.entity();
        if(!acceptLiquid(tile, source, liquid, amount)) return;
        entity.ductNetwork.handleLiquid(tile, liquid, amount);
    }
}
