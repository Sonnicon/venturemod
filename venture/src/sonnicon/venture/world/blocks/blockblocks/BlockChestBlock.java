package sonnicon.venture.world.blocks.blockblocks;

import io.anuke.arc.Core;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.math.geom.Point2;
import io.anuke.arc.scene.Element;
import io.anuke.arc.scene.ui.Image;
import io.anuke.arc.scene.ui.layout.Stack;
import io.anuke.arc.scene.ui.layout.Table;
import io.anuke.mindustry.gen.Tex;
import io.anuke.mindustry.ui.Cicon;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Edges;
import io.anuke.mindustry.world.Tile;

import static io.anuke.mindustry.Vars.world;

public class BlockChestBlock extends BlockStorageBlock{
    protected TextureRegion bottom, pointer;

    protected Point2[] edges;

    public BlockChestBlock(String name){
        super(name);
        blockCapacity = 50;
        solid = false;
        update = true;
        stopOnDisabled = true;
        consumesTap = true;
        configurable = true;
    }

    @Override
    public void load(){
        super.load();
        edges = Edges.getEdges(size);

        bottom = Core.atlas.find(name + "-bottom");
        pointer = Core.atlas.find(name + "-pointer");
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        BlockStorageBlockEntity entity = tile.entity();
        if(entity.blocks.total() > 0){
            for(Point2 point : edges){
                Tile other = world.ltile(tile.x + point.x, tile.y + point.y);
                if(other != null && other.block() instanceof BlockStorageBlock && (!other.block().rotate || other.getNearbyLink(other.rotation()) != tile) && ((BlockStorageBlock) other.block()).acceptBlock(other.block(), other, tile)){
                    ((BlockStorageBlock) other.block()).handleBlock(entity.blocks.take(), other, tile);
                }
            }
        }
    }

    @Override
    public void buildTable(Tile tile, Table table){
        BlockStorageBlockEntity entity = tile.entity();
        table.background(Tex.inventory);
        final int[] c = {0};
        entity.blocks.forEach((block, amount) -> {
            table.add(blockCount(block, (int) amount)).pad(4f);
            if((++c[0]) % 4 == 0) table.row();
        });
    }

    protected Element blockCount(Block block, int amount){
        Stack s = new Stack();
        Table t = new Table().left().bottom();
        t.label(() -> round(amount));
        s.add(new Image(block.icon(Cicon.xlarge)));
        s.add(t);
        return s;
    }

    private String round(float f){
        f = (int)f;
        if(f >= 1000000){
            return (int)(f / 1000000f) + "[gray]mil[]";
        }else if(f >= 1000){
            return (int)(f / 1000) + "k";
        }else{
            return (int)f + "";
        }
    }
}
