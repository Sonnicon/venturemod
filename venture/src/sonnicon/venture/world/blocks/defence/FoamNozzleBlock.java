package sonnicon.venture.world.blocks.defence;

import io.anuke.arc.Core;
import io.anuke.arc.collection.Array;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.math.geom.Point2;
import io.anuke.mindustry.content.Blocks;
import io.anuke.mindustry.world.Edges;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.logic.LogicBlock;
import io.anuke.mindustry.world.consumers.ConsumeType;
import sonnicon.venture.world.blocks.logic.ModLogicBlock;

import static io.anuke.mindustry.Vars.world;

public class FoamNozzleBlock extends ModLogicBlock{
    protected static Array<Tile> available = new Array<>();
    protected TextureRegion top, middle, bottom;

    private Point2[] nearby;

    public FoamNozzleBlock(String name){
        super(name);
        doOutput = false;
        hasLiquids = true;
        liquidCapacity = 120f;
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.getRegionMap().get("block-" + name + "-full");

        top = Core.atlas.find(name + "-top");
        middle = Core.atlas.find(name + "-middle");
        bottom = Core.atlas.find(name + "-bottom");
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        if(nearby == null) nearby = Edges.getEdges(size);
        LogicEntity entity = tile.entity();
        Tile back = tile.back();
        if((tile.front() != null && tile.front().block() == Blocks.air) && (!(back != null && back.block() instanceof LogicBlock && ((LogicBlock) back.block()).canSignal(tile, back)) || entity.lastSignal > 0)){
            available.clear();
            available.add(tile.front());
            int i = 0;
            while(i <= 15 && available.size > 0 && entity.cons.valid()){
                i++;
                consumes.get(ConsumeType.liquid).update(entity);
                Tile t = available.remove(0);
                if(Mathf.randomSeed(t.pos(), 0, 10) > 4){
                    t.setBlock(sonnicon.venture.content.Blocks.foamBlock, tile.getTeam(), Mathf.random(0, 3));

                    for(Point2 point : nearby){
                        Tile other = world.ltile(t.x + point.x, t.y + point.y);
                        if(other.block() == Blocks.air) available.add(other);
                    }
                }
            }
        }
    }

    @Override
    public void draw(Tile tile){
        LogicEntity entity = tile.entity();
        Draw.rect(bottom, tile.drawx(), tile.drawy());
        if(entity.liquids.total() > 0.1f){
            Draw.color(entity.liquids.current().color);
            Draw.rect(middle, tile.drawx(), tile.drawy(), (entity.nextSignal - 90) % 360);
            Draw.color();
    }
        Draw.rect(top, tile.drawx(), tile.drawy(), rotate ? tile.rotation() * 90 : 0);
    }

    @Override
    public int signal(Tile tile){
        if(tile.back().block() instanceof LogicBlock)
            return ((LogicBlock) tile.back().block()).sback(tile);
        return 0;
    }
}
