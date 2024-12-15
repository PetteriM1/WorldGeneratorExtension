package worldgeneratorextension.shpop.loot;

import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import worldgeneratorextension.global.util.RandomizableContainer;
import com.google.common.collect.Maps;

//\\ ./data/behavior_packs/vanilla/loot_tables/chests/stronghold_crossing.json
public class StrongholdCrossingChest extends RandomizableContainer {

    private static final StrongholdCrossingChest INSTANCE = new StrongholdCrossingChest();

    public static StrongholdCrossingChest get() {
        return INSTANCE;
    }

    private StrongholdCrossingChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.IRON_INGOT, 0, 5, 50))
                .register(new ItemEntry(Item.GOLD_INGOT, 0, 3, 25))
                .register(new ItemEntry(Item.REDSTONE, 0, 9, 4, 25))
                .register(new ItemEntry(Item.COAL, 0, 8, 3, 50))
                .register(new ItemEntry(Item.BREAD, 0, 3, 75))
                .register(new ItemEntry(Item.APPLE, 0, 3, 75))
                .register(new ItemEntry(Item.IRON_PICKAXE, 5))
                .register(new ItemEntry(Item.ENCHANTED_BOOK, 6)) //TODO: treasure enchant_with_levels 30
                .register(new ItemEntry(Item.DYE, 0, 3, 75));
        this.pools.put(pool1.build(), new RollEntry(4, 1, pool1.getTotalWeight()));
    }
}
