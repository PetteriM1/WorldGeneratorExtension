package worldgeneratorextension.singletspop.loot;

import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import worldgeneratorextension.global.util.RandomizableContainer;
import com.google.common.collect.Maps;

public class ShipwreckTreasureChest extends RandomizableContainer {

    private static final ShipwreckTreasureChest INSTANCE = new ShipwreckTreasureChest();

    public static ShipwreckTreasureChest get() {
        return INSTANCE;
    }

    private ShipwreckTreasureChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.IRON_INGOT, 0, 5, 90))
                .register(new ItemEntry(Item.GOLD_INGOT, 0, 5, 10))
                .register(new ItemEntry(Item.EMERALD, 0, 5, 40))
                .register(new ItemEntry(Item.DIAMOND, 5))
                .register(new ItemEntry(Item.EXPERIENCE_BOTTLE, 5));
        this.pools.put(pool1.build(), new RollEntry(6, 3, pool1.getTotalWeight()));

        PoolBuilder pool2 = new PoolBuilder()
                .register(new ItemEntry(Item.IRON_INGOT, 0, 10, 50))
                .register(new ItemEntry(Item.GOLD_INGOT, 0, 10, 10))
                .register(new ItemEntry(Item.DYE, 4, 10, 20));
        this.pools.put(pool2.build(), new RollEntry(5, 2, pool2.getTotalWeight()));

        PoolBuilder pool3 = new PoolBuilder()
                .register(new ItemEntry(Item.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, 0, 2, 2, 6));
        this.pools.put(pool3.build(), new RollEntry(1, 1, pool3.getTotalWeight()));
    }
}
