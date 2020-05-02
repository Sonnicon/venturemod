package sonnicon.venture.world.blocks.logic;

import io.anuke.mindustry.entities.type.Unit;
import io.anuke.mindustry.world.Tile;

public class PressurepadBlock extends ModLogicBlock{

    public PressurepadBlock(String name){
        super(name);
        entityType = PressurepadBlockEntity::new;
    }

    @Override
    public int signal(Tile tile){
        PressurepadBlockEntity entity = tile.entity();
        for(Tile other : entity.proximity()){
            if(tile.front() != other && other.block() instanceof PressurepadBlock && ((PressurepadBlock) other.block()).getSignal(tile, other) == 1) return 1;
        }
        int sig = entity.stoodOn ? 1 : 0;
        entity.stoodOn = false;
        return sig;
    }

    @Override
    public void unitOn(Tile tile, Unit unit){
        PressurepadBlockEntity entity = tile.entity();
        entity.stoodOn = true;
    }

    public class PressurepadBlockEntity extends LogicEntity{
        public boolean stoodOn = false;
    }
}
