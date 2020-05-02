package sonnicon.venture.content;

import io.anuke.mindustry.type.Category;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.blocks.logic.LogicBlock;
import sonnicon.sonnicore.world.BlockUtils;
import sonnicon.sonnicore.types.ModCategory;
import sonnicon.venture.core.Loader;

import static sonnicon.venture.Venture.MOD_NAME;

public class Categories implements Loader.Load {
    public static ModCategory logic, world;

    @Override
    public void load() {
        logic = new ModCategory("logic", MOD_NAME + "icon-logic");
        world = new ModCategory("world", MOD_NAME + "icon-world");

        for(Block b : io.anuke.mindustry.Vars.content.blocks()){
            if(b.category == Category.effect && b instanceof LogicBlock){
                BlockUtils.setCategory(b, Categories.logic);
            }
        }
    }
}
