package sonnicon.venture.types;

import io.anuke.mindustry.content.Liquids;
import io.anuke.mindustry.entities.traits.ShooterTrait;
import io.anuke.mindustry.entities.traits.SpawnerTrait;
import io.anuke.mindustry.entities.type.Player;
import io.anuke.mindustry.type.Liquid;
import io.anuke.mindustry.type.Weapon;
import io.anuke.mindustry.world.blocks.units.MechPad;

public class LiquidWeapon extends Weapon{
    public Liquid liquidType = Liquids.water;
    public float liquidCost = 0f;

    @Override
    public void shoot(ShooterTrait p, float x, float y, float angle, boolean left){
        if(!(p instanceof Player)) return;
        SpawnerTrait spawner = ((Player) p).lastSpawner;
        if(!(spawner instanceof MechPad.MechFactoryEntity)) return;
        if(liquidCost > ((MechPad.MechFactoryEntity)spawner).liquids.get(liquidType)) return;
        ((MechPad.MechFactoryEntity)spawner).liquids.remove(liquidType, liquidCost);
        super.shoot(p, x, y, angle, left);
    }
}
