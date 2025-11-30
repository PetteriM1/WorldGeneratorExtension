package worldgeneratorextension.shpop.loot;

import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import worldgeneratorextension.global.util.RandomizableContainer;
import com.google.common.collect.Maps;

//\\ ./data/behavior_packs/vanilla/loot_tables/chests/stronghold_library.json
public class StrongholdLibraryChest extends RandomizableContainer {

    private static final StrongholdLibraryChest INSTANCE = new StrongholdLibraryChest();

    public static StrongholdLibraryChest get() {
        return INSTANCE;
    }

    private StrongholdLibraryChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.BOOK, 0, 3, 100))
                .register(new ItemEntry(Item.PAPER, 0, 7, 2, 100))
                .register(new ItemEntry(Item.EMPTY_MAP, 5))
                .register(new ItemEntry(Item.COMPASS, 5))
                .register(new ItemEntry(Item.ENCHANTED_BOOK, 60)); //TODO: treasure enchant_with_levels 30
        this.pools.put(pool1.build(), new RollEntry(10, 2, pool1.getTotalWeight()));

        PoolBuilder pool2 = new PoolBuilder()
                .register(new ItemEntry(Item.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, 0, 1, 1, 1));
        this.pools.put(pool2.build(), new RollEntry(1, 1, pool2.getTotalWeight()));
    }
}
