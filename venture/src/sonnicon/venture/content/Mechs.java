package sonnicon.venture.content;

import io.anuke.arc.graphics.Color;
import io.anuke.mindustry.type.Mech;
import sonnicon.venture.core.Loader;

import static sonnicon.venture.Venture.MOD_NAME;

public class Mechs implements Loader.Load{
    public static Mech foam;

    @Override
    public void load(){
        foam = new Mech(MOD_NAME + "foam-mech", false){{
            drillPower = 1;
            mineSpeed = 0.3f;
            mass = 1.5f;
            speed = 0.3f;
            itemCapacity = 0;
            boostSpeed = 0.6f;
            engineColor = Color.valueOf("#eeeeee");
            health = 350f;
            weapon = Weapons.foamblaster;
            weaponOffsetX = 5.5f;
            weaponOffsetY = -0.25f;
        }};
    }
}
