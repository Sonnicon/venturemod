package sonnicon.venture.world.blocks.transportation;

import io.anuke.arc.util.Structs;
import io.anuke.mindustry.content.Blocks;
import io.anuke.mindustry.world.Tile;

import java.util.HashSet;
import java.util.Set;

public class AttractorBlock extends RepulsorBlock{
    public AttractorBlock(String name){
        super(name);
    }

    @Override
    public void update(Tile tile){
        LogicEntity entity = tile.entity();
        entity.lastSignal = entity.nextSignal;
        entity.nextSignal = this.signal(tile);

        if(entity.nextSignal > 0 && entity.lastSignal == 0 && tile.front() != null && tile.front().getNearby(tile.rotation()) != null && tile.front().block() == Blocks.air && tile.front().getNearby(tile.rotation()).block() != Blocks.air){
            origin = tile;
            tilesDone.clear();
            tomove.clear();
            int reverserotation = (tile.rotation() + 2) % 4;
            if(canMove(tile.front().getNearby(tile.rotation()), reverserotation)){
                //failsafe
                Set<Tile> set = new HashSet<>(tomove);
                tomove.clear();
                tomove.addAll(set);
                tomove.sort(Structs.comparing(t -> (reverserotation % 2 == 0 ? t.x : t.y) * (reverserotation > 1 ? 1 : -1)));
                tomove.iterator().forEachRemaining(t -> move(t, reverserotation));
            }
        }
    }
}
