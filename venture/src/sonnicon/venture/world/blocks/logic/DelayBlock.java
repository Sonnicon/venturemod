package sonnicon.venture.world.blocks.logic;

import io.anuke.arc.math.Mathf;
import io.anuke.arc.scene.ui.layout.Table;
import io.anuke.mindustry.entities.type.Player;
import io.anuke.mindustry.gen.Icon;
import io.anuke.mindustry.world.Tile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import static io.anuke.mindustry.Vars.control;
import static io.anuke.mindustry.Vars.ui;

public class DelayBlock extends ModLogicBlock{
    public int maxDelay = 32;

    public DelayBlock(String name){
        super(name);
        configurable = true;
        entityType = DelayBlockEntity::new;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        DelayBlockEntity entity = tile.entity();
        if(entity.signals.length == 0) return;
        entity.signals[entity.pointer] = sback(tile);
        entity.pointer = ++entity.pointer % entity.signals.length;
    }

    @Override
    public int signal(Tile tile){
        DelayBlockEntity entity = tile.entity();
        if(entity.signals.length == 0) return 0;
        return entity.signals[entity.pointer];
    }

    @Override
    public void buildTable(Tile tile, Table table){
        super.buildTable(tile, table);
        DelayBlockEntity entity = tile.entity();

        table.addImageButton(Icon.pencilSmall, () -> {
            ui.showTextInput("$block." + name + ".editdelay", "", (int) Math.ceil(Math.log(maxDelay)), entity.signals.length + "", true, result -> {
                tile.configure(Integer.parseInt(result));
            });
            control.input.frag.config.hideConfig();
        }).size(40f);
    }

    @Override
    public void configured(Tile tile, Player player, int value){
        super.configured(tile, player, value);
        if(!(tile.block() instanceof DelayBlock)) return;
        DelayBlockEntity entity = tile.entity();
        entity.signals = new int[Mathf.clamp(value, 0, maxDelay)];
        Arrays.fill(entity.signals, 0);
        entity.pointer = 0;
    }

    public class DelayBlockEntity extends LogicEntity{
        public int[] signals = new int[0];
        public int pointer = 0;

        @Override
        public int config(){
            return signals.length;
        }

        @Override
        public void write(DataOutput stream) throws IOException{
            super.write(stream);
            stream.writeInt(pointer);
            stream.writeInt(signals.length);
            for(int i : signals){
                stream.writeInt(i);
            }
        }

        @Override
        public void read(DataInput stream, byte revision) throws IOException{
            super.read(stream, revision);
            pointer = stream.readInt();
            signals = new int[stream.readInt()];
            for(int i = 0; i < signals.length; i++){
                signals[i] = stream.readInt();
            }
        }
    }
}
