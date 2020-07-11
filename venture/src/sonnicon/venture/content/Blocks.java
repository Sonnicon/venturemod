package sonnicon.venture.content;

import io.anuke.mindustry.content.Items;
import io.anuke.mindustry.type.Category;
import io.anuke.mindustry.type.ItemStack;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import sonnicon.venture.core.Loader;
import sonnicon.venture.world.blocks.blockblocks.*;
import sonnicon.venture.world.blocks.defence.*;
import sonnicon.venture.world.blocks.distribution.*;
import sonnicon.venture.world.blocks.logic.*;
import sonnicon.venture.world.blocks.transportation.*;
import sonnicon.venture.world.blocks.units.*;
import sonnicon.sonnicore.world.BlockUtils;

import static sonnicon.venture.Venture.MOD_NAME;

public class Blocks implements Loader.Load {
    public static Block duct, ductItemIO, ductLiquidIO, ductPowerIO, ductLogicIO,
            turretControllerBlock, logicDelay, logicSequencer, logicExtender, logicPressurepad, logicEntitydetector, logicEntitypointer, logicBlockSensor, logicRotator,
            foamBlock, hardFoamBlock, foamMechPad, foamNozzle,
            repulsor, attractor, bracket, bracketAdhesive, bracketFrictionless, bracketdoor, playerChair, carrier, anchor,
            explosives,
            blockrelay, blockchest, blockbreaker, blockplacer, blocksorter, blockconfigurator, blockconstructor;

    @Override
    public void load() {
        duct = new Duct(MOD_NAME + "duct"){{
            requirements(Category.distribution, ItemStack.with(Items.silicon, 1));
        }};

        ductItemIO = new DuctItemIO(MOD_NAME + "ductitemio"){{
            requirements(Category.distribution, ItemStack.with(Items.silicon, 1));
        }};

        ductLiquidIO = new DuctLiquidIO(MOD_NAME + "ductliquidio"){{
            requirements(Category.distribution, ItemStack.with(Items.silicon, 1));
        }};

        ductPowerIO = new DuctPowerIO(MOD_NAME + "ductpowerio"){{
            requirements(Category.distribution, ItemStack.with(Items.silicon, 1));
        }};

        ductLogicIO = new DuctLogicIO(MOD_NAME + "ductlogicio"){{
            requirements(Category.distribution, ItemStack.with(Items.silicon, 1));
        }};

        turretControllerBlock = new TurretController(MOD_NAME + "turretcontroller"){{
            BlockUtils.requirements(this, Categories.logic, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        logicDelay = new DelayBlock(MOD_NAME + "logicdelay"){{
            BlockUtils.requirements(this, Categories.logic, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        logicSequencer = new SequencerBlock(MOD_NAME + "logicsequencer"){{
            BlockUtils.requirements(this, Categories.logic, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        logicExtender = new ExtenderBlock(MOD_NAME + "logicextender"){{
            BlockUtils.requirements(this, Categories.logic, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        logicPressurepad = new PressurepadBlock(MOD_NAME + "logicpressurepad"){{
            BlockUtils.requirements(this, Categories.logic, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        logicEntitydetector = new EntityDetectorBlock(MOD_NAME + "logicentitydetector"){{
            BlockUtils.requirements(this, Categories.logic, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        logicEntitypointer = new EntityPointerBlock(MOD_NAME + "logicentitypointer"){{
            BlockUtils.requirements(this, Categories.logic, new ItemStack[]{new ItemStack(Items.silicon, 1)});
            size = 2;
        }};

        logicBlockSensor = new BlockSensorBlock(MOD_NAME + "logicblocksensor"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        logicRotator = new RotatorBlock(MOD_NAME + "logicrotator"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        foamBlock = new FoamBlock(MOD_NAME + "foamblock");

        hardFoamBlock = new Block(MOD_NAME + "hardfoamblock"){{
            health = 150;
            controllable = false;
            solid = true;
            destructible = true;
            rotate = true;
        }};

        foamMechPad = new LiquidMechPad(MOD_NAME + "mechpad-foammech"){{
            requirements(Category.upgrade, new ItemStack[]{new ItemStack(Items.silicon, 1)});
            mech = Mechs.foam;
            size = 2;
            consumes.power(1f);
            liquidType = Liquids.liquidFoam;
        }};

        foamNozzle = new FoamNozzleBlock(MOD_NAME + "foamnozzle"){{
            BlockUtils.requirements(this, Categories.logic, new ItemStack[]{new ItemStack(Items.silicon, 1)});
            consumes.liquid(Liquids.liquidFoam, 10f).update(false);
        }};

        repulsor = new RepulsorBlock(MOD_NAME + "repulsor"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        attractor = new AttractorBlock(MOD_NAME + "attractor"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        bracket = new BracketBlock(MOD_NAME + "bracketblock"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
            health = 50;
        }};

        bracketAdhesive = new BracketBlock(MOD_NAME + "bracketblock-adhesive"){{
                BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
                rotate = true;
            }

            @Override
            protected boolean addNearbyFront(Tile tile, Tile front) {
                return true;
            }
        };

        bracketFrictionless = new BracketBlock(MOD_NAME + "bracketblock-frictionless"){{
                BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
                rotate = true;
            }

            @Override
            protected boolean addNearbyFront(Tile tile, Tile front) {
                return false;
            }
        };

        bracketdoor = new BracketDoor(MOD_NAME + "bracketdoor"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
            health = 250;
        }};

        playerChair = new PlayerChair(MOD_NAME + "playerchair"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
            health = 150;
        }};

        carrier = new CarrierBlock(MOD_NAME + "carrierblock"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
            health = 100;
        }};

        anchor = new AnchorBlock(MOD_NAME + "anchor"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
            health = 100;
        }};

        explosives = new ExplosivesBlock(MOD_NAME + "explosives"){{
            BlockUtils.requirements(this, Categories.logic, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        blockrelay = new BlockRelayBlock(MOD_NAME + "blockrelay"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        blockchest = new BlockChestBlock(MOD_NAME + "blockchest"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
            size = 2;
        }};

        blockbreaker = new BlockBreakerBlock(MOD_NAME + "blockbreaker"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        blockplacer = new BlockPlacerBlock(MOD_NAME + "blockplacer"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        blocksorter = new BlockSorterBlock(MOD_NAME + "blocksorter"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        blockconfigurator = new ConfiguratorBlock(MOD_NAME + "configurator"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};

        blockconstructor = new BlockBuildBlock(MOD_NAME + "blockconstructor"){{
            BlockUtils.requirements(this, Categories.world, new ItemStack[]{new ItemStack(Items.silicon, 1)});
        }};
    }
}
