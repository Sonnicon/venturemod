package sonnicon.venture.world.blocks.logic;

import io.anuke.arc.Core;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.math.geom.Geometry;
import io.anuke.arc.math.geom.Vector2;
import io.anuke.arc.scene.ui.ButtonGroup;
import io.anuke.arc.scene.ui.ImageButton;
import io.anuke.arc.scene.ui.layout.Table;
import io.anuke.arc.util.ArcAnnotate;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.entities.traits.TargetTrait;
import io.anuke.mindustry.entities.type.Player;
import io.anuke.mindustry.game.Team;
import io.anuke.mindustry.gen.Icon;
import io.anuke.mindustry.graphics.Drawf;
import io.anuke.mindustry.graphics.Pal;
import io.anuke.mindustry.ui.Styles;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.defense.turrets.Turret;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static sonnicon.venture.Venture.MOD_NAME;

public class TurretController extends ModLogicBlock{
    protected TurretTarget turretTarget = new TurretTarget();

    public TurretController(String name){
        super(name);
        configurable = true;
        entityType = TurretControllerEntity::new;
    }

    @Override
    public void update(Tile tile){
        super.update(tile);
        TurretControllerEntity entity = tile.entity();
        Tile facing = tile.front();
        if(facing != null && facing.entity != null && facing.block() instanceof Turret){
            Turret.TurretEntity facingentity = facing.entity();
            Turret facingblock = (Turret) facing.block();
            facingentity.control(false);
            if(Float.isNaN(facingentity.rotation)){
                facingentity.rotation = 0;
            }
            try{
                switch(entity.mode){
                    case(0):{
                        if(facingentity.rotation == entity.nextSignal % 360) break;
                        if(entity.turnToTarget == null){
                            entity.turnToTarget = Turret.class.getDeclaredMethod("turnToTarget", Tile.class, float.class);
                            entity.turnToTarget.setAccessible(true);
                        }
                        entity.turnToTarget.invoke(facingblock, facing, entity.nextSignal % 360);
                        break;
                    }
                    case(1):{
                        if(entity.nextSignal == 0 || !facingblock.hasAmmo(facing)) break;
                        if(entity.updateShooting == null){
                            entity.updateShooting = Turret.class.getDeclaredMethod("updateShooting", Tile.class);
                            entity.updateShooting.setAccessible(true);
                        }
                        //hack to possibly fix some turrets?
                        if(facingentity.target == null){
                            facingentity.target = turretTarget;
                            if(entity.range == null){
                                entity.range = Turret.class.getDeclaredField("range");
                                entity.range.setAccessible(true);
                            }
                            //not worth doing circles
                            turretTarget.setPos(facing.worldx() + (float)entity.range.get(facingblock), facing.worldx() + (float)entity.range.get(facingblock));
                        }else if(facingentity.target == turretTarget){
                            turretTarget.setPos(facing.worldx() + (float)entity.range.get(facingblock), facing.worldx() + (float)entity.range.get(facingblock));
                        }

                        try{
                            entity.updateShooting.invoke(facingblock, facing);
                        }catch(NullPointerException ex){
                            ex.printStackTrace();
                            Vars.ui.chatfrag.addMessage("$block." + name + ".notsupported", "[#bb99ff][[Venture][]");
                            tile.entity().kill();
                        }
                        break;
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
                System.exit(0);
            }
        }
    }

    @Override
    public int signal(Tile tile){
        return sback(tile);
    }

    @Override
    public void onProximityUpdate(Tile tile){
        super.onProximityUpdate(tile);
        if(tile.front().block() instanceof Turret) return;
        TurretControllerEntity entity = tile.entity();
        entity.updateShooting = null;
        entity.turnToTarget = null;
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
    public void buildTable(Tile tile, Table table){
        TurretControllerEntity entity = tile.entity();
        ButtonGroup<ImageButton> group = new ButtonGroup<>();
        Table buttons = new Table();
        buttons.addImageButton(Core.atlas.drawable(MOD_NAME + "icon-turret-rotate"), Styles.clearToggleTransi, () -> tile.configure(0)).size(44).group(group).update(b -> b.setChecked(entity.mode == 0));
        buttons.addImageButton(Core.atlas.drawable(MOD_NAME + "icon-turret-fire"), Styles.clearToggleTransi, () -> tile.configure(1)).size(44).group(group).update(b -> b.setChecked(entity.mode == 1));
        table.add(buttons);
    }

    @Override
    public void configured(Tile tile, @ArcAnnotate.Nullable Player player, int value){
        TurretControllerEntity entity = tile.entity();
        entity.mode = Mathf.clamp(value, 0, 1);
    }

    @Override
    public void drawSelect(Tile tile){
        super.drawSelect(tile);
        Tile facing = tile.front();
        if (facing != null && facing.entity != null && facing.block().controllable){
            Drawf.selected(facing.x, facing.y, facing.block(), Pal.accent);
        }else{
            Draw.color(Pal.remove);
            Draw.rect(Icon.cancelSmall.getRegion(), tile.drawx() + (float)(Geometry.d4(tile.rotation()).x * 8), tile.drawy() + (float)(Geometry.d4(tile.rotation()).y * 8));
            Draw.color();
        }
    }

    class TurretControllerEntity extends LogicEntity{
        public int mode;
        Method turnToTarget, updateShooting;
        Field range;

        @Override
        public int config(){
            return mode;
        }

        @Override
        public void write(DataOutput stream) throws IOException{
            super.write(stream);
            stream.writeInt(mode);
        }

        @Override
        public void read(DataInput stream, byte revision) throws IOException{
            super.read(stream, revision);
            mode = stream.readInt();
        }
    }

    public class TurretTarget implements TargetTrait{
        private float x, y;

        @Override
        public boolean isDead(){
            return false;
        }

        @Override
        public Team getTeam(){
            return null;
        }

        @Override
        public Vector2 velocity(){
            return Vector2.ZERO;
        }

        @Override
        public void setX(float v){
            x = v;
        }

        @Override
        public void setY(float v){
            y = v;
        }

        public void setPos(float x, float y){
            this.x = x;
            this.y = y;
        }

        @Override
        public float getX(){
            return x;
        }

        @Override
        public float getY(){
            return y;
        }
    }
}
