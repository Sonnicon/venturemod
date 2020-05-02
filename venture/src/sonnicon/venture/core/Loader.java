package sonnicon.venture.core;

import sonnicon.venture.content.*;

public class Loader {
    public static Load[] loads = {
            new Categories(),
            new Liquids(),
            new Fx(),
            new StatusEffects(),
            new Bullets(),
            new Weapons(),
            new Mechs(),
            new Blocks()
    };

    public static void load(){
        for(Load l : loads) l.load();
    }

    public interface Load{
        void load();
    }
}
