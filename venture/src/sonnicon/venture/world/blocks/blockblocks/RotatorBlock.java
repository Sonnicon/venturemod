package sonnicon.venture.world.blocks.blockblocks;

import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.mindustry.content.Blocks;
import io.anuke.mindustry.graphics.Pal;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.logic.LogicBlock;
import sonnicon.venture.world.blocks.logic.ModLogicBlock;

import static sonnicon.venture.Venture.MOD_NAME;

public class RotatorBlock extends ModLogicBlock{
    public RotatorBlock(String name){
        super(name);
        doOutput = false;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        LogicEntity entity = tile.entity();
        if(entity.nextSignal > 0 && entity.lastSignal < 5 && tile.front() != null && tile.front().block() != Blocks.air && tile.front().block().rotate){
            tile.front().rotation(entity.nextSignal);
        }
    }

    @Override
    public void draw(Tile tile){
        LogicEntity entity = tile.entity();
        Draw.rect(MOD_NAME + "interactor-base", tile.drawx(), tile.drawy(), this.rotate ? (float)(tile.rotation() * 90) : 0.0F);
        Draw.color(entity.nextSignal > 0 ? Pal.accent : Color.white);
        Draw.rect(this.region, tile.drawx(), tile.drawy(), this.rotate ? (float)(tile.rotation() * 90) : 0.0F);
        Draw.color();
    }

    @Override
    public int signal(Tile tile){
        if(tile.back().block() instanceof LogicBlock)
            return ((LogicBlock) tile.back().block()).sback(tile);
        return 0;
    }
}
