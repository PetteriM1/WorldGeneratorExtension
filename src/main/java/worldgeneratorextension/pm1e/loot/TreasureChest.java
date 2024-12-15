package worldgeneratorextension.pm1e.loot;

import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPotion;
import com.google.common.collect.Maps;
import worldgeneratorextension.global.util.RandomizableContainer;

public class TreasureChest extends RandomizableContainer {

    private static final TreasureChest INSTANCE = new TreasureChest();

    public static TreasureChest get() {
        return INSTANCE;
    }

    private TreasureChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool0 = new PoolBuilder().register(new ItemEntry(Item.HEART_OF_THE_SEA, 1));
        this.pools.put(pool0.build(), new RollEntry(1, 0));

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.IRON_INGOT, 0, 5, 3, 20))
                .register(new ItemEntry(Item.CHAIN_HELMET, 0, 1, 1, 20))
                .register(new ItemEntry(Item.CHAIN_CHESTPLATE, 0, 1, 1, 20))
                .register(new ItemEntry(Item.CHAIN_LEGGINGS, 0, 1, 1, 20))
                .register(new ItemEntry(Item.CHAIN_BOOTS, 0, 1, 1, 20))
                .register(new ItemEntry(Item.POTION, ItemPotion.WATER_BREATHING, 1, 1, 15))
                .register(new ItemEntry(Item.DIAMOND, 0, 1, 1, 15))
                .register(new ItemEntry(Item.GOLD_INGOT, 0, 5, 1, 10))
                .register(new ItemEntry(Item.LEAD, 0, 3, 1, 10))
                .register(new ItemEntry(BlockID.TNT, 0, 2, 1, 10))
                .register(new ItemEntry(Item.NAME_TAG, 0, 1, 1, 10))
                .register(new ItemEntry(Item.POTION, ItemPotion.REGENERATION, 1, 1, 10))
                .register(new ItemEntry(Item.PRISMARINE_CRYSTALS, 0, 5, 1, 5))
                .register(new ItemEntry(Item.BOOK_AND_QUILL, 0, 2, 1, 5))
                .register(new ItemEntry(Item.RECORD_MELLOHI, 0, 1, 1, 5))
                .register(new ItemEntry(Item.RECORD_WAIT, 0, 1, 1, 5))
                .register(new ItemEntry(Item.EXPERIENCE_BOTTLE, 0, 1, 1, 3))
                .register(new ItemEntry(Item.CAKE, 0, 1, 1, 1));
        this.pools.put(pool1.build(), new RollEntry(5, 12, pool1.getTotalWeight()));
    }
}
