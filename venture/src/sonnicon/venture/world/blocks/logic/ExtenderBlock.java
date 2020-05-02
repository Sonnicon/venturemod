package sonnicon.venture.world.blocks.logic;

import io.anuke.arc.scene.ui.layout.Table;
import io.anuke.mindustry.entities.type.Player;
import io.anuke.mindustry.gen.Icon;
import io.anuke.mindustry.world.Tile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static io.anuke.mindustry.Vars.control;
import static io.anuke.mindustry.Vars.ui;

public class ExtenderBlock extends ModLogicBlock{
    public ExtenderBlock(String name){
        super(name);
        configurable = true;
        entityType = ExtenderBlockEntity::new;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        ExtenderBlockEntity entity = tile.entity();
        if(sback(tile) > 0){
            entity.extensionTimer = entity.setting;
        }else if(entity.extensionTimer-- <= 0){
            entity.nextSignal = 0;
        }
    }

    @Override
    public int signal(Tile tile){
        ExtenderBlockEntity entity = tile.entity();
        return entity.nextSignal | sback(tile);
    }

    @Override
    public void buildTable(Tile tile, Table table){
        super.buildTable(tile, table);
        ExtenderBlockEntity entity = tile.entity();
        table.addImageButton(Icon.pencilSmall, () -> {
            ui.showTextInput("$block." + name + ".editlength", "", 8, entity.setting + "", true, result -> {
                tile.configure(Integer.parseInt(result));
            });
            control.input.frag.config.hideConfig();
        }).size(40f);
    }

    @Override
    public void configured(Tile tile, Player player, int value){
        super.configured(tile, player, value);
        if(!(tile.block() instanceof ExtenderBlock)) return;
        ExtenderBlockEntity entity = tile.entity();
        entity.setting = value;
        entity.extensionTimer = 0;
    }

    public class ExtenderBlockEntity extends LogicEntity{
        public int setting = 0;
        public int extensionTimer = 0;

        @Override
        public int config(){
            return setting;
        }

        @Override
        public void write(DataOutput stream) throws IOException{
            super.write(stream);
            stream.writeInt(setting);
            stream.writeInt(extensionTimer);
        }

        @Override
        public void read(DataInput stream, byte revision) throws IOException{
            super.read(stream, revision);
            setting = stream.readInt();
            extensionTimer = stream.readInt();
        }
    }
}
