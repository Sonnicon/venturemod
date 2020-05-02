package sonnicon.venture.world.blocks.logic;

import io.anuke.arc.scene.ui.ImageButton;
import io.anuke.arc.scene.ui.layout.Table;
import io.anuke.mindustry.entities.type.Player;
import io.anuke.mindustry.gen.Tex;
import io.anuke.mindustry.ui.Styles;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.logic.LogicBlock;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SequencerBlock extends ModLogicBlock{
    public int settings = 16;

    public SequencerBlock(String name){
        super(name);
        configurable = true;
        entityType = SequencerEntity::new;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        SequencerEntity entity = tile.entity();
        if(entity.signals.length == 0) return;
        if(tile.back().block() instanceof LogicBlock && ((LogicBlock) tile.back().block()).sback(tile) != 0) return;
        entity.pointer = ++entity.pointer % entity.signals.length;
    }

    @Override
    public int signal(Tile tile){
        SequencerEntity entity = tile.entity();
        if(entity.signals.length == 0) return 0;
        return entity.signals[entity.pointer] ? 1 : 0;
    }

    @Override
    public void buildTable(Tile tile, Table table){
        super.buildTable(tile, table);
        Table t = new Table();
        for(int i = 0; i < settings; i++){
            int finalI = i;
            ImageButton button = t.addImageButton(Tex.whiteui, Styles.clearTogglei, 34, () -> {
                if(!(tile.block() instanceof SequencerBlock)) return;
                SequencerEntity entity = tile.entity();
                tile.configure(entity.config() ^ (1 << finalI));
            }).size(48).get();
            button.update(() -> button.setChecked(isChecked(tile, finalI)));
            if(i % 4 == 3){
                t.row();
            }
        }
        table.add(t);
    }

    @Override
    public void configured(Tile tile, Player player, int value){
        if(!(tile.block() instanceof SequencerBlock)) return;
        SequencerEntity entity = tile.entity();
        for (int i = 0; i < entity.signals.length; ++i) {
            entity.signals[i] = (value & (1 << i)) != 0;
        }
    }

    protected boolean isChecked(Tile tile, int index){
        if(!(tile.block() instanceof SequencerBlock)) return false;
        SequencerEntity entity = tile.entity();
        if(entity.signals.length == 0) return false;
        return entity.signals[index];
    }

    public class SequencerEntity extends LogicEntity{
        public boolean[] signals = new boolean[settings];
        public int pointer = 0;

        @Override
        public int config(){
            int result = 0;
            for(int i = 0; i < signals.length; i++)
                if(signals[i]) result |= (1 << i);
            return result;
        }

        @Override
        public void write(DataOutput stream) throws IOException{
            super.write(stream);
            stream.writeInt(pointer);
            stream.writeInt(signals.length);
            for(boolean b : signals){
                stream.writeBoolean(b);
            }
        }

        @Override
        public void read(DataInput stream, byte revision) throws IOException{
            super.read(stream, revision);
            pointer = stream.readInt();
            signals = new boolean[stream.readInt()];
            for(int i = 0; i < signals.length; i++){
                signals[i] = stream.readBoolean();
            }
        }
    }
}
