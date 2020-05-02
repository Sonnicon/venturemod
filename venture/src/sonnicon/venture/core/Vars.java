package sonnicon.venture.core;

import io.anuke.arc.graphics.Color;
import sonnicon.venture.net.PlayerChairPacketHandler;
import sonnicon.venture.ui.ChannelPaletteDialog;

public class Vars{
    public static ChannelPaletteDialog channelPaletteDialog;

    public static final Color[] channelColours = {Color.red, Color.green, Color.blue, Color.yellow, Color.purple, Color.brown, Color.black, Color.white};

    public static void init(){
        if(!io.anuke.mindustry.Vars.headless) channelPaletteDialog = new ChannelPaletteDialog();

        new PlayerChairPacketHandler();
    }
}
