package sonnicon.venture.content;

import io.anuke.mindustry.entities.bullet.BulletType;
import sonnicon.venture.core.Loader;
import sonnicon.venture.entities.bullets.FoamBullet;

public class Bullets implements Loader.Load{
    public static BulletType foamBubble;
    @Override
    public void load(){
        foamBubble = new FoamBullet(1f);
    }
}
