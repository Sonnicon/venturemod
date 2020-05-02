package sonnicon.venture.world.blocks.transportation;

import io.anuke.arc.Core;
import io.anuke.arc.collection.ObjectMap;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.scene.ui.TextField;
import io.anuke.arc.util.ArcAnnotate;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.entities.type.Player;
import io.anuke.mindustry.input.Binding;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.logic.AcceptorLogicBlock;
import sonnicon.venture.net.PlayerChairPacketHandler;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static io.anuke.arc.Core.scene;
import static io.anuke.mindustry.Vars.ui;

public class PlayerChair extends AcceptorLogicBlock{
    public static ObjectMap<Player, PlayerChairEntity> playerchairs = new ObjectMap<>();
    static boolean wrote = false;

    public PlayerChair(String name){
        super(name);
        doOutput = true;
        rotate = false;
        consumesTap = true;
        entityType = PlayerChairEntity::new;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        wrote = false;
        PlayerChairEntity entity = tile.entity();
        if(entity.player != null){
            if(entity.player.isDead() || (!entity.player.isLocal && !Vars.playerGroup.all().contains(entity.player))){
                dismount(entity);
                return;
            }else{
                entity.player.velocity().set(0f, 0f);
                entity.player.set(tile.getX(), tile.getY());
            }
            if(entity.player.isLocal && entity.nextSignal != entity.lastSignal) PlayerChairPacketHandler.onTileEntityConfigure(entity.player, entity, entity.nextSignal);
        }else{
            entity.nextSignal = 0;
        }
    }

    @Override
    public void draw(Tile tile){
        Draw.rect(region, tile.drawx(), tile.drawy());
    }

    @Override
    public int signal(Tile tile){
        PlayerChairEntity entity = tile.entity();
        if(entity.player == null) return 0;
        if(!entity.player.isLocal) return entity.nextSignal;
        if(scene.getKeyboardFocus() instanceof TextField || ui.chatfrag.chatOpen()) return 1;
        int result = 1;
        result |= (Core.input.axis(Binding.move_x) != 0f ? (Core.input.axis(Binding.move_x) > 0f ? 2 : 4) : 0);
        result |= (Core.input.axis(Binding.move_y) != 0f ? (Core.input.axis(Binding.move_y) > 0f ? 8 : 16) : 0);
        result |= (Core.input.keyDown(Binding.dash) ? 1 : 0) * 32;
        result |= (Core.input.keyDown(Binding.mouse_move) ? 1 : 0) * 64;
        return result;
    }

    @Override
    public void tapped(Tile tile, Player player){
        if(player.isLocal && checkValidTap(tile, player)){
            PlayerChairPacketHandler.onTileEntityConfigure(player, (PlayerChairEntity) tile.entity, 0);
        }
    }


    public static void playerChairConfigured(PlayerChairEntity entity, @ArcAnnotate.Nullable Player player, int value){
        if(player == null) return;
        entity = playerchairs.get(player, entity);
        if(entity == null) return;
        if(entity.player == null){
            mount(entity, player);
        }else if(entity.player != player){
            dismount(entity);
            mount(entity, player);
        }else if(value == 0){
            dismount(entity);
        }else{
            entity.nextSignal = value;
        }
    }

    public static void dismount(PlayerChairEntity entity){
        if(entity.player != null && playerchairs.containsKey(entity.player)) playerchairs.remove(entity.player);
        entity.player = null;
        entity.nextSignal = 0;
    }

    public static void mount(PlayerChairEntity entity, Player player){
        if(player == null) return;
        playerchairs.put(player, entity);
        entity.player = player;
        entity.nextSignal = 1;
    }

    protected boolean checkValidTap(Tile tile, Player player){
        return !player.isDead() && tile.interactable(player.getTeam()) && Math.abs(player.getX() - tile.getX()) <= 32 && Math.abs(player.getY() - tile.getY()) <= 32;
    }

    public class PlayerChairEntity extends LogicEntity{
        Player player;

        @Override
        public void write(DataOutput stream) throws IOException{
            super.write(stream);
            stream.writeInt(player != null ? player.id : -1);

            //todo move to world sync packet
            if(!wrote){
                stream.writeInt(playerchairs.size);
                for(ObjectMap.Entry<Player, PlayerChairEntity> entry : playerchairs){
                    stream.writeInt(entry.key.getID());
                    stream.writeInt(entry.value.tile.pos());
                }
            }
        }

        @Override
        public void read(DataInput stream, byte revision) throws IOException{
            super.read(stream, revision);
            player = Vars.playerGroup.getByID(stream.readInt());
            mount(this, player);

            //todo move to world sync packet
            playerchairs.clear();
            if(!wrote){
                int len = stream.readInt();
                for(int i = 0; i < len; i++){
                    Player p = Vars.playerGroup.getByID(stream.readInt());
                    if(p == null){
                        stream.skipBytes(Integer.SIZE / 8);
                    }else{
                        playerchairs.put(p, Vars.world.tile(stream.readInt()).entity());
                    }
                }
            }
        }
    }
}
