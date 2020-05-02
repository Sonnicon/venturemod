package sonnicon.venture.world.modules;

import io.anuke.arc.collection.Array;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.type.ContentType;
import io.anuke.mindustry.type.Item;
import io.anuke.mindustry.type.ItemStack;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.modules.ItemModule;
import sonnicon.venture.types.BlockStack;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static io.anuke.mindustry.Vars.content;

public class BlockModule extends io.anuke.mindustry.world.modules.BlockModule{
    private Array<BlockStack> blockstacks = new Array<>();
    private int total;

    public void forEach(BlockConsumer cons){
        for(BlockStack stack : blockstacks){
            cons.accept(stack.block, stack.amount);
        }
    }

    public float sum(BlockCalculator calc){
        float sum = 0f;
        for(BlockStack stack : blockstacks){
            sum += calc.get(stack.block, stack.amount);
        }
        return sum;
    }

    public boolean has(Block block){
        for(BlockStack stack : blockstacks){
            if(stack.block == block) return true;
        }
        return false;
    }

    public boolean has(Block block, int amount){
        return get(block) >= amount;
    }

    public boolean has(BlockStack[] stacks){
        for(BlockStack stack : stacks){
            if(!has(stack.block, stack.amount)) return false;
        }
        return true;
    }

    public boolean has(BlockStack[] stacks, float multiplier){
        for(BlockStack stack : stacks){
            if(!has(stack.block, Math.round(stack.amount * multiplier))) return false;
        }
        return true;
    }

    public int total(){
        return total;
    }

    public Block take(){
        if(blockstacks.size == 0) return null;
        Block result = blockstacks.get(0).block;
        remove(result, 1);
        return result;
    }

    public Block taste(){
        if(blockstacks.size == 0) return null;
        return blockstacks.get(0).block;
    }

    public int get(Block block){
        for(BlockStack stack : blockstacks){
            if(stack.block == block) return stack.amount;
        }
        return 0;
    }

    public void set(Block block, int amount){
        for(BlockStack stack : blockstacks){
            if(stack.block == block){
                total += (amount - stack.amount);
                if(amount == 0){
                    blockstacks.remove(stack);
                }else{
                    stack.amount = amount;
                }
                return;
            }
        }
    }

    public void add(Block block, int amount){
        total += amount;
        for(BlockStack stack : blockstacks){
            if(stack.block == block){
                stack.amount += amount;
                return;
            }
        }
        blockstacks.add(new BlockStack(block, amount));
    }

    public void addAll(BlockModule blocks){
        for(BlockStack stack : blocks.blockstacks){
            add(stack.block, stack.amount);
        }
    }

    public void remove(Block block, int amount){
        for(BlockStack stack : blockstacks){
            if(stack.block == block){
                amount = Math.min(amount, stack.amount);
                total -= amount;
                if(amount == stack.amount){
                    blockstacks.remove(stack);
                }else{
                    stack.amount -= amount;
                }
                return;
            }
        }
    }

    public void remove(BlockStack stack){
        remove(stack.block, stack.amount);
    }

    public void clear(){
        blockstacks.clear();
        total = 0;
    }

    @Override
    public void write(DataOutput stream) throws IOException{
        stream.writeShort(blockstacks.size);
        for(BlockStack stack : blockstacks){
            stream.writeShort(stack.block.id);
            stream.writeInt(stack.amount);
        }
    }

    @Override
    public void read(DataInput stream) throws IOException{
        short count = stream.readShort();
        for(int i = 0; i < count; i++){
            blockstacks.add(new BlockStack(Vars.content.getByID(ContentType.block, stream.readShort()), stream.readInt()));
        }
    }

    public interface BlockConsumer{
        void accept(Block block, float amount);
    }

    public interface BlockCalculator{
        float get(Block block, int amount);
    }
}
