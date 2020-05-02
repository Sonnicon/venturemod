package sonnicon.venture.net;

import io.anuke.arc.util.pooling.Pools;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.entities.type.Player;
import io.anuke.mindustry.gen.Call;
import io.anuke.mindustry.io.TypeIO;
import io.anuke.mindustry.net.Net;
import io.anuke.mindustry.net.Packets;
import io.anuke.mindustry.world.Tile;
import sonnicon.sonnicore.net.ModInvokePacketHandler;
import sonnicon.venture.world.blocks.transportation.PlayerChair;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;

public class PlayerChairPacketHandler extends ModInvokePacketHandler{
    public static ByteBuffer TEMP_BUFFER;
    public static byte typeid;

    public PlayerChairPacketHandler(){
        super();
        typeid = id;
        try{
            if(TEMP_BUFFER == null){
                Field field = Call.class.getDeclaredField("TEMP_BUFFER");
                field.setAccessible(true);
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                TEMP_BUFFER = (ByteBuffer) field.get(null);
            }
        }catch(NoSuchFieldException | IllegalAccessException ex){
            ex.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void readClient(ByteBuffer buffer, int id){
        try{
            Player player = TypeIO.readPlayer(buffer);
            int pos = buffer.getInt();
            Tile tile = Vars.world.tile(pos);
            int value = buffer.getInt();
            PlayerChair.playerChairConfigured(tile.block() instanceof PlayerChair ? (PlayerChair.PlayerChairEntity) tile.entity : null, player, value);
        }catch(Exception ex){
            throw new java.lang.RuntimeException("Failed to to read remote Venture method 'TileEntityConfigure:configured'!", ex);
        }
    }

    @Override
    public void readServer(ByteBuffer buffer, int id, Player player){
        try{
            Tile tile = TypeIO.readTile(buffer);
            int value = buffer.getInt();
            PlayerChair.playerChairConfigured((tile != null && tile.block() instanceof PlayerChair) ? tile.entity() : null, player, value);
            forward(player, tile, value);
        }catch(Exception ex){
            throw new java.lang.RuntimeException("Failed to to read remote Venture method 'PlayerChair:playerChairConfigured'!", ex);
        }
    }

    static synchronized void forward(Player player, Tile tile, int value) {
        if(Vars.net.server() || Vars.net.client()) {
            Packets.InvokePacket packet = io.anuke.arc.util.pooling.Pools.obtain(Packets.InvokePacket.class, Packets.InvokePacket::new);
            packet.writeBuffer = TEMP_BUFFER;
            packet.priority = (byte)1;
            packet.type = typeid;
            TEMP_BUFFER.position(0);
            if(Vars.net.server()){
                TypeIO.writePlayer(TEMP_BUFFER, player);
            }
            TypeIO.writeTile(TEMP_BUFFER, tile);
            TEMP_BUFFER.putInt(value);
            packet.writeLength = TEMP_BUFFER.position();
            Vars.net.send(packet, Net.SendMode.tcp);
        }
    }

    public static synchronized void onTileEntityConfigure(Player player, PlayerChair.PlayerChairEntity entity, int value) {
        if(entity == null || entity.tile == null) return;
        if(Vars.net.server() || Vars.net.client()) {
            Packets.InvokePacket packet = Pools.obtain(Packets.InvokePacket.class, Packets.InvokePacket::new);
            packet.writeBuffer = TEMP_BUFFER;
            packet.priority = (byte)1;
            packet.type = typeid;
            TEMP_BUFFER.position(0);
            if(io.anuke.mindustry.Vars.net.server()) {
                TypeIO.writePlayer(TEMP_BUFFER, player);
            }
            TypeIO.writeTile(TEMP_BUFFER, entity.tile);
            TEMP_BUFFER.putInt(value);
            packet.writeLength = TEMP_BUFFER.position();
            Vars.net.send(packet, Net.SendMode.tcp);
        }else{
            PlayerChair.playerChairConfigured(entity, player, value);
        }
    }
}
