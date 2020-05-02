package sonnicon.venture.world.blocks.logic;

import io.anuke.arc.Core;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.math.Angles;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.entities.type.Unit;
import io.anuke.mindustry.graphics.Pal;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.util.UnitsUtil;

public class EntityPointerBlock extends EntityDetectorBlock{
    protected TextureRegion middle, bottom;

    public EntityPointerBlock(String name){
        super(name);
        configurable = true;
        hasPower = true;
        consumesPower = true;
        rotate = false;
        scanDelay = 15f;
        consumes.power(8f);
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find(name + "-top");
        middle = Core.atlas.find(name + "-middle");
        bottom = Core.atlas.find(name + "-bottom");
    }

    @Override
    public void update(Tile tile){
        EntityDetectorBlockEntity entity = tile.entity();
        entity.lastSignal = entity.nextSignal;
        entity.nextSignal = this.signal(tile);
        if(entity.cons.valid()){
            boolean following = lastScanFrame == Core.graphics.getFrameId();
            if(following || lastScanFrame + 20 <= Core.graphics.getFrameId()){
                if(!following) lastScanFrame = Core.graphics.getFrameId();
                Unit target = getTarget(tile);
                if(target == null){
                    entity.nextSignal = 0;
                }else{
                    entity.nextSignal = (int) Angles.angle(tile.worldx() + tile.block().size * 4, tile.worldy() + tile.block().size * 4, target.getX(), target.getY());
                }
            }
        }else{
            entity.nextSignal = 0;
        }
    }

    @Override
    public void draw(Tile tile){
        EntityDetectorBlockEntity entity = tile.entity();
        Draw.rect(bottom, tile.drawx(), tile.drawy());
        Draw.color(entity.nextSignal > 0 ? Pal.accent : Color.white);
        Draw.rect(middle, tile.drawx(), tile.drawy(), (entity.nextSignal - 90) % 360);
        Draw.color();
        Draw.rect(region, tile.drawx(), tile.drawy());
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Draw.rect(name + "-full", x, y);
        super.drawPlace(x, y, rotation, valid);
    }

    protected Unit getTarget(Tile tile){
        EntityDetectorBlockEntity entity = tile.entity();
        return UnitsUtil.closest(tile.worldx(), tile.worldy(), Vars.tilesize * (entity.setting + size / 2f), b -> true);
    }
}
