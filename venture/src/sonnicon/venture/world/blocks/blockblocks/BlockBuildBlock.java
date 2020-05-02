package sonnicon.venture.world.blocks.blockblocks;

import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.math.geom.Geometry;
import io.anuke.arc.math.geom.Rectangle;
import io.anuke.arc.scene.style.TextureRegionDrawable;
import io.anuke.arc.scene.ui.layout.Table;
import io.anuke.arc.util.Time;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.content.Blocks;
import io.anuke.mindustry.entities.EntityGroup;
import io.anuke.mindustry.entities.type.Player;
import io.anuke.mindustry.entities.type.Unit;
import io.anuke.mindustry.game.Team;
import io.anuke.mindustry.gen.Tex;
import io.anuke.mindustry.graphics.Pal;
import io.anuke.mindustry.type.ContentType;
import io.anuke.mindustry.type.TypeID;
import io.anuke.mindustry.type.Weapon;
import io.anuke.mindustry.ui.Cicon;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Build;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.BuildBlock;
import io.anuke.mindustry.world.blocks.logic.LogicBlock;
import sonnicon.venture.util.TileUtil;
import sonnicon.venture.world.blocks.logic.ModLogicBlock;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static io.anuke.mindustry.Vars.state;
import static sonnicon.venture.Venture.MOD_NAME;

public class BlockBuildBlock extends ModLogicBlock{
    private BlockBuildBlockUnit unit = new BlockBuildBlockUnit();

    public BlockBuildBlock(String name){
        super(name);
        configurable = true;
        consumesTap = true;
        doOutput = false;
        entityType = BlockBuildBlockEntity::new;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        BlockBuildBlockEntity entity = tile.entity();
        if(entity.setting != Blocks.air && entity.lastSignal > 0){
            if(tile.front().entity() instanceof BuildBlock.BuildEntity){
                Tile front = tile.front();
                BuildBlock.BuildEntity frontentity = front.entity();
                unit.tile = tile;
                frontentity.construct(unit, Geometry.findClosest(tile.getX(), tile.getY(), state.teams.get(tile.getTeam()).cores).entity(),1f / frontentity.buildCost * Time.delta() * state.rules.buildSpeedMultiplier, false);
            }else{
                Tile target = TileUtil.getNearbyDistance(tile, tile.rotation(), (int) Math.ceil(entity.setting.size / 2f));
                if(target == null) return;
                Build.beginPlace(tile.getTeam(), target.x, target.y, entity.setting, 0);
            }
        }
    }

    @Override
    public int signal(Tile tile){
        return tile.back() != null && tile.back().block() instanceof LogicBlock ? ((LogicBlock) tile.back().block()).getSignal(tile, tile.back()) : 0;
    }

    @Override
    public void draw(Tile tile){
        LogicEntity entity = tile.entity();
        Draw.rect(MOD_NAME + "interactor-base", tile.drawx(), tile.drawy(), this.rotate ? (float)(tile.rotation() * 90) : 0.0F);
        Draw.color(entity.nextSignal > 0 ? Pal.accent : Color.white);
        Draw.rect(this.region, tile.drawx(), tile.drawy(), this.rotate ? (float)(tile.rotation() * 90) : 0.0F);
        Draw.color();
    }

    @Override
    public void configured(Tile tile, Player player, int value){
        if(!(tile.block() instanceof BlockBuildBlock)) return;
        BlockBuildBlockEntity entity = tile.entity();
        entity.setting = Vars.content.getByID(ContentType.block, value);
        if(entity.setting == null) entity.setting = Blocks.air;
    }

    @Override
    public void buildTable(Tile tile, Table table){
        super.buildTable(tile, table);
        BlockBuildBlockEntity entity = tile.entity();
        table.background(Tex.pane);
        table.addImageButton(new TextureRegionDrawable(entity.setting.icon(Cicon.xlarge)), () -> {
            if(Vars.control.input.block == null) return;
            tile.configure(Vars.control.input.block.id);
            Vars.control.input.frag.config.hideConfig();
        });
    }

    public class BlockBuildBlockEntity extends LogicEntity{
        public Block setting = Blocks.air;

        @Override
        public int config(){
            return setting.id;
        }

        @Override
        public void write(DataOutput stream) throws IOException{
            super.write(stream);
            stream.writeShort(config());
        }

        @Override
        public void read(DataInput stream, byte revision) throws IOException{
            super.read(stream, revision);
            tile.configure(stream.readShort());
        }
    }


    private static class BlockBuildBlockUnit extends Unit{
        public Tile tile;

        public BlockBuildBlockUnit(){
            super();
        }

        @Override
        public Team getTeam(){
            return tile.getTeam();
        }

        @Override
        public TextureRegion getIconRegion(){
            return null;
        }

        @Override
        public Weapon getWeapon(){
            return null;
        }

        @Override
        public int getItemCapacity(){
            return 0;
        }

        @Override
        public float mass(){
            return 0;
        }

        @Override
        public boolean isFlying(){
            return false;
        }

        @Override
        public void draw(){

        }

        @Override
        public byte version(){
            return 0;
        }

        @Override
        public void hitbox(Rectangle rectangle){

        }

        @Override
        public void hitboxTile(Rectangle rectangle){

        }

        @Override
        public void write(DataOutput dataOutput) throws IOException{

        }

        @Override
        public void read(DataInput dataInput) throws IOException{

        }

        @Override
        public EntityGroup targetGroup(){
            return null;
        }

        @Override
        public float maxHealth(){
            return 0;
        }

        @Override
        public TypeID getTypeID(){
            return null;
        }
    }
}
