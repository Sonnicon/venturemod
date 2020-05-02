package sonnicon.venture.world.blocks.transportation;

import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.types.SometimesMove;

public class AnchorBlock extends Block implements SometimesMove{
    public AnchorBlock(String name){
        super(name);
        health = 350;
        controllable = false;
        solid = true;
        destructible = true;
    }

    @Override
    public boolean shouldMove(Tile tile, int direction){
        return false;
    }
}
