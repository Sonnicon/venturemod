package sonnicon.venture.world.blocks.blockblocks;

import io.anuke.arc.Core;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.type.ContentType;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.meta.BlockGroup;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static sonnicon.venture.Venture.MOD_NAME;

public class BlockRelayBlock extends BlockStorageBlock{
    protected Color dark = Color.valueOf("6aa95e"), light = Color.valueOf("80cb71");

    public BlockRelayBlock(String name){
        super(name);
        blockCapacity = 1;
        solid = false;
        update = true;
        rotate = true;
        group = BlockGroup.transportation;
        entityType = BlockRelayBlockEntity::new;
    }

    @Override
    public void load(){
        super.load();
        Core.atlas.getRegionMap().put("block-" + name + "-full", Core.atlas.getRegionMap().remove(name + "-full"));
    }

    @Override
    public void update(Tile tile){
        BlockRelayBlockEntity entity = tile.entity();
        if(entity.enabled()){
            Tile front = tile.front();
            if(front != null && front.block() instanceof BlockStorageBlock && entity.blocks.total() > 0 && ((BlockStorageBlock) front.block()).acceptBlock(entity.blocks.taste(), front, tile)){
                ((BlockStorageBlock) front.block()).handleBlock(entity.blocks.take(), front, tile);
            }
        }
        if(entity.next != null){
            entity.blocks.add(entity.next, 1);
            entity.next = null;
        }
    }

    @Override
    public void handleBlock(Block block, Tile tile, Tile source){
        BlockRelayBlockEntity entity = tile.entity();
        entity.next = block;
    }

    @Override
    public boolean acceptBlock(Block block, Tile tile, Tile source){
        return super.acceptBlock(block, tile, source) && ((BlockRelayBlockEntity)tile.entity()).next == null;
    }

    @Override
    public void draw(Tile tile){
        BlockRelayBlockEntity entity = tile.entity();
        Draw.rect(MOD_NAME + "blockblock-base", tile.drawx(), tile.drawy());
        Draw.color(entity.blocks.total() > 0 ? light : dark);
        Draw.rect(region, tile.drawx(), tile.drawy(), this.rotate ? (float)(tile.rotation() * 90) : 0.0F);
        Draw.color();
    }

    class BlockRelayBlockEntity extends BlockStorageBlockEntity{
        public Block next;

        @Override
        public void write(DataOutput stream) throws IOException{
            super.write(stream);
            stream.writeShort(next != null ? next.id : -1);
        }

        @Override
        public void read(DataInput stream, byte revision) throws IOException{
            super.read(stream, revision);
            short result = stream.readShort();
            if(result == -1){
                next = null;
            }else{
                next = Vars.content.getByID(ContentType.block, result);
            }
        }
    }
}
