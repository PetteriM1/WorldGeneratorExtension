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
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import worldgeneratorextension.mspop.populator.PopulatorMineshaft;
import worldgeneratorextension.multitspop.populator.*;
import worldgeneratorextension.nbpop.populator.PopulatorNetherFortress;
import worldgeneratorextension.nbpop.structure.NetherBridgePieces;
import worldgeneratorextension.ompop.populator.PopulatorOceanMonument;
import worldgeneratorextension.pm1e.populator.PopulatorRuinedPortal;
import worldgeneratorextension.pm1e.populator.PopulatorTreasureChest;
import worldgeneratorextension.quasistructure.populator.PopulatorDesertWell;
import worldgeneratorextension.quasistructure.populator.PopulatorDungeon;
import worldgeneratorextension.scatteredbuilding.populator.PopulatorDesertPyramid;
import worldgeneratorextension.scatteredbuilding.populator.PopulatorJungleTemple;
import worldgeneratorextension.scatteredbuilding.populator.PopulatorSwampHut;
import worldgeneratorextension.shpop.populator.PopulatorStronghold;
import worldgeneratorextension.pm1e.populator.PopulatorCoralCrust;
import worldgeneratorextension.singletspop.populator.PopulatorFossil;
import worldgeneratorextension.pm1e.populator.PopulatorNetherFossil;
import worldgeneratorextension.singletspop.populator.PopulatorShipwreck;
import worldgeneratorextension.global.task.ChunkPopulateTask;
import worldgeneratorextension.theend.noise.SimplexNoise;
import worldgeneratorextension.theend.object.theend.ObsidianPillar;
import worldgeneratorextension.theend.populator.theend.*;
import worldgeneratorextension.vanillagenerator.NormalGenerator;
import worldgeneratorextension.vipop.populator.PopulatorVillage;
import worldgeneratorextension.vipop.structure.VillagePieces;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Loader extends PluginBase implements Listener {

    public static Loader INSTANCE;

    private static final List<Populator> populatorsOverworld = new ArrayList<>();
    private static final List<Populator> populatorsNether = new ArrayList<>();
    private static final Map<Long, List<Populator>> populatorsEnd = new HashMap<>();

    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        Plugin betterVanillaGenerator = getServer().getPluginManager().getPlugin("BetterVanillaGenerator");
        if (betterVanillaGenerator != null && "cn.wode490390.nukkit.vanillagenerator.BetterGenerator".equals(betterVanillaGenerator.getDescription().getMain())) {
            getLogger().warning("Already loaded plugin cn.wode490390.nukkit.vanillagenerator.BetterGenerator found. Using WorldGeneratorExtension instead is recommended.");
        }

        Plugin theEnd = getServer().getPluginManager().getPlugin("TheEnd");
        if (theEnd != null && "cn.wode490390.nukkit.theend.TheEnd".equals(theEnd.getDescription().getMain())) {
            getLogger().warning("Already loaded plugin cn.wode490390.nukkit.theend.TheEnd found. Using WorldGeneratorExtension instead is recommended.");
        }

        boolean vanillaOverworld = getServer().getPropertyBoolean("wgenext.vanilla-overworld");
        if (vanillaOverworld) {
            getLogger().info("Using better vanilla overworld generator");
            Generator.addGenerator(NormalGenerator.class, "default", NormalGenerator.TYPE_INFINITE);
            Generator.addGenerator(NormalGenerator.class, "normal", NormalGenerator.TYPE_INFINITE);
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
        PopulatorCoralCrust.init();
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
        populatorsOverworld.add(new PopulatorVillage(!vanillaOverworld && Normal.seaHeight > 62));
        populatorsOverworld.add(new PopulatorStronghold());
        populatorsOverworld.add(new PopulatorOceanMonument());
        populatorsOverworld.add(new PopulatorMineshaft());
        populatorsOverworld.add(new PopulatorDesertWell());
        populatorsOverworld.add(new PopulatorDungeon());
        populatorsOverworld.add(new PopulatorCoralCrust());
        populatorsOverworld.add(new PopulatorTreasureChest());
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
        if (level.getGenerator() instanceof Void) {
            return;
        }

        if (level.getDimension() == Level.DIMENSION_OVERWORLD) {
            getServer().getScheduler().scheduleAsyncTask(this, new ChunkPopulateTask(level, event.getChunk(), Loader.populatorsOverworld));
        } else if (level.getDimension() == Level.DIMENSION_NETHER) {
            getServer().getScheduler().scheduleAsyncTask(this, new ChunkPopulateTask(level, event.getChunk(), Loader.populatorsNether));
        } else if (level.getDimension() == Level.DIMENSION_THE_END) {
            if (event.getChunk().getX() == 0 && event.getChunk().getZ() == 0) {
                getServer().getScheduler().scheduleAsyncTask(this, new ChunkPopulateTask(level, event.getChunk(), Collections.singleton(new PopulatorPodium())));
            } else {
                long seed = level.getSeed();
                List<Populator> populators = populatorsEnd.get(seed);
                if (populators == null) {
                    populators = new ArrayList<>();
                    SimplexNoise islandNoise = new SimplexNoise(new NukkitRandom(seed));
                    populators.add(new PopulatorEndGateway(islandNoise));
                    populators.add(new PopulatorChorusPlant(islandNoise));
                    populators.add(new PopulatorEndIsland(islandNoise));
                    ObsidianPillar[] obsidianPillars = ObsidianPillar.getObsidianPillars(seed);
                    for (ObsidianPillar obsidianPillar : obsidianPillars) {
                        populators.add(new PopulatorObsidianPillar(obsidianPillar));
                    }
                    populatorsEnd.put(seed, populators);
                }
                getServer().getScheduler().scheduleAsyncTask(this, new ChunkPopulateTask(level, event.getChunk(), populators));
            }
        }
    }

    public static RuntimeItemMapping getRuntimeItemMapptings() {
        if ("Nukkit PetteriM1 Edition".equals(Server.getInstance().getName())) {
            return RuntimeItems.getMapping(419);
        }

        try {
            //noinspection JavaReflectionMemberAccess
            return (RuntimeItemMapping) Class.forName("cn.nukkit.item.RuntimeItems").getMethod("getMapping").invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get RuntimeItemMapping", e);
        }
    }

    public static float getEndIslandHeight(int chunkX, int chunkZ, SimplexNoise islandNoise) {
        float xx = (chunkX << 1) + 1;
        float zz = (chunkZ << 1) + 1;

        float height = (float) (100 - Math.sqrt(Math.pow(xx, 2) + Math.pow(zz, 2)) * 8f);
        if (height > 80) {
            height = 80;
        }
        if (height < -100) {
            height = -100;
        }

        for (int cx = -12; cx <= 12; ++cx) {
            for (int cz = -12; cz <= 12; ++cz) {
                long x = chunkX + cx;
                long z = chunkZ + cz;

                if (Math.pow(x, 2) + Math.pow(z, 2) > 4096 && islandNoise.noise(x, z) < -0.8999999761581421) { // 0.9f / 1.0d
                    xx = 1 - (cx << 1);
                    zz = 1 - (cz << 1);
                    float height2 = (float) (100 - Math.sqrt(Math.pow(xx, 2) + Math.pow(zz, 2)) * ((Math.abs(x) * 3439 + Math.abs(z) * 147) % 13 + 9));
                    if (height2 > 80) {
                        height2 = 80;
                    }
                    if (height2 < -100) {
                        height2 = -100;
                    }
                    if (height2 > height) {
                        height = height2;
                    }
                }
            }
        }

        return height;
    }
}
