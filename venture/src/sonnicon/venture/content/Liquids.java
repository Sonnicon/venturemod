package sonnicon.venture.content;

import io.anuke.arc.graphics.Color;
import io.anuke.mindustry.type.Liquid;
import sonnicon.venture.core.Loader;

import static sonnicon.venture.Venture.MOD_NAME;

public class Liquids implements Loader.Load{
    public static Liquid liquidFoam;

    @Override
    public void load(){
        liquidFoam = new Liquid(MOD_NAME + "liquid-foam", Color.valueOf("eeeeee")){{
            flammability = 0f;
            heatCapacity = 0f;
            viscosity = 1.2f;
            explosiveness = 0f;
            effect = StatusEffects.foamy;
        }};
    }
}
