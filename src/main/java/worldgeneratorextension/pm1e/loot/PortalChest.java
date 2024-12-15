package worldgeneratorextension.pm1e.loot;

import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import com.google.common.collect.Maps;
import worldgeneratorextension.global.util.RandomizableContainer;

public class PortalChest extends RandomizableContainer {

    private static final PortalChest INSTANCE = new PortalChest();

    public static PortalChest get() {
        return INSTANCE;
    }

    private PortalChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.IRON_NUGGET, 0, 18, 9, 40))
                .register(new ItemEntry(Item.FLINT, 0, 4, 1, 40))
                .register(new ItemEntry(Item.OBSIDIAN, 0, 2, 1, 40))
                .register(new ItemEntry(Item.FIRE_CHARGE, 0, 1, 1, 40))
                .register(new ItemEntry(Item.FLINT_AND_STEEL, 0, 1, 1, 40))
                .register(new ItemEntry(Item.GOLD_NUGGET, 0, 24, 4, 15))
                .register(new ItemEntry(Item.GOLDEN_APPLE, 0, 1, 1, 15))
                .register(new ItemEntry(Item.GOLDEN_AXE, 0, 1, 1, 15))
                .register(new ItemEntry(Item.GOLDEN_HOE, 0, 1, 1, 15))
                .register(new ItemEntry(Item.GOLDEN_PICKAXE, 0, 1, 1, 15))
                .register(new ItemEntry(Item.GOLDEN_SHOVEL, 0, 1, 1, 15))
                .register(new ItemEntry(Item.GOLDEN_SWORD, 0, 1, 1, 15))
                .register(new ItemEntry(Item.GOLDEN_HELMET, 0, 1, 1, 15))
                .register(new ItemEntry(Item.GOLDEN_CHESTPLATE, 0, 1, 1, 15))
                .register(new ItemEntry(Item.GOLDEN_LEGGINGS, 0, 1, 1, 15))
                .register(new ItemEntry(Item.GOLDEN_BOOTS, 0, 1, 1, 15))
                .register(new ItemEntry(Item.GLISTERING_MELON_SLICE, 0, 12, 4, 5))
                .register(new ItemEntry(Item.GOLDEN_CARROT, 0, 12, 4, 5))
                .register(new ItemEntry(Item.GOLD_INGOT, 0, 8, 2, 5))
                .register(new ItemEntry(Item.CLOCK, 0, 1, 1, 5))
                .register(new ItemEntry(Item.LIGHT_WEIGHTED_PRESSURE_PLATE, 0, 1, 1, 5))
                .register(new ItemEntry(Item.GOLDEN_HORSE_ARMOR, 0, 1, 1, 5))
                .register(new ItemEntry(BlockID.GOLD_BLOCK, 0, 2, 1, 1))
                .register(new ItemEntry(255 - BlockID.BELL, 0, 1, 1, 1))
                .register(new ItemEntry(Item.ENCHANTED_GOLDEN_APPLE, 0, 1, 1, 1));
        this.pools.put(pool1.build(), new RollEntry(8, 4, pool1.getTotalWeight()));
    }
}
