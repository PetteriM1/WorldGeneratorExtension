package worldgeneratorextension.nbpop.populator;

import cn.nukkit.Server;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import worldgeneratorextension.global.math.BoundingBox;
import worldgeneratorextension.global.structure.StructurePiece;
import worldgeneratorextension.global.structure.StructureStart;
import worldgeneratorextension.global.task.CallbackableChunkGenerationTask;
import worldgeneratorextension.nbpop.structure.NetherBridgePieces;

import java.util.List;

public class PopulatorNetherFortress extends Populator {

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (chunk.getBiomeId(7, 7) == EnumBiome.HELL.id) {
            //\\ NetherFortressFeature::isFeatureChunk(BiomeSource const &,Random &,ChunkPos const &,uint)
            int gx = chunkX >> 4;
            int gz = chunkZ >> 4;
            long seed = level.getSeed();
            random.setSeed(gx ^ gz << 4 ^ seed);
            random.nextInt();
            if (random.nextBoundedInt(3) == (0x51d8e999 & 3) // salted
                    && chunkX == (gx << 4) + 4 + random.nextBoundedInt(8) && chunkZ == (gz << 4) + 4 + random.nextBoundedInt(8)) {
                random.setSeed(seed);
                int r1 = random.nextInt();
                int r2 = random.nextInt();

                //\\ NetherFortressFeature::createStructureStart(Dimension &,BiomeSource &,Random &,ChunkPos const &)
                NetherFortressStart start = new NetherFortressStart(level, chunkX, chunkZ);
                start.generatePieces(level, chunkX, chunkZ);

                if (start.isValid()) { //TODO: serialize nbt
                    BoundingBox boundingBox = start.getBoundingBox();
                    for (int cx = boundingBox.x0 >> 4; cx <= boundingBox.x1 >> 4; cx++) {
                        for (int cz = boundingBox.z0 >> 4; cz <= boundingBox.z1 >> 4; cz++) {
                            NukkitRandom rand = new NukkitRandom(cx * r1 ^ cz * r2 ^ seed);
                            int x = cx << 4;
                            int z = cz << 4;
                            BaseFullChunk ck = level.getChunk(cx, cz);
                            if (ck == null) {
                                ck = chunk.getProvider().getChunk(cx, cz, true);
                            }

                            if (ck.isGenerated()) {
                                start.postProcess(level, rand, new BoundingBox(x, z, x + 15, z + 15), cx, cz);
                            } else {
                                int f_cx = cx;
                                int f_cz = cz;
                                Server.getInstance().getScheduler().scheduleAsyncTask(null, new CallbackableChunkGenerationTask<>(
                                        chunk.getProvider().getLevel(), ck, start,
                                        structure -> structure.postProcess(level, rand, new BoundingBox(x, z, x + 15, z + 15), f_cx, f_cz)));
                            }
                        }
                    }
                }
            }
        }
    }

    public static class NetherFortressStart extends StructureStart {

        public NetherFortressStart(ChunkManager level, int chunkX, int chunkZ) {
            super(level, chunkX, chunkZ);
        }

        @Override
        public void generatePieces(ChunkManager level, int chunkX, int chunkZ) {
            NetherBridgePieces.StartPiece start = new NetherBridgePieces.StartPiece(this.random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
            this.pieces.add(start);
            start.addChildren(start, this.pieces, this.random);

            List<StructurePiece> pendingChildren = start.pendingChildren;
            while (!pendingChildren.isEmpty()) {
                pendingChildren.remove(this.random.nextBoundedInt(pendingChildren.size()))
                        .addChildren(start, this.pieces, this.random);
            }

            this.calculateBoundingBox();
            this.moveInsideHeights(this.random, 48, 70);
        }

        @Override
        public String getType() {
            return "Fortress";
        }
    }
}
