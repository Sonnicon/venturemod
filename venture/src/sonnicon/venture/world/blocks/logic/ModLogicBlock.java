package sonnicon.venture.world.blocks.logic;

import io.anuke.arc.Core;
import io.anuke.mindustry.world.blocks.logic.LogicBlock;

public abstract class ModLogicBlock extends LogicBlock{
    public ModLogicBlock(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        Core.atlas.getRegionMap().put("block-" + name + "-full", Core.atlas.getRegionMap().remove(name + "-full"));
    }
}

