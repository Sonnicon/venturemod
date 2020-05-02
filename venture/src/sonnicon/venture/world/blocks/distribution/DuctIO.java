package sonnicon.venture.world.blocks.distribution;

import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.Fill;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.scene.ui.Button;
import io.anuke.arc.scene.ui.ImageButton;
import io.anuke.arc.scene.ui.layout.Table;
import io.anuke.mindustry.entities.type.Player;
import io.anuke.mindustry.gen.Tex;
import io.anuke.mindustry.ui.Styles;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.core.Vars;
import sonnicon.venture.types.DuctIOType;
import sonnicon.venture.ui.ChannelPaletteDialog;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static io.anuke.mindustry.Vars.*;

public class DuctIO extends DuctType{
    public DuctIOType type = DuctIOType.none;
    public DuctIO(String name){
        super(name);
        update = true;
        solid = true;
        configurable = true;
        controllable = true;
        entityType = DuctIOEntity::new;
    }

    @Override
    public void buildTable(Tile tile, Table table){
        super.buildTable(tile, table);
        DuctIOEntity entity = tile.entity();
        Table all = new Table(Styles.black);
        Table t = new Table();
        ImageButton channelButton = t.addImageButton(Tex.whiteui, Styles.clearFulli, 40, () -> {
            if(!(tile.block() instanceof DuctIO)) return;
            new ChannelPaletteDialog().show(tile, color -> {
                if(!(tile.block() instanceof DuctIO)) return;
                ((DuctIO)tile.block()).setChannel(tile, color);
            });
        }).size(54f).get();
        channelButton.update(() -> channelButton.getStyle().imageUpColor = Vars.channelColours[entity.setting]);

        Table buttons = new Table();
        buttons.columnDefaults(2);
        Button inButton = buttons.addButton("I", Styles.togglet, ()  -> {
            if(!(tile.block() instanceof DuctIO)) return;
            setInput(tile, true);
        }).height(32f).fillX().get();
        Button outButton = buttons.addButton("O", Styles.togglet, () -> {
            if(!(tile.block() instanceof DuctIO)) return;
            setInput(tile, false);
        }).height(32f).fillX().get();

        inButton.update(() -> inButton.setChecked(entity.input));
        outButton.update(() -> outButton.setChecked(!entity.input));

        all.add(t);
        all.row();
        all.add(buttons);
        table.add(all);
    }

    public void setChannel(Tile tile, int color){
        DuctIOEntity entity = tile.entity();
        if(color == entity.setting) return;
        entity.setting = color;
        tile.configure(entity.config());
        control.input.frag.config.hideConfig();
    }

    public void setInput(Tile tile, boolean input){
        DuctIOEntity entity = tile.entity();
        if(input == entity.input) return;
        entity.input = input;
        tile.configure(entity.config());
        control.input.frag.config.hideConfig();
    }

    @Override
    public void configured(Tile tile, Player player, int value){
        if(!(tile.block() instanceof DuctIO)) return;
        super.configured(tile, player, value);
        DuctIOEntity entity = tile.entity();
        entity.readConfig(value);
    }

    @Override
    public void drawSelect(Tile tile){
        super.drawSelect(tile);
        DuctIOEntity entity = tile.entity();
        Draw.color(Vars.channelColours[entity.setting]);
        Fill.rect(tile.drawx(), tile.drawy(), 2, 2);
        Draw.color();
    }

    public class DuctIOEntity extends DuctTypeEntity{
        public int setting = 0;
        public boolean input = false;

        @Override
        public int config(){
            return (setting + 1) * (input ? 1 : -1);
        }

        public void readConfig(int config){
            setting = Mathf.clamp(Math.abs(config), 1, Vars.channelColours.length - 1) - 1;
            input = config >= 0;
        }

        @Override
        public void write(DataOutput stream) throws IOException{
            super.write(stream);
            stream.writeInt(config());
        }

        @Override
        public void read(DataInput stream, byte revision) throws IOException{
            super.read(stream, revision);
            readConfig(stream.readInt());
        }
    }
}
