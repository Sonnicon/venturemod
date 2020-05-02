package sonnicon.venture.ui;

import io.anuke.arc.func.Cons;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.input.KeyCode;
import io.anuke.arc.scene.ui.Dialog;
import io.anuke.arc.scene.ui.ImageButton;
import io.anuke.arc.scene.ui.layout.Table;
import io.anuke.mindustry.gen.Tex;
import io.anuke.mindustry.ui.Styles;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.world.blocks.distribution.DuctIO;

import static sonnicon.venture.core.Vars.channelColours;

public class ChannelPaletteDialog extends Dialog{
    private Cons<Integer> cons;
    public Tile tile;

    public ChannelPaletteDialog(){
        super("");
        build();
    }

    private void build(){
        Table table = new Table();
        cont.add(table);

        for(int i = 0; i < channelColours.length; i++){
            Color color = channelColours[i];

            int finalI = i;
            ImageButton button = table.addImageButton(Tex.whiteui, Styles.clearTogglei, 34, () -> {
                cons.get(finalI);
                hide();
            }).size(48).get();
            button.setChecked(tile != null && ((DuctIO.DuctIOEntity) tile.entity()).setting == i);
            button.getStyle().imageUpColor = color;

            if(i % 4 == 3){
                table.row();
            }
        }

        keyDown(key -> {
            if(key == KeyCode.ESCAPE || key == KeyCode.BACK)
                hide();
        });

    }

    public void show(Tile tile, Cons<Integer> cons){
        this.cons = cons;
        this.tile = tile;
        show();
    }

    @Override
    public void hide(){
        super.hide();
        tile = null;
    }
}
