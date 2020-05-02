package sonnicon.venture.world.blocks.logic;

import io.anuke.arc.Core;
import io.anuke.arc.func.Cons;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.scene.ui.Label;
import io.anuke.arc.scene.ui.layout.Table;
import io.anuke.arc.util.ArcAnnotate;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.entities.Units;
import io.anuke.mindustry.entities.type.Player;
import io.anuke.mindustry.entities.type.Unit;
import io.anuke.mindustry.ui.Styles;
import io.anuke.mindustry.world.Tile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static sonnicon.venture.Venture.MOD_NAME;

public class EntityDetectorBlock extends ModLogicBlock{
    protected static long lastScanFrame = -1;
    public float scanDelay = 30f;
    public int maxRange = 32;

    protected static AtomicInteger count = new AtomicInteger();

    public EntityDetectorBlock(String name){
        super(name);
        configurable = true;
        hasPower = true;
        consumesPower = true;
        consumes.power(2f);
        entityType = EntityDetectorBlockEntity::new;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        EntityDetectorBlockEntity entity = tile.entity();
        if(entity.cons.valid()){
            boolean following = lastScanFrame == Core.graphics.getFrameId();
            if(following || lastScanFrame + 20 <= Core.graphics.getFrameId()){
                if(!following) lastScanFrame = Core.graphics.getFrameId();
                count.set(0);
                getNearby(tile, c -> count.getAndIncrement());
                entity.nextSignal = count.intValue();
            }
        }else{
            entity.nextSignal = 0;
        }
    }

    protected void getNearby(Tile tile, Cons<Unit> cons){
        EntityDetectorBlockEntity entity = tile.entity();
        float width =  Vars.tilesize * (entity.setting * 2f + size);
        Units.nearby(tile.worldx() - (entity.setting + size / 2f) * Vars.tilesize, tile.worldy() - (entity.setting + size / 2f) * Vars.tilesize, width, width, cons);
    }

    @Override
    public int signal(Tile tile){
        EntityDetectorBlockEntity entity = tile.entity();
        return entity.nextSignal;
    }

    @Override
    public void buildTable(Tile tile, Table table){
        EntityDetectorBlockEntity entity = tile.entity();
        Table t = new Table(Styles.black6);
        t.add(new Label("$" + MOD_NAME.substring(0, MOD_NAME.length() - 1) + ".setrange")).colspan(2);
        t.row();
        t.addSlider(0, maxRange, 1, entity.setting, f -> {
            tile.configure((int) f);
        }).width(200f).pad(5f);
        t.label(() -> entity.setting + "").pad(5f).width(20f);
        table.add(t).width(240f).grow();
    }

    @Override
    public void configured(Tile tile, @ArcAnnotate.Nullable Player player, int value){
        if(!(tile.block() instanceof EntityDetectorBlock)) return;
        EntityDetectorBlockEntity entity = tile.entity();
        entity.setting = Mathf.clamp(value, 0, maxRange);
    }

    public class EntityDetectorBlockEntity extends LogicEntity{
        public int setting = 10;

        @Override
        public int config(){
            return setting;
        }

        @Override
        public void write(DataOutput stream) throws IOException{
            super.write(stream);
            stream.writeInt(setting);
        }

        @Override
        public void read(DataInput stream, byte revision) throws IOException{
            super.read(stream, revision);
            setting = stream.readInt();
        }
    }
}
