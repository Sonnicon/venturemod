package sonnicon.venture.content;

import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.Fill;
import io.anuke.arc.graphics.g2d.Lines;
import io.anuke.arc.math.Angles;
import io.anuke.arc.math.Mathf;
import io.anuke.mindustry.entities.Effects;
import sonnicon.venture.core.Loader;

import static sonnicon.venture.Venture.MOD_NAME;

public class Fx implements Loader.Load{
    public static Effects.Effect foambubbles, foamdroplets;
    @Override
    public void load(){
        foambubbles = new Effects.Effect(80f, e -> {
            Draw.rect(MOD_NAME + "bubblybubble" + Mathf.randomSeed(e.id,0, 1),
                    e.x + Angles.trnsx(Mathf.randomSeedRange(e.id, 360), e.time / 8f),
                    e.y + Angles.trnsy(Mathf.randomSeedRange(e.id, 360), e.time / 8f));
            if(Mathf.randomSeed(e.id) > 0.95f){
                //pop
                e.time = e.lifetime;
            }
        });

        foamdroplets = new Effects.Effect(40f, e -> {
            Lines.stroke(2f);
            Draw.color(Color.valueOf("eeeeee"));
            Angles.randLenVectors(e.id, 5, e.finpow() * 6f, e.rotation, 20f, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * 1.5f);
            });
            Draw.reset();
        });
    }
}
