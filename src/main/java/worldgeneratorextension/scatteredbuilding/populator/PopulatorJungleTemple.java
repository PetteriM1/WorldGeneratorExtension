package worldgeneratorextension.scatteredbuilding.populator;

import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitRandom;
import worldgeneratorextension.scatteredbuilding.structure.piece.JungleTemple;
import worldgeneratorextension.scatteredbuilding.structure.piece.ScatteredStructurePiece;

public class PopulatorJungleTemple extends PopulatorScatteredStructure {

    @Override
    protected boolean canGenerate(int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int biome = chunk.getBiomeId(7, 7);
        return (biome == EnumBiome.JUNGLE.id || biome == EnumBiome.JUNGLE_EDGE.id || biome == EnumBiome.JUNGLE_EDGE_M.id || biome == EnumBiome.JUNGLE_HILLS.id || biome == EnumBiome.JUNGLE_M.id) && super.canGenerate(chunkX, chunkZ, random, chunk);
    }

    @Override
    protected ScatteredStructurePiece getPiece(int chunkX, int chunkZ) {
        return new JungleTemple(this.getStart(chunkX, chunkZ));
    }
}
