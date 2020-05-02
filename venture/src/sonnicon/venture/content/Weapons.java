package sonnicon.venture.content;

import io.anuke.mindustry.gen.Sounds;
import io.anuke.mindustry.type.Weapon;
import sonnicon.venture.core.Loader;
import sonnicon.venture.types.LiquidWeapon;

import static sonnicon.venture.Venture.MOD_NAME;

public class Weapons implements Loader.Load{
    public static Weapon foamblaster;

    @Override
    public void load(){
        foamblaster = new LiquidWeapon(){{
            //the constructor that does this is protected
            name = MOD_NAME + "foamblaster";

            bullet = Bullets.foamBubble;
            reload = 20f;
            shots = 2;
            inaccuracy = 10f;
            recoil = 0.2f;
            lengthRand = 5f;
            alternate = true;
            shootSound = Sounds.splash;

            liquidCost = 10f;
            liquidType = Liquids.liquidFoam;
        }};
    }
}
