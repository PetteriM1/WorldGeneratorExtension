package worldgeneratorextension;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.ChunkPopulateEvent;
import cn.nukkit.item.RuntimeItemMapping;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.level.generator.Void;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import worldgeneratorextension.mspop.populator.PopulatorMineshaft;
import worldgeneratorextension.multitspop.populator.*;
import worldgeneratorextension.nbpop.populator.PopulatorNetherFortress;
import worldgeneratorextension.nbpop.structure.NetherBridgePieces;
import worldgeneratorextension.ompop.populator.PopulatorOceanMonument;
import worldgeneratorextension.quasistructure.populator.PopulatorDesertWell;
import worldgeneratorextension.quasistructure.populator.PopulatorDungeon;
import worldgeneratorextension.scatteredbuilding.populator.PopulatorDesertPyramid;
import worldgeneratorextension.scatteredbuilding.populator.PopulatorJungleTemple;
import worldgeneratorextension.scatteredbuilding.populator.PopulatorSwampHut;
import worldgeneratorextension.shpop.populator.PopulatorStronghold;
import worldgeneratorextension.singletspop.populator.PopulatorCoralCrust;
import worldgeneratorextension.singletspop.populator.PopulatorFossil;
import worldgeneratorextension.singletspop.populator.PopulatorNetherFossil;
import worldgeneratorextension.singletspop.populator.PopulatorShipwreck;
import worldgeneratorextension.global.task.ChunkPopulateTask;
import worldgeneratorextension.vipop.populator.PopulatorVillage;
import worldgeneratorextension.vipop.structure.VillagePieces;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Loader extends PluginBase implements Listener {

    public static Loader INSTANCE;

    private static final List<Populator> populatorsOverworld = new ArrayList<>();
    private static final List<Populator> populatorsNether = new ArrayList<>();

    @Override
    public void onLoad() {
        INSTANCE = this;

        Generator.addGenerator(worldgeneratorextension.theend.generator.TheEndGenerator.class, "the_end", Generator.TYPE_THE_END);
    }

    @Override
    public void onEnable() {
        Plugin theEnd = getServer().getPluginManager().getPlugin("TheEnd");
        if (theEnd != null && "cn.wode490390.nukkit.theend.TheEnd".equals(theEnd.getDescription().getMain())) {
            getLogger().info("Disabling already loaded cn.wode490390.nukkit.theend.TheEnd");
            getServer().getPluginManager().disablePlugin(theEnd);
        }

        PopulatorFossil.init();
        PopulatorShipwreck.init();
        PopulatorIgloo.init();
        PopulatorPillagerOutpost.init();
        PopulatorOceanRuin.init();
        PopulatorRuinedPortal.init();
        VillagePieces.init();
        PopulatorStronghold.init();
        PopulatorOceanMonument.init();
        PopulatorMineshaft.init();
        NetherBridgePieces.init();
        PopulatorNetherFossil.init();
        populatorsOverworld.add(new PopulatorFossil());
        populatorsOverworld.add(new PopulatorShipwreck());
        populatorsOverworld.add(new PopulatorSwampHut());
        populatorsOverworld.add(new PopulatorDesertPyramid());
        populatorsOverworld.add(new PopulatorJungleTemple());
        populatorsOverworld.add(new PopulatorIgloo());
        populatorsOverworld.add(new PopulatorPillagerOutpost());
        populatorsOverworld.add(new PopulatorOceanRuin());
        populatorsOverworld.add(new PopulatorRuinedPortal());
        populatorsOverworld.add(new PopulatorVillage(Normal.seaHeight > 62));
        populatorsOverworld.add(new PopulatorStronghold());
        populatorsOverworld.add(new PopulatorOceanMonument());
        populatorsOverworld.add(new PopulatorMineshaft());
        populatorsOverworld.add(new PopulatorDesertWell());
        populatorsOverworld.add(new PopulatorDungeon());
        populatorsOverworld.add(new PopulatorCoralCrust());
        populatorsNether.add(new PopulatorNetherFortress());
        populatorsNether.add(new PopulatorNetherFossil());
        getServer().getPluginManager().registerEvents(this, this);
    }

    public static CompoundTag loadNBT(String path) {
        try (InputStream inputStream = Loader.class.getClassLoader().getResourceAsStream(path)) {
            return NBTIO.readCompressed(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        Level level = event.getLevel();
        if (level.getDimension() == Level.DIMENSION_OVERWORLD && !(level.getGenerator() instanceof Void)) {
            getServer().getScheduler().scheduleAsyncTask(this, new ChunkPopulateTask(level, event.getChunk(), Loader.populatorsOverworld));
        } else if (level.getDimension() == Level.DIMENSION_NETHER) {
            getServer().getScheduler().scheduleAsyncTask(this, new ChunkPopulateTask(level, event.getChunk(), Loader.populatorsNether));
        }
    }

    public static RuntimeItemMapping getRuntimeItemMapptings() {
        if ("Nukkit PetteriM1 Edition".equals(Server.getInstance().getName())) {
            return RuntimeItems.getMapping(419);
        }
        try {
            return (RuntimeItemMapping) Class.forName("cn.nukkit.item.RuntimeItems").getMethod("getMapping").invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get RuntimeItemMapping", e);
        }
    }
}
