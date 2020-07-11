package sonnicon.venture.world.blocks.transportation;

import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.util.Structs;
import io.anuke.mindustry.content.Blocks;
import io.anuke.mindustry.entities.type.TileEntity;
import io.anuke.mindustry.game.Team;
import io.anuke.mindustry.graphics.Pal;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.BlockPart;
import io.anuke.mindustry.world.blocks.logic.LogicBlock;
import io.anuke.mindustry.world.blocks.power.PowerNode;
import sonnicon.venture.types.OnMove;
import sonnicon.venture.types.SometimesMove;
import sonnicon.venture.util.TileUtil;
import sonnicon.venture.world.blocks.logic.ModLogicBlock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.anuke.mindustry.Vars.world;
import static sonnicon.venture.Venture.MOD_NAME;

public class RepulsorBlock extends ModLogicBlock{
    public static boolean moving = false;
    protected Block intermediateBlock;
    protected TileEntity intermediateEntity;
    protected byte intermediateRotation;
    protected Team intermediateTeam;

    protected Field blockField;

    protected Tile origin;
    protected List<Tile> tilesDone = new ArrayList<>(), tomove = new ArrayList<>();

    public RepulsorBlock(String name){
        super(name);
        doOutput = false;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        LogicEntity entity = tile.entity();
        if(entity.nextSignal > 0 && entity.lastSignal == 0 && tile.front() != null && tile.front().block() != Blocks.air){
            origin = tile;
            tilesDone.clear();
            tomove.clear();
            if(canMove(tile.front(), tile.rotation()) && !tomove.contains(tile)){
                moving = true;
                //failsafe
                Set<Tile> set = new HashSet<>(tomove);
                tomove.clear();
                tomove.addAll(set);
                tomove.sort(Structs.comparing(t -> (tile.rotation() % 2 == 0 ? t.x : t.y) * (tile.rotation() > 1 ? 1 : -1)));
                tomove.iterator().forEachRemaining(t -> move(t, tile.rotation()));
                moving = false;
            }
        }
    }

    protected boolean canMove(Tile tile, int direction){
        if(tile == null || tile == origin || (tile.block() instanceof SometimesMove && !((SometimesMove) tile.block()).shouldMove(tile, direction))) return false;
        if(tile.block() == Blocks.air || (tile.block() instanceof BracketBlock && tilesDone.contains(tile)) || (tile.block().isMultiblock() && tilesDone.contains(tile))) return true;
        if(tile.link().block().size > 1 || tile.block() instanceof BracketBlock){
            ArrayList<Tile> linked = new ArrayList<Tile>();
            TileUtil.getLinkedTiles(tile, linked, direction);
            tilesDone.addAll(linked);
            for(Tile t : linked){
                Tile tother = t.getNearbyLink(direction);
                if(tother == null || ((t.block() instanceof BracketBlock ? !(tother.block() instanceof BracketBlock) : tother.link() != t.link()) && !canMove(tother, direction))) return false;
            }
            tomove.addAll(linked);
            return true;
        }
        Tile other = tile.getNearbyLink(direction);
        if(other == null) return false;
        if(movable(tile) && (other.block() == Blocks.air || canMove(other, direction))){
            tomove.add(tile);
            return true;
        }
        return false;
    }

    protected boolean movable(Tile tile){
        return tile.breakable();
    }

    protected void move(Tile tile, int direction){
        getIntermediates(tile);
        if(intermediateBlock instanceof OnMove) ((OnMove) intermediateBlock).beforeMoved(tile, direction);
        tile.setBlock(Blocks.air);
        Tile newPos = tile.getNearby(direction);
        setIntermediates(newPos);
        if(intermediateBlock instanceof OnMove) ((OnMove) intermediateBlock).afterMoved(newPos, direction);
        if(intermediateBlock.hasPower && !(intermediateBlock instanceof BlockPart)){
            if(intermediateBlock instanceof PowerNode){
                for(int link : intermediateEntity.power.links.items){
                    world.tile(link).configure(tile.pos());
                    world.tile(link).configure(newPos.pos());
                }
            }
            intermediateEntity.power.graph.reflow(newPos);
        }
    }

    protected void getIntermediates(Tile tile){
        intermediateBlock = tile.block();
        if(intermediateBlock.hasEntity()) intermediateEntity = tile.entity;
        intermediateRotation = tile.rotation();
        intermediateTeam = tile.getTeam();
    }

    protected void setIntermediates(Tile tile){
        if(intermediateBlock.hasEntity()){
            try{
                if(blockField == null){
                    blockField = Tile.class.getDeclaredField("block");
                    blockField.setAccessible(true);
                }
                blockField.set(tile, intermediateBlock);
                tile.entity = intermediateEntity;
                tile.rotation(intermediateRotation);
                tile.setTeam(intermediateTeam);
                blockChanged(tile);
            }catch(Exception ex){
                ex.printStackTrace();
                System.exit(0);
            }
        }else{
            tile.setBlock(intermediateBlock, intermediateTeam, intermediateRotation);
        }
    }

    protected void blockChanged(Tile tile){
        tile.entity.init(tile, true);
        tile.entity.updateProximity();
        tile.updateOcclusion();
        world.notifyChanged(tile);


    }

    @Override
    public void draw(Tile tile){
        LogicBlock.LogicEntity entity = tile.entity();
        Draw.rect(MOD_NAME + "interactor-base", tile.drawx(), tile.drawy(), this.rotate ? (float)(tile.rotation() * 90) : 0.0F);
        Draw.color(entity.nextSignal > 0 ? Pal.accent : Color.white);
        Draw.rect(this.region, tile.drawx(), tile.drawy(), this.rotate ? (float)(tile.rotation() * 90) : 0.0F);
        Draw.color();
    }

    @Override
    public int signal(Tile tile){
        if(tile.back().block() instanceof LogicBlock)
            return ((LogicBlock) tile.back().block()).sback(tile);
        return 0;
    }
}
