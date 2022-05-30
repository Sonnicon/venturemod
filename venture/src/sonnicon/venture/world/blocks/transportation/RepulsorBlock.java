package sonnicon.venture.world.blocks.transportation;

import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.util.Structs;
import io.anuke.mindustry.content.Blocks;
import io.anuke.mindustry.entities.type.TileEntity;
import io.anuke.mindustry.game.Team;
import io.anuke.mindustry.graphics.Pal;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Pos;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.BlockPart;
import io.anuke.mindustry.world.blocks.logic.LogicBlock;
import io.anuke.mindustry.world.blocks.logic.NodeLogicBlock;
import io.anuke.mindustry.world.blocks.power.PowerNode;
import sonnicon.venture.types.IMoveModifiers;
import sonnicon.venture.types.IMoved;
import sonnicon.venture.util.TileUtil;
import sonnicon.venture.world.blocks.logic.ModLogicBlock;

import java.lang.reflect.Field;
import java.util.HashSet;

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
    protected HashSet<Tile> tilesSearch = new HashSet<>();

    public RepulsorBlock(String name){
        super(name);
        doOutput = false;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        LogicEntity entity = tile.entity();
        if(entity.nextSignal > 0 && entity.lastSignal == 0 && tile.front() != null){
            origin = tile;
            if(searchMoveTiles(tile.front(), tile.rotation())){
                moving = true;
                tilesSearch.stream().sorted(
                                // Order tiles to move front-to-back based on direction
                                Structs.comparing(t -> (tile.rotation() % 2 == 0 ? t.x : t.y) * (tile.rotation() > 1 ? 1 : -1)))
                        .forEachOrdered(t -> move(t, tile.rotation()));
                moving = false;
            }
        }
    }

    protected boolean searchMoveTiles(Tile startTile, int direction){
        tilesSearch.clear();

        HashSet<Tile> currentSet = new HashSet<>(), queueSet = new HashSet<>();
        TileUtil.getMultiblockTiles(startTile, currentSet::add);

        // Keep searching until we found everything
        while(currentSet.size() > 0){
            for(Tile tile : currentSet){
                // don't move unmovable tile
                if(!canMove(tile, direction)) return false;

                Tile destination = tile.getNearby(direction);
                // Don't move off the map
                if(destination == null) return false;
                // Queue block at destination to move away
                if(destination.block() != null && destination.block() != Blocks.air){
                    // Queue multiblocks
                    if(destination.link().block().isMultiblock()){
                        TileUtil.getMultiblockTiles(destination, queueSet::add);
                    }else{
                        queueSet.add(destination);
                    }
                }
                // Queue other blocks brought along
                if(tile.link().block() instanceof IMoveModifiers){
                    for(int i = 0; i <= 3; i++){
                        // We already looked this way
                        if(i == direction) continue;

                        if(((IMoveModifiers) tile.link().block()).bringWhenMoved(tile, i)){
                            Tile other = tile.getNearby(i);
                            if(other == null || other.block() == Blocks.air) continue;
                            if(other.link().block().isMultiblock()){
                                TileUtil.getMultiblockTiles(other, queueSet::add);
                            }else{
                                queueSet.add(other);
                            }
                        }
                    }
                }
            }
            // Move tiles between sets and clear queue
            tilesSearch.addAll(currentSet);
            queueSet.removeAll(tilesSearch);
            HashSet<Tile> temp = currentSet;
            currentSet = queueSet;
            queueSet = temp;
            queueSet.clear();
        }
        return true;
    }

    protected boolean canMove(Tile tile, int direction){
        // Can't move null, ourselves, or tiles that don't want to move
        return tile != null && tile != origin &&
                ((tile.block() != null && tile.block() instanceof IMoveModifiers) ?
                        ((IMoveModifiers) tile.block()).shouldMove(tile, direction) :
                        tile.breakable());
    }

    protected void move(Tile tile, int direction){
        getIntermediates(tile);
        if(intermediateBlock instanceof IMoved) ((IMoved) intermediateBlock).beforeMoved(tile, direction);
        tile.setBlock(Blocks.air);
        Tile newPos = tile.getNearby(direction);
        setIntermediates(newPos);
        if(intermediateBlock instanceof IMoved) ((IMoved) intermediateBlock).afterMoved(newPos, direction);
        if(intermediateBlock.hasPower && !(intermediateBlock instanceof BlockPart)){
            if(intermediateBlock instanceof PowerNode){
                for(int link : intermediateEntity.power.links.items){
                    world.tile(link).configure(tile.pos());
                    world.tile(link).configure(newPos.pos());
                }
            }
            intermediateEntity.power.graph.reflow(newPos);
        }
        // Not ideal, but better than replacing the content
        if(intermediateBlock instanceof NodeLogicBlock){
            // Shift target node with source node
            NodeLogicBlock.NodeLogicEntity entity = newPos.entity();
            if(entity.link != Pos.invalid){
                Tile o = world.tile(entity.link);
                if(o != null){
                    o = o.getNearby(direction);
                    if(o != null){
                        entity.link = o.pos();
                    }
                }
            }
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
        tile.entity.tile = tile;
        tile.entity.x = tile.drawx();
        tile.entity.y = tile.drawy();
        tile.entity.add();
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
