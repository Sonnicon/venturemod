package sonnicon.venture.world.blocks.distribution;

import io.anuke.arc.collection.Array;
import io.anuke.arc.math.geom.Point2;
import io.anuke.mindustry.entities.type.TileEntity;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Edges;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.types.DuctNetwork;
import sonnicon.venture.types.IMoved;

import static io.anuke.mindustry.Vars.world;

public abstract class DuctType extends Block implements IMoved{

    public DuctType(String name){
        super(name);
        rotate = false;
        destructible = true;
        entityType = DuctTypeEntity::new;
    }

    @Override
    public void update(Tile tile){
        DuctTypeEntity entity = tile.entity();
        if(entity.ductNetwork != null){
            entity.ductNetwork.update();
        }else{
            addNetwork(tile);
        }
    }

    @Override
    public void placed(Tile tile){
        super.placed(tile);
        addNetwork(tile);
    }

    // Merge self and nearby networks
    public void addNetwork(Tile tile){
        DuctTypeEntity entity = tile.entity();
        if(entity.ductNetwork != null) return;
        for(Tile t : entity.proximity()){
            if(!(t.block() instanceof DuctType)) continue;
            DuctTypeEntity oe = t.entity();
            if(oe.ductNetwork == null) continue;
            if(entity.ductNetwork == null){
                oe.ductNetwork.add(tile);
                entity.ductNetwork = oe.ductNetwork;
            }else if(entity.ductNetwork != oe.ductNetwork){
                mergeNetworks(entity.ductNetwork, oe.ductNetwork);
            }
        }
        if(entity.ductNetwork == null) entity.ductNetwork = new DuctNetwork(tile);
    }

    public void mergeNetworks(DuctNetwork n1, DuctNetwork n2){
        if(n1.getTiles().size() > n2.getTiles().size()){
            n1.add(n2.getTiles());
            n2.getTiles().clear();
        }else{
            n2.add(n1.getTiles());
            n1.getTiles().clear();
        }
    }

    @Override
    public void removed(Tile tile){
        DuctTypeEntity entity = tile.entity();
        if(entity.ductNetwork == null) return;
        entity.ductNetwork.clear();
        Array<Tile> prox = entity.proximity().copy();
        entity.removeFromProximity();
        super.removed(tile);
        for(Tile ti : prox) growNetwork(ti);
    }

    // Reconstruct network after a clear
    public void growNetwork(Tile tile){
        if(!(tile.block() instanceof DuctType)) return;
        DuctTypeEntity ent = tile.entity();
        if(ent.ductNetwork != null) return;
        ent.ductNetwork = new DuctNetwork(tile);
        for(Tile t : ent.proximity()){
            if(!(t.block() instanceof DuctType)) continue;
            expandNetwork(t, ent.ductNetwork);
        }
    }

    private void expandNetwork(Tile tile, DuctNetwork network){
        DuctTypeEntity entity = tile.entity();
        if(entity.ductNetwork != null) return;
        entity.ductNetwork = network;
        network.add(tile);
        for(Tile t : entity.proximity()){
            if(!(t.block() instanceof DuctType)) continue;
            expandNetwork(t, network);
        }
    }

    @Override
    public void beforeMoved(Tile tile, int direction){
        DuctTypeEntity entity = tile.entity();
        entity.ductNetwork.clear();
    }

    @Override
    public void afterMoved(Tile tile, int direction){
        Tile back = tile.getNearby((direction + 2) % 4);
        for(Point2 point : Edges.getEdges(back.block().size)){
            Tile other = world.ltile(back.x + point.x, back.y + point.y);
            if(other != tile && other.block() instanceof DuctType){
                growNetwork(other);
            }
        }
        addNetwork(tile);
    }

    public class DuctTypeEntity extends TileEntity{
        public DuctNetwork ductNetwork;
    }
}
