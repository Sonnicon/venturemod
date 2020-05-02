package sonnicon.venture.world.blocks.logic;

import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.mindustry.content.Blocks;
import io.anuke.mindustry.graphics.Pal;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.logic.LogicBlock;

import static sonnicon.venture.Venture.MOD_NAME;

public class BlockSensorBlock extends ModLogicBlock{
    public BlockSensorBlock(String name){
        super(name);
    }

    @Override
    public int signal(Tile tile){
        return (tile.back() != null && tile.back().block() != Blocks.air) ? tile.rotation() + 1 : 0;
    }

    @Override
    public void draw(Tile tile){
        LogicBlock.LogicEntity entity = tile.entity();
        Draw.rect(MOD_NAME + "interactor-base", tile.drawx(), tile.drawy(), this.rotate ? (float)(((tile.rotation() + 2) % 4) * 90) : 180f);
        Draw.color(entity.nextSignal > 0 ? Pal.accent : Color.white);
        Draw.rect(this.region, tile.drawx(), tile.drawy(), this.rotate ? (float)(tile.rotation() * 90) : 0f);
        Draw.color();
    }
}
