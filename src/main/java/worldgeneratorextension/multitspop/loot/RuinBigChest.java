package worldgeneratorextension.multitspop.loot;

import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import worldgeneratorextension.global.util.RandomizableContainer;
import com.google.common.collect.Maps;

public class RuinBigChest extends RandomizableContainer {

    private static final RuinBigChest INSTANCE = new RuinBigChest();

    public static RuinBigChest get() {
        return INSTANCE;
    }

    private RuinBigChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.COAL, 0, 4, 10))
                .register(new ItemEntry(Item.GOLD_NUGGET, 0, 3, 10))
                .register(new ItemEntry(Item.EMERALD, 1))
                .register(new ItemEntry(Item.WHEAT, 0, 3, 2, 10));
        this.pools.put(pool1.build(), new RollEntry(8, 2, pool1.getTotalWeight()));

        PoolBuilder pool2 = new PoolBuilder()
                .register(new ItemEntry(Item.GOLDEN_APPLE, 1))
                .register(new ItemEntry(Item.ENCHANT_BOOK, 5)) //TODO: enchant_randomly
                .register(new ItemEntry(Item.LEATHER_TUNIC, 1))
                .register(new ItemEntry(Item.GOLD_HELMET, 1))
                .register(new ItemEntry(Item.FISHING_ROD, 5)) //TODO: enchant_randomly
                .register(new ItemEntry(Item.EMPTY_MAP, 10)); //TODO: exploration_map buried treasure
        this.pools.put(pool2.build(), new RollEntry(1, pool2.getTotalWeight()));
    }
}
