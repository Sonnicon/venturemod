package sonnicon.venture.entities.bullets;

import io.anuke.arc.Core;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.math.Mathf;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.entities.bullet.BasicBulletType;
import io.anuke.mindustry.entities.type.Bullet;
import io.anuke.mindustry.game.Team;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.content.Blocks;
import sonnicon.venture.content.StatusEffects;
import sonnicon.venture.world.blocks.defence.FoamBlock;

import static sonnicon.venture.Venture.MOD_NAME;

public class FoamBullet extends BasicBulletType{
    public float solidifychance = 0.3f;

    protected TextureRegion[] regions;

    public FoamBullet(float speed){
        super(speed, 0, MOD_NAME + "foambubble");
        shootEffect = io.anuke.mindustry.content.Fx.none;
        smokeEffect = io.anuke.mindustry.content.Fx.none;
        inaccuracy = 10f;
        status = StatusEffects.foamy;
        statusDuration = 60 * 2f;
        lifetime = 60f;
        collidesTeam = true;
        keepVelocity = false;
    }

    @Override
    public void load(){
        regions = new TextureRegion[3];
        for(int i = 0; i < regions.length; i++){
            regions[i] = Core.atlas.find(bulletSprite + i);
        }
    }

    @Override
    public void draw(Bullet b){
        Draw.rect(regions[b.id % 3], b.x, b.y);
    }

    @Override
    public boolean collides(Bullet bullet, Tile tile){
        if(tile.block() instanceof FoamBlock) return true;
        return super.collides(bullet, tile);
    }

    @Override
    public void hitTile(Bullet b, Tile tile){
        for(byte i = 0; i < 4; i++){
            tryPlaceFoam(tile.getNearby(i), b.getTeam());
        }
    }

    @Override
    public void hit(Bullet b, float x, float y){
        tryPlaceFoam((int)x / 8, (int)y / 8, b.getTeam());
    }

    @Override
    public void despawned(Bullet b){
        tryPlaceFoam((int)b.x / 8, (int)b.y / 8, b.getTeam());
    }

    protected void tryPlaceFoam(int x, int y, Team team){
        tryPlaceFoam(Vars.world.tile(x, y), team);
    }

    protected void tryPlaceFoam(Tile tile, Team team){
        if(Mathf.chance(solidifychance)){
            if(tile != null && tile.block() == io.anuke.mindustry.content.Blocks.air){
                tile.setBlock(Blocks.foamBlock, team, Mathf.randomSeed(tile.pos(), 0, 3));
            }
        }
    }
}
