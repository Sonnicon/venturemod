package sonnicon.venture.types;

import io.anuke.mindustry.world.Block;

public class BlockStack implements Comparable<BlockStack>{
    public Block block;
    public int amount;

    public BlockStack(Block block, int amount){
        this.block = block;
        this.amount = amount;
    }

    //serialization only
    public BlockStack(){
    }

    public BlockStack copy(){
        return new BlockStack(block, amount);
    }

    public boolean equals(BlockStack other){
        return other != null && other.block == block && other.amount == amount;
    }

    @Override
    public int compareTo(BlockStack blockStack){
        return block.compareTo(blockStack.block);
    }

    @Override
    public String toString(){
        return "BlockStack{" +
        "block=" + block.name +
        ", amount=" + amount +
        '}';
    }
}
