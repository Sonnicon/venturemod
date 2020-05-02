package sonnicon.venture.world.blocks.distribution;

import io.anuke.arc.Core;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.mindustry.ui.Cicon;
import io.anuke.mindustry.world.Tile;

import java.util.Arrays;
import java.util.HashMap;

public class Duct extends DuctType{
    protected TextureRegion[] regions;

    private static final HashMap<Integer, Integer> blends = new HashMap<Integer, Integer>(){{
        put(1, 1);
        put(2, 4);
        put(3, 10);
        put(4, 3);
        put(5, 5);
        put(6, 9);
        put(7, 14);
        put(8, 2);
        put(9, 7);
        put(10, 6);
        put(11, 11);
        put(12, 8);
        put(13, 12);
        put(14, 13);
        put(15, 15);
    }};

    public Duct(String name){
        super(name);
        floating = true;
        conveyorPlacement = true;
        hasShadow = false;
        entityType = DuctEntity::new;
    }

    @Override
    public void load(){
        regions = new TextureRegion[16];
        for(int i = 0; i < regions.length; i++){
            regions[i] = Core.atlas.find(name + "-" + i);
        }
        cicons[Cicon.medium.ordinal()] = regions[0];
    }

    @Override
    public void draw(Tile tile){
        DuctEntity entity = tile.entity();
        if(entity == null) return;
        /*Random r = new Random(entity.ductNetwork.getTiles().get(0).pos());
        Draw.color(r.nextFloat(), r.nextFloat(), r.nextFloat());*/
        Draw.rect(regions[entity.blend], tile.drawx(), tile.drawy());
        //Draw.color();
    }

    @Override
    public void onProximityUpdate(Tile tile){
        super.onProximityUpdate(tile);
        DuctEntity entity = tile.entity();
        int touching = 0;
        boolean[] a = new boolean[4];
        Arrays.fill(a, false);
        for(Tile t : entity.proximity()){
            if(t.block() instanceof DuctType){
                a[tile.relativeTo(t)] = true;
            }
        }
        for (int i = 0; i < 4; ++i) {
            touching = (touching >> 1) + (a[i] ? 8 : 0);
        }
        entity.blend = blends.getOrDefault(touching, 0);
    }

    @Override
    public TextureRegion[] generateIcons(){
        return new TextureRegion[]{regions[0]};
    }

    @Override
    public TextureRegion icon(Cicon icon){
        if(cicons[icon.ordinal()] == null){
            cicons[icon.ordinal()] = regions[0];
        }
        return cicons[icon.ordinal()];
    }

    public class DuctEntity extends DuctTypeEntity{
        int blend = 0;
    }
}
