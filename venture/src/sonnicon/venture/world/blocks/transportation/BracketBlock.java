package sonnicon.venture.world.blocks.transportation;

import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.types.IMoveModifiers;

public class BracketBlock extends Block implements IMoveModifiers{
    public BracketBlock(String name){
        super(name);
        health = 50;
        solid = true;
        destructible = true;
        controllable = false;
        hasShadow = false;
    }

    @Override
    public boolean canReplace(Block other) {
        return other instanceof BracketBlock && other != this;
    }

    @Override
    public boolean bringWhenMoved(Tile tile, int direction){
        return tile.getNearby(direction).block() instanceof BracketBlock;
    }
}
