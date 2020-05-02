package sonnicon.venture.world.blocks.units;

import io.anuke.arc.Core;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.mindustry.content.Liquids;
import io.anuke.mindustry.type.Liquid;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.units.MechPad;
import io.anuke.mindustry.world.meta.BlockStat;

public class LiquidMechPad extends MechPad{
    public Liquid liquidType = Liquids.water;
    public TextureRegion[] liquidRegions;
    public int liquidRegionAmount = 3;
    protected float[] bounds;

    public LiquidMechPad(String name){
        super(name);
        hasLiquids = true;
        liquidCapacity = 300f;
    }

    @Override
    public void load(){
        super.load();
        liquidRegions = new TextureRegion[liquidRegionAmount];
        bounds = new float[liquidRegionAmount];
        float stage = (liquidCapacity - 10f) / liquidRegionAmount;
        for(int i = 0; i < liquidRegionAmount; i++){
            liquidRegions[i] = Core.atlas.find(name + "-" + i);
            bounds[i] = stage * i;
        }
    }

    @Override
    public void draw(Tile tile){
        super.draw(tile);
        MechFactoryEntity entity = tile.entity();
        for(int i = 0; i < bounds.length; i++){
            if(entity.liquids.total() > bounds[i]){
                Draw.rect(liquidRegions[i], tile.drawx(), tile.drawy(), rotate ? tile.rotation() * 90 : 0);
            }else{
                break;
            }
        }
    }

    @Override
    public boolean acceptLiquid(Tile tile, Tile source, Liquid liquid, float amount){
        return liquid == liquidType && tile.entity.liquids.get(liquid) + amount < liquidCapacity;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(BlockStat.input, liquidType, 0f, false);
    }
}
