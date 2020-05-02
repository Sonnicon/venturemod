package sonnicon.venture.world.blocks.defence;

import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.math.geom.Vector2;
import io.anuke.arc.util.Time;
import io.anuke.mindustry.content.Fx;
import io.anuke.mindustry.entities.Damage;
import io.anuke.mindustry.entities.Effects;
import io.anuke.mindustry.gen.Sounds;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.logic.AcceptorLogicBlock;

import static io.anuke.mindustry.Vars.*;

public class ExplosivesBlock extends AcceptorLogicBlock{
    protected int explosionRadius = 12;
    protected int explosionDamage = 1600;

    protected final Vector2 tr = new Vector2();

    public ExplosivesBlock(String name){
        super(name);
        hasShadow = false;
        rotate = false;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        LogicEntity entity = tile.entity();

        if(entity.nextSignal > 0){
            entity.kill();
        }
    }

    @Override
    public void onDestroyed(Tile tile){
        super.onDestroyed(tile);
        Sounds.explosionbig.at(tile);
        Damage.damage(tile.worldx(), tile.worldy(), explosionRadius * tilesize, explosionDamage * 4);

        for(int i = 0; i < 10; i++){
            Time.run(Mathf.random(5), () -> {
                tr.rnd(Mathf.random(25f));
                Effects.effect(Fx.explosion, tr.x + tile.worldx(), tr.y + tile.worldy());
            });
        }
    }

    @Override
    public void draw(Tile tile){
        Draw.rect(region, tile.drawx(), tile.drawy());
    }
}
