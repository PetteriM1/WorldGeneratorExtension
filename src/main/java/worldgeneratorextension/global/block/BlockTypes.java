package worldgeneratorextension.global.block;

import cn.nukkit.block.Block;

public final class BlockTypes {

    public static boolean isLiquid(int id) {
        return id == Block.WATER || id == Block.STILL_WATER || id == Block.LAVA || id == Block.STILL_LAVA;
    }

    public static final boolean[] isPlant = new boolean[512];

    static {
        isPlant[Block.AIR] = true; //gap
        isPlant[Block.LOG] = true;
        isPlant[Block.LEAVES] = true;
        isPlant[Block.TALL_GRASS] = true;
        isPlant[Block.DEAD_BUSH] = true;
        isPlant[Block.DANDELION] = true;
        isPlant[Block.RED_FLOWER] = true;
        isPlant[Block.BROWN_MUSHROOM] = true;
        isPlant[Block.RED_MUSHROOM] = true;
        isPlant[Block.SNOW_LAYER] = true; //falls on trees
        isPlant[Block.CACTUS] = true;
        isPlant[Block.REEDS] = true;
        isPlant[Block.PUMPKIN] = true;
        isPlant[Block.BROWN_MUSHROOM_BLOCK] = true;
        isPlant[Block.RED_MUSHROOM_BLOCK] = true;
        isPlant[Block.MELON_BLOCK] = true;
        isPlant[Block.VINE] = true;
        isPlant[Block.WATER_LILY] = true;
        isPlant[Block.COCOA] = true;
        isPlant[Block.LEAVES2] = true;
        isPlant[Block.LOG2] = true;
        isPlant[Block.DOUBLE_PLANT] = true;
    }

    public static final boolean[] isPlantOrFluid = isPlant.clone();

    static {
        isPlantOrFluid[Block.WATER] = true;
        isPlantOrFluid[Block.STILL_WATER] = true;
        isPlantOrFluid[Block.LAVA] = true;
        isPlantOrFluid[Block.STILL_LAVA] = true;
        isPlantOrFluid[Block.ICE] = true; //solid water
        isPlantOrFluid[Block.PACKED_ICE] = true; //solid water
        isPlantOrFluid[Block.BLUE_ICE] = true; //solid water
    }

    private BlockTypes() {

    }
}
