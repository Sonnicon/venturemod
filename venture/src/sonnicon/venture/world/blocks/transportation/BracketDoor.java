package sonnicon.venture.world.blocks.transportation;

import io.anuke.arc.Core;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.mindustry.entities.Units;
import io.anuke.mindustry.entities.type.Player;
import io.anuke.mindustry.entities.type.TileEntity;
import io.anuke.mindustry.world.Tile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static io.anuke.mindustry.Vars.pathfinder;

public class BracketDoor extends BracketBlock{
    protected TextureRegion openRegion;

    public BracketDoor(String name){
        super(name);
        update = true;
        solid = false;
        solidifes = true;
        consumesTap = true;
        controllable = true;
        entityType = BracketDoorEntity::new;
    }

    @Override
    public void load(){
        super.load();
        openRegion = Core.atlas.find(name + "-open");
    }

    @Override
    public void draw(Tile tile){
        BracketDoorEntity entity = tile.entity();

        if(entity.open) Draw.rect(openRegion, tile.drawx(), tile.drawy());
        else Draw.rect(region, tile.drawx(), tile.drawy());
    }

    @Override
    public boolean isSolidFor(Tile tile){
        return !((BracketDoorEntity)tile.entity()).open;
    }

    @Override
    public void tapped(Tile tile, Player player){
        BracketDoorEntity entity = tile.entity();

        if(Units.anyEntities(tile) && entity.open) return;

        setDoorOpen(tile, !entity.open);
    }

    @Override
    public void update(Tile tile){
        BracketDoorEntity entity = tile.entity();
        if((entity.lastSignal != tile.entity.enabled())){
            entity.lastSignal = tile.entity.enabled();
            if(entity.open != tile.entity.enabled()){
                setDoorOpen(tile, !entity.open);
            }
        }
    }

    protected void setDoorOpen(Tile tile, boolean open){
        BracketDoorEntity entity = tile.entity();
        entity.open = open;
        pathfinder.updateTile(tile);
        byte direction = -1;
        for(Tile t : entity.proximity()){
            if(t.block() instanceof BracketDoor){
                direction = tile.relativeTo(t);
                break;
            }
        }
        if(direction == -1) return;
        chainDoorOpen(tile, open, direction);
        if(tile.getNearby((direction + 2) % 4).block() instanceof BracketDoor) chainDoorOpen(tile, open, (byte) ((direction + 2) % 4));
    }

    protected void chainDoorOpen(Tile tile, boolean open, byte direction){
        Tile t = tile.getNearby(direction);
        while(t != null){
            ((BracketDoorEntity)t.entity()).open = open;
            t = t.getNearby(direction);
            if(!(t.block() instanceof BracketDoor)) t = null;
        }
    }

    public class BracketDoorEntity extends TileEntity{
        public boolean open = false;
        public boolean lastSignal = true;

        @Override
        public void write(DataOutput stream) throws IOException{
            super.write(stream);
            stream.writeBoolean(open);
        }

        @Override
        public void read(DataInput stream, byte revision) throws IOException{
            super.read(stream, revision);
            open = stream.readBoolean();
        }
    }
}
