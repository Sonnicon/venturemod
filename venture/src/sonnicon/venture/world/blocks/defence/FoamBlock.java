package sonnicon.venture.world.blocks.defence;

import io.anuke.mindustry.entities.type.Bullet;
import io.anuke.mindustry.entities.type.TileEntity;
import io.anuke.mindustry.entities.type.Unit;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.content.Blocks;
import sonnicon.venture.content.StatusEffects;
import sonnicon.venture.entities.bullets.FoamBullet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FoamBlock extends Block{
    public FoamBlock(String name){
        super(name);
        breakable = true;
        destructible = false;
        solid = false;
        targetable = false;
        controllable = false;
        update = true;
        hasShadow = false;
        rotate = true;
        entityType = FoamBlockTileEntity::new;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        FoamBlockTileEntity entity = tile.entity();
        if((entity.time += entity.delta()) > 60f * 30f){
            tile.setBlock(Blocks.hardFoamBlock, entity.getTeam(), tile.rotation());
        }
    }

    @Override
    public void unitOn(Tile tile, Unit unit){
        super.unitOn(tile, unit);
        unit.applyEffect(StatusEffects.foamy, 180f);
    }

    @Override
    public void drawCracks(Tile tile){}

    public class FoamBlockTileEntity extends TileEntity{
        public float time;

        @Override
        public boolean collide(Bullet other){
            return other.getBulletType() instanceof FoamBullet;
        }

        @Override
        public void write(DataOutput stream) throws IOException{
            super.write(stream);
            stream.writeFloat(time);
        }

        @Override
        public void read(DataInput stream, byte revision) throws IOException{
            super.read(stream, revision);
            time = stream.readFloat();
        }
    }

}
