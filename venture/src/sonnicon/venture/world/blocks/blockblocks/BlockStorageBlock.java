package sonnicon.venture.world.blocks.blockblocks;

import io.anuke.arc.Core;
import io.anuke.mindustry.entities.type.TileEntity;
import io.anuke.mindustry.entities.type.Unit;
import io.anuke.mindustry.graphics.Pal;
import io.anuke.mindustry.ui.Bar;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.world.modules.BlockModule;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static sonnicon.venture.Venture.MOD_NAME;

public abstract class BlockStorageBlock extends Block{
    public int blockCapacity = -1;

    public BlockStorageBlock(String name){
        super(name);
        entityType = BlockStorageBlockEntity::new;
    }

    public int acceptBlockStack(Block block, int amount, Tile tile, Unit source){
        if(acceptBlock(block, tile, tile) && (source == null || source.getTeam() == tile.getTeam())){
            return Math.min(getMaximumAccepted(tile, block) - ((BlockStorageBlockEntity)tile.entity).blocks.get(block), amount);
        }else{
            return 0;
        }
    }

    public boolean acceptBlock(Block block, Tile tile, Tile source){
        return tile.getNearbyLink(tile.rotation()) != source && (getMaximumAccepted(tile, block) == -1 || ((BlockStorageBlockEntity)tile.entity()).blocks.total() < getMaximumAccepted(tile, block));
    }

    public int getMaximumAccepted(Tile tile, Block block){
        return blockCapacity;
    }

    /** Remove a stack from this inventory, and return the amount removed. */
    public int removeBlockStack(Tile tile, Block block, int amount){
        BlockStorageBlockEntity entity = tile.entity();
        if(entity == null || entity.blocks == null) return 0;
        amount = Math.min(amount, entity.blocks.get(block));
        entity.noSleep();
        entity.blocks.remove(block, amount);
        return amount;
    }

    /** Handle a stack input. */
    public void handleBlockStack(Block block, int amount, Tile tile, Unit source){
        tile.entity.noSleep();
        ((BlockStorageBlockEntity)tile.entity()).blocks.add(block, amount);
    }

    public void handleBlock(Block block, Tile tile, Tile source){
        ((BlockStorageBlockEntity)tile.entity()).blocks.add(block, 1);
    }

    public void setBars(){
        bars.add("blocks", entity -> new Bar(() -> Core.bundle.format("bar." + MOD_NAME + "blocks", ((BlockStorageBlockEntity)entity).blocks.total()), () -> Pal.items, () -> (float)((BlockStorageBlockEntity)entity).blocks.total() / blockCapacity));
    }

    public class BlockStorageBlockEntity extends TileEntity{
        public BlockModule blocks;

        @Override
        public TileEntity init(Tile tile, boolean shouldAdd){
            blocks = new BlockModule();
            return super.init(tile, shouldAdd);
        }

        @Override
        public void write(DataOutput stream) throws IOException{
            super.write(stream);
            blocks.write(stream);
        }

        @Override
        public void read(DataInput stream, byte revision) throws IOException{
            super.read(stream, revision);
            blocks.read(stream);
        }
    }
}
