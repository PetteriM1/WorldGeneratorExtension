package worldgeneratorextension.scatteredbuilding.populator;

import cn.nukkit.Server;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import worldgeneratorextension.global.task.CallbackableScatteredGenerationTask;
import worldgeneratorextension.scatteredbuilding.structure.StructureBoundingBox;
import worldgeneratorextension.scatteredbuilding.structure.piece.ScatteredStructurePiece;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;

public abstract class PopulatorScatteredStructure extends Populator {

    protected static final int MIN_DISTANCE = 8;
    protected static final int MAX_DISTANCE = 32;

    protected final Map<Long, Set<Long>> waitingChunks = Maps.newConcurrentMap();

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (this.canGenerate(chunkX, chunkZ, random, chunk)) {
            ScatteredStructurePiece piece = this.getPiece(chunkX, chunkZ);
            StructureBoundingBox boundingBox = piece.getBoundingBox();

            if (boundingBox.getMinChunkX() != boundingBox.getMaxChunkX() || boundingBox.getMinChunkZ() != boundingBox.getMaxChunkZ()) { // cross-chunk
                Level world = chunk.getProvider().getLevel();
                Set<BaseFullChunk> chunks = Sets.newHashSet();
                Set<Long> indexes = Sets.newConcurrentHashSet();

                for (int cX = boundingBox.getMinChunkX(); cX <= boundingBox.getMaxChunkX(); cX++) {
                    for (int cZ = boundingBox.getMinChunkZ(); cZ <= boundingBox.getMaxChunkZ(); cZ++) {
                        BaseFullChunk ck = world.getChunk(cX, cZ, true);
                        if (!ck.isGenerated()) {
                            chunks.add(ck);
                            indexes.add(Level.chunkHash(cX, cZ));
                        }
                    }
                }

                if (!chunks.isEmpty()) {
                    this.waitingChunks.put(Level.chunkHash(chunkX, chunkZ), indexes);
                    chunks.forEach(ck -> Server.getInstance().getScheduler().scheduleAsyncTask(null, new CallbackableScatteredGenerationTask(world, ck, this, piece, level, chunkX, chunkZ)));
                    return;
                }
            }

            this.generate(level, chunkX, chunkZ, piece);
        }
    }

    protected void generate(ChunkManager level, int chunkX, int chunkZ, ScatteredStructurePiece piece) {
        piece.generate(level, new NukkitRandom(0xdeadbeef ^ (chunkX << 8) ^ chunkZ ^ level.getSeed()));
        this.waitingChunks.remove(Level.chunkHash(chunkX, chunkZ));
    }

    public void generateChunkCallback(ChunkManager level, int startChunkX, int startChunkZ, ScatteredStructurePiece piece, int chunkX, int chunkZ) {
        Set<Long> indexes = this.waitingChunks.get(Level.chunkHash(startChunkX, startChunkZ));
        indexes.remove(Level.chunkHash(chunkX, chunkZ));
        if (indexes.isEmpty()) {
            this.generate(level, startChunkX, startChunkZ, piece);
        }
    }

    protected boolean canGenerate(int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        return (chunkX < 0 ? (chunkX - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkX / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkX && (chunkZ < 0 ? (chunkZ - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkZ / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkZ;
    }

    protected BlockVector3 getStart(int chunkX, int chunkZ) {
        return new BlockVector3(chunkX << 4, 64, chunkZ << 4);
    }

    protected abstract ScatteredStructurePiece getPiece(int chunkX, int chunkZ);
}
