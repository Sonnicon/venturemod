package sonnicon.venture.world.blocks.distribution;

import io.anuke.mindustry.world.Tile;
import sonnicon.venture.types.DuctIOType;

public class DuctPowerIO extends DuctIO{
    public DuctPowerIO(String name){
        super(name);
        hasPower = true;
        consumesPower = true;
        outputsPower = true;
        consumes.powerBuffered(100f);
        type = DuctIOType.power;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        tile.entity.power.graph.update();
    }
}
