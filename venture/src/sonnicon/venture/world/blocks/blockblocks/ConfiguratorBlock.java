package sonnicon.venture.world.blocks.blockblocks;

import io.anuke.arc.Core;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.mindustry.graphics.Pal;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;

import static sonnicon.venture.Venture.MOD_NAME;

public class ConfiguratorBlock extends Block{
    public ConfiguratorBlock(String name){
        super(name);
        rotate = true;
        update = true;
        controllable = true;
    }

    @Override
    public void load(){
        super.load();
        Core.atlas.getRegionMap().put("block-" + name + "-full", Core.atlas.getRegionMap().remove(name + "-full"));
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        if(tile.entity().enabled() && tile.front() != null && tile.front().block().hasEntity() && tile.back() != null && tile.back().block().hasEntity()){
            tile.front().block().configured(tile.front(), null, tile.back().entity().config());
        }
    }

    @Override
    public void draw(Tile tile){
        Draw.rect(MOD_NAME + "configurator-base", tile.drawx(), tile.drawy(), tile.rotation() * 90);
        Draw.color(tile.back() != null && tile.back().block().hasEntity() ? Pal.accent : Color.white);
        Draw.rect(this.region, tile.drawx(), tile.drawy(), tile.rotation() * 90);
        Draw.color();
    }
}
