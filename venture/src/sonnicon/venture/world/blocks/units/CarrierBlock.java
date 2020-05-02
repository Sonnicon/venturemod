package sonnicon.venture.world.blocks.units;

import io.anuke.arc.Core;
import io.anuke.arc.collection.Array;
import io.anuke.mindustry.entities.Units;
import io.anuke.mindustry.entities.type.Unit;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.types.OnMove;

import static io.anuke.mindustry.Vars.tilesize;

public class CarrierBlock extends Block implements OnMove{
    public Array<Unit> moved = new Array<>();
    public long lastFrameCleared = -1;

    public CarrierBlock(String name){
        super(name);
        solid = false;
        destructible = true;
        controllable = false;
        hasShadow = false;
    }

    @Override
    public void beforeMoved(Tile tile, int direction){
        if(lastFrameCleared != Core.graphics.getFrameId()){
            lastFrameCleared = Core.graphics.getFrameId();
            moved.clear();
        }
    }

    @Override
    public void afterMoved(Tile tile, int direction){
        float siz = size * tilesize;
        Tile prev = tile.getNearby((direction + 2) % 4);
        Units.nearby(prev.drawx() - siz/2f, prev.drawy() - siz/2f, siz, siz, unit -> {
            if(moved.contains(unit)) return;
            unit.setNet(
                    unit.getX() + (direction % 2 == 0 ? -direction + 1 : 0) * tilesize,
                    unit.getY() + (direction % 2 == 1 ? -direction + 2 : 0) * tilesize);
            moved.add(unit);
        });
    }
}
