package sonnicon.venture.types;

import io.anuke.arc.Core;
import io.anuke.mindustry.type.Item;
import io.anuke.mindustry.type.Liquid;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.core.Vars;
import sonnicon.venture.world.blocks.distribution.*;

import java.util.ArrayList;
import java.util.Arrays;

public class DuctNetwork{
    private ArrayList<Tile> tiles = new ArrayList<>();
    private ArrayList<Tile>[] ios = new ArrayList[DuctIOType.values().length];
    private long lastUpdated = -1;

    private static ArrayList<Tile>[] reqTilesChannels = new ArrayList[Vars.channelColours.length];
    private static float[] reqs = new float[Vars.channelColours.length];
    private static float[] prov = new float[Vars.channelColours.length];
    private static ArrayList<Tile> reqTiles = new ArrayList<>();

    public DuctNetwork(){
        for(int i = 0; i < ios.length; i++) ios[i] = new ArrayList<Tile>();
    }

    public DuctNetwork(Tile tile){
        this();
        add(tile);
    }

    public void update(){
        if(Core.graphics.getFrameId() == lastUpdated) return;
        lastUpdated = Core.graphics.getFrameId();

        for(int i = 0; i < reqTilesChannels.length; i++) reqTilesChannels[i] = new ArrayList<Tile>();
        Arrays.fill(reqs, 0f);
        Arrays.fill(prov, 0f);

        for(Tile tile : ios[DuctIOType.power.ordinal()]){
            DuctIO.DuctIOEntity entity = tile.entity();
            float capacity = getCapacity(tile);
            if(entity.input || !entity.enabled()) continue;
            reqs[entity.setting] += capacity - tile.entity.power.status * capacity;
            reqTilesChannels[entity.setting].add(tile);
        }
        for(Tile tile : ios[DuctIOType.power.ordinal()]){
            DuctIO.DuctIOEntity entity = tile.entity();
            float capacity = getCapacity(tile);
            if(!entity.input || !entity.enabled()) continue;
            float inc = Math.min(tile.entity.power.status * capacity, reqs[entity.setting] - prov[entity.setting]);
            prov[entity.setting] += inc;
            tile.entity.power.status -= inc / capacity;
            if(reqs[entity.setting] - prov[entity.setting] < 0.01f) break;
        }
        for(int i = 0; i < reqTilesChannels.length; i++){
            for(Tile tile : reqTilesChannels[i]){
                float capacity = getCapacity(tile);
                float ch = Math.min(prov[i], capacity - tile.entity.power.status * capacity);
                tile.entity.power.status += ch / capacity;
                prov[i] -= ch;
                if(ch < 0.01f) break;
            }
        }
    }

    public void reLogic(int channel){
        reqTiles.clear();
        int acc = 0;
        for(Tile tile : ios[DuctIOType.logic.ordinal()]){
            DuctLogicIO.DuctLogicIOEntity entity = tile.entity();
            if(entity.setting == channel){
                if(entity.input) acc |= entity.signal;
                else reqTiles.add(tile);
            }
        }
        for(Tile tile : reqTiles){
            DuctLogicIO.DuctLogicIOEntity entity = tile.entity();
            entity.signal = acc;
        }
    }

    private float getCapacity(Tile tile){
        return tile.block().consumes.getPower().capacity;
    }

    public void add(Tile tile){
        DuctType.DuctTypeEntity entity = tile.entity();
        tiles.add(tile);
        if(tile.block() instanceof DuctIO){
            ios[((DuctIO) tile.block()).type.ordinal()].add(tile);
        }
        entity.ductNetwork = this;
    }

    public void add(ArrayList<Tile> tiles){
        for(Tile t : tiles){
            add(t);
        }
    }

    public void remove(Tile tile){
        DuctType.DuctTypeEntity entity = tile.entity();
        tiles.remove(tile);
        if(tile.block() instanceof DuctIO){
            ios[((DuctIO) tile.block()).type.ordinal()].remove(tile);
        }

        entity.ductNetwork = null;
    }

    public void remove(int index){
        remove(tiles.get(index));
    }

    public void clear(){
        while(tiles.size() > 0){
            remove(0);
        }
        Arrays.fill(ios, new ArrayList<Tile>());
    }

    public boolean canAccept(Tile tile, Tile source){
        DuctIO.DuctIOEntity entity = tile.entity();
        return entity.enabled() && !entity.input && entity.setting == ((DuctIO.DuctIOEntity)source.entity()).setting;
    }

    public boolean canAcceptItems(Tile tile){
        for(Tile t : ios[DuctIOType.item.ordinal()]){
            if(t == tile) continue;
            if(t.entity().enabled() && t.block().hasItems && canAccept(t, tile) && t.entity().items.total() < t.block().itemCapacity) return true;
        }
        return false;
    }

    public void handleItem(Item item, Tile tile){
        for(Tile t : ios[DuctIOType.item.ordinal()]){
            if(t == tile || !t.entity().enabled() || !t.block().hasItems || !canAccept(t, tile) || t.entity().items.total() >= t.block().itemCapacity) continue;
            t.entity().items.add(item, 1);
            return;
        }
    }

    public boolean canAcceptLiquids(Tile tile, Liquid liquid, Float amount){
        float space = 0f;
        for(Tile t : ios[DuctIOType.liquid.ordinal()]){
            if(t == tile) continue;
            if(t.entity().enabled() && t.block().hasLiquids && canAccept(t, tile)) space += t.block().liquidCapacity - t.entity.liquids.total();
        }
        return space > 0f;
    }

    public void handleLiquid(Tile tile, Liquid liquid, Float amount){
        for(Tile t : ios[DuctIOType.liquid.ordinal()]){
            if(t == tile || !t.entity().enabled() || !t.block().hasLiquids || !canAccept(t, tile) || tile.entity().liquids.total() >= tile.block().liquidCapacity) continue;
            float vol = Math.min(t.block().liquidCapacity - t.entity().liquids.total(), amount);
            t.entity().liquids.add(liquid, vol);
            amount -= vol;
            if(amount <= 0) return;
        }
    }

    public ArrayList<Tile> getTiles(){
        return tiles;
    }
}
