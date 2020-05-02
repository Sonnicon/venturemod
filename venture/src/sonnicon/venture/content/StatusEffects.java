package sonnicon.venture.content;

import io.anuke.mindustry.type.StatusEffect;
import sonnicon.venture.core.Loader;

public class StatusEffects implements Loader.Load{
    public static StatusEffect foamy;

    @Override
    public void load(){
        foamy = new StatusEffect(){{
            effect = Fx.foambubbles;
            speedMultiplier = 0.6f;
            armorMultiplier = 1.2f;
        }};
    }
}
