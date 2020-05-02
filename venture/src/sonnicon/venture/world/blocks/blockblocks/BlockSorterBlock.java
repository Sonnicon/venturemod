package sonnicon.venture.world.blocks.blockblocks;

import io.anuke.arc.scene.style.TextureRegionDrawable;
import io.anuke.arc.scene.ui.layout.Table;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.content.Blocks;
import io.anuke.mindustry.entities.type.Player;
import io.anuke.mindustry.gen.Tex;
import io.anuke.mindustry.type.ContentType;
import io.anuke.mindustry.ui.Cicon;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class BlockSorterBlock extends BlockRelayBlock{
    public BlockSorterBlock(String name){
        super(name);
        configurable = true;
        consumesTap = true;
        entityType = BlockSorterBlockEntity::new;
    }

    @Override
    public void update(Tile tile){
        BlockSorterBlockEntity entity = tile.entity();
        if(entity.enabled() && entity.blocks.total() > 0){
            if(entity.setting == entity.blocks.taste()){
                offloadTo(tile, tile.front());
            }else if(!offloadTo(tile, tile.left())){
                 offloadTo(tile, tile.right());
            }
        }
        if(entity.next != null){
            entity.blocks.add(entity.next, 1);
            entity.next = null;
        }
    }

    protected boolean offloadTo(Tile tile, Tile destination){
        BlockSorterBlockEntity entity = tile.entity();
        if(destination != null && destination.block() instanceof BlockStorageBlock && ((BlockStorageBlock) destination.block()).acceptBlock(entity.blocks.taste(), destination, tile)){
            ((BlockStorageBlock) destination.block()).handleBlock(entity.blocks.take(), destination, tile);
            return true;
        }
        return false;
    }

    @Override
    public void buildTable(Tile tile, Table table){
        super.buildTable(tile, table);
        BlockSorterBlockEntity entity = tile.entity();
        table.background(Tex.pane);
        table.addImageButton(new TextureRegionDrawable(entity.setting.icon(Cicon.xlarge)), () -> {
            if(Vars.control.input.block == null) return;
            tile.configure(Vars.control.input.block.id);
            Vars.control.input.frag.config.hideConfig();
        });
    }

    @Override
    public void configured(Tile tile, Player player, int value){
        if(!(tile.block() instanceof BlockSorterBlock)) return;
        BlockSorterBlockEntity entity = tile.entity();
        entity.setting = Vars.content.getByID(ContentType.block, value);
        if(entity.setting == null) entity.setting = Blocks.air;
    }

    class BlockSorterBlockEntity extends BlockRelayBlockEntity{
        public Block setting = Blocks.air;

        @Override
        public int config(){
            return setting.id;
        }

        @Override
        public void write(DataOutput stream) throws IOException{
            super.write(stream);
            stream.writeShort(config());
        }

        @Override
        public void read(DataInput stream, byte revision) throws IOException{
            super.read(stream, revision);
            tile.configure(stream.readShort());
        }
    }
}
