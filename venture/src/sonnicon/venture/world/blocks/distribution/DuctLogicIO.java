package sonnicon.venture.world.blocks.distribution;

import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.logic.LogicBlock;
import sonnicon.venture.types.DuctIOType;

public class DuctLogicIO extends DuctIO{
    public DuctLogicIO(String name){
        super(name);
        type = DuctIOType.logic;
        //why would you ever need this in any situation?
        controllable = false;
        entityType = DuctLogicIOEntity::new;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        DuctLogicIOEntity entity = tile.entity();
        int sig = 0;
        for(Tile ti : entity.proximity()){
            if(ti.block() instanceof LogicBlock){
                if(entity.input){
                    sig |= ((LogicBlock) ti.block()).getSignal(tile, ti);
                }else{
                    LogicBlock.LogicEntity en = ti.entity();
                    en.lastSignal = entity.signal | ((LogicBlock)ti.block()).signal(ti);
                    en.nextSignal = en.lastSignal;
                }
            }
        }
        if(entity.input && sig != entity.signal) changedSignal(tile, sig);
    }

    public void changedSignal(Tile tile, int newSignal){
        DuctLogicIOEntity entity = tile.entity();
        entity.signal = newSignal;
        entity.ductNetwork.reLogic(entity.setting);
    }

    @Override
    public void setChannel(Tile tile, int color){
        DuctLogicIOEntity entity = tile.entity();
        int ch = entity.setting;
        super.setChannel(tile, color);
        entity.ductNetwork.reLogic(ch);
        entity.ductNetwork.reLogic(entity.setting);
    }

    @Override
    public void setInput(Tile tile, boolean input){
        DuctIOEntity entity = tile.entity();
        super.setInput(tile, input);
        entity.ductNetwork.reLogic(entity.setting);
    }

    public class DuctLogicIOEntity extends DuctIOEntity{
        public int signal = 0;
    }
}
