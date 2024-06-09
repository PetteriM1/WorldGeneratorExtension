package worldgeneratorextension.global.biome;

import cn.nukkit.level.biome.EnumBiome;

public class BiomeTypes {

    public static final boolean[] OCEAN_BIOMES = new boolean[256];

    static {
        OCEAN_BIOMES[EnumBiome.OCEAN.id] = true;
        OCEAN_BIOMES[EnumBiome.FROZEN_OCEAN.id] = true;
        OCEAN_BIOMES[EnumBiome.WARM_OCEAN.id] = true;
        OCEAN_BIOMES[EnumBiome.LUKEWARM_OCEAN.id] = true;
        OCEAN_BIOMES[EnumBiome.COLD_OCEAN.id] = true;
        OCEAN_BIOMES[EnumBiome.DEEP_OCEAN.id] = true;
        OCEAN_BIOMES[EnumBiome.DEEP_WARM_OCEAN.id] = true;
        OCEAN_BIOMES[EnumBiome.DEEP_LUKEWARM_OCEAN.id] = true;
        OCEAN_BIOMES[EnumBiome.DEEP_COLD_OCEAN.id] = true;
        OCEAN_BIOMES[EnumBiome.DEEP_FROZEN_OCEAN.id] = true;
    }
}
