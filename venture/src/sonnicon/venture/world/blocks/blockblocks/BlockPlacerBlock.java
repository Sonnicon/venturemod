package sonnicon.venture.world.blocks.blockblocks;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Build;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.logic.LogicBlock;
import sonnicon.venture.util.TileUtil;

public class BlockPlacerBlock extends BlockBreakerBlock{

    public BlockPlacerBlock(String name){
        super(name);
    }

    @Override
    public void update(Tile tile){
        BlockBreakerBlockEntity entity = tile.entity();
        entity.signalled = tile.back() != null && tile.back().block() instanceof LogicBlock && ((LogicBlock) tile.back().block()).getSignal(tile, tile.back()) > 0;
        if(entity.signalled && entity.blocks.total() > 0){
            Tile target = TileUtil.getNearbyDistance(tile, tile.rotation(), (int) Math.ceil(entity.blocks.taste().size / 2f));
            if(target != null && Build.validPlace(tile.getTeam(), target.x, target.y, entity.blocks.taste(), 0)){
                Vars.world.setBlock(target, entity.blocks.take(), tile.getTeam());
            }
        }
    }

    @Override
    public boolean acceptBlock(Block block, Tile tile, Tile source){
        return getMaximumAccepted(tile, block) == -1 || ((BlockStorageBlockEntity)tile.entity()).blocks.total() < getMaximumAccepted(tile, block);
    }
}
