package sonnicon.venture;

import io.anuke.mindustry.mod.Mod;
import io.anuke.mindustry.mod.Mods;
import sonnicon.venture.core.Loader;
import sonnicon.venture.core.Vars;

public class Venture extends Mod {
    public static final String MOD_NAME = "venture-";
    public static Mods.LoadedMod loadedMod;

    @Override
    public void loadContent(){
        loadedMod = io.anuke.mindustry.Vars.mods.getMod(Venture.class);
        Loader.load();
    }

    @Override
    public void init() {
        Vars.init();
    }
}
