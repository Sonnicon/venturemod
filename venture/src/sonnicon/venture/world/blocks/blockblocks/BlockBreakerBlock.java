package sonnicon.venture.world.blocks.blockblocks;

import io.anuke.arc.Core;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.math.geom.Point2;
import io.anuke.mindustry.graphics.Pal;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Edges;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.logic.LogicBlock;

import static io.anuke.mindustry.Vars.world;
import static sonnicon.venture.Venture.MOD_NAME;

public class BlockBreakerBlock extends BlockStorageBlock{
    public Point2[] edges;

    public BlockBreakerBlock(String name){
        super(name);
        blockCapacity = 1;
        update = true;
        rotate = true;
        controllable = false;
        entityType = BlockBreakerBlockEntity::new;
    }

    @Override
    public void load(){
        super.load();
        Core.atlas.getRegionMap().put("block-" + name + "-full", Core.atlas.getRegionMap().remove(name + "-full"));
    }

    @Override
    public void update(Tile tile){
        if(edges == null) edges = Edges.getEdges(size);
        BlockBreakerBlockEntity entity = tile.entity();
        entity.signalled = tile.back() != null && tile.back().block() instanceof LogicBlock && ((LogicBlock) tile.back().block()).getSignal(tile, tile.back()) > 0;
        if(entity.blocks.total() > 0){
            for(Point2 p : edges){
                Tile other = world.ltile(tile.x + p.x, tile.y + p.y);
                if(other.block() instanceof BlockStorageBlock && ((BlockStorageBlock) other.block()).acceptBlock(entity.blocks.taste(), other, tile)){
                    ((BlockStorageBlock) other.block()).handleBlock(entity.blocks.take(), other, tile);
                    break;
                }
            }
        }else if(entity.signalled){
            Tile front = tile.front();
            if(front != null && front.block().hasEntity() && front.block().destructible){
                entity.blocks.add(front.block(), 1);
                if(front.block().hasEntity()){
                    front.entity().setDead(true);
                    front.entity().health = Integer.MIN_VALUE;
                    front.entity().remove();
                }
                world.removeBlock(front);
            }
        }
    }

    @Override
    public boolean acceptBlock(Block block, Tile tile, Tile source){
        return false;
    }

    @Override
    public void draw(Tile tile){
        BlockBreakerBlockEntity entity = tile.entity();
        Draw.rect(MOD_NAME + "interactor-base", tile.drawx(), tile.drawy(),tile.rotation() * 90);
        Draw.color(entity.signalled ? Pal.accent : Color.white);
        Draw.rect(region, tile.drawx(), tile.drawy(), this.rotate ? (float)(tile.rotation() * 90) : 0.0F);
        Draw.color();
    }

    public class BlockBreakerBlockEntity extends BlockStorageBlockEntity{
        public boolean signalled = false;
    }
}
