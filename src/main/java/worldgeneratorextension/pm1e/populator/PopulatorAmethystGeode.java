package worldgeneratorextension.pm1e.populator;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAmethystBud;
import cn.nukkit.block.BlockLayer;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.noise.Perlin;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import com.google.common.collect.AbstractIterator;
import it.unimi.dsi.fastutil.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PopulatorAmethystGeode extends Populator {

    private static final List<Integer> INNER_PLACEMENTS = Arrays.asList(
            Block.SMALL_AMETHYST_BUD,
            Block.MEDIUM_AMETHYST_BUD,
            Block.LARGE_AMETHYST_BUD,
            Block.AMETHYST_CLUSTER
    );

    private static Iterable<Position> between(Position a, Position b) {
        int minX = Math.min((int) a.x, (int) b.x);
        int minY = Math.min((int) a.y, (int) b.y);
        int minZ = Math.min((int) a.z, (int) b.z);
        int maxX = Math.max((int) a.x, (int) b.x);
        int maxY = Math.max((int) a.y, (int) b.y);
        int maxZ = Math.max((int) a.z, (int) b.z);

        int diffX = maxX - minX + 1;
        int diffY = maxY - minY + 1;
        int diffZ = maxZ - minZ + 1;
        int end = diffX * diffY * diffZ;

        return () -> new AbstractIterator<Position>() {
            private final Position cursor = new Position(0, 0, 0, a.level);
            private int index;

            @Override
            protected Position computeNext() {
                if (this.index == end) {
                    return this.endOfData();
                } else {
                    int x = this.index % diffX;
                    int i = this.index / diffX;
                    int y = i % diffY;
                    int z = i / diffY;
                    this.index++;
                    return this.cursor.setComponents(minX + x, minY + y, minZ + z);
                }
            }
        };
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (random.nextRange(1, 24) == 1) {
            Position placePos = new Position((chunkX << 4) + 8, random.nextRange(5, 30), (chunkZ << 4) + 8);
            Perlin noise = new Perlin(new NukkitRandom(random.nextRange(0, Integer.MAX_VALUE - 1)), -4, 1.0);

            int distributionPoints = random.nextRange(3, 4);
            double d = (double) distributionPoints / 5;
            double filling = 1.0 / Math.sqrt(1.7);
            double innerLayer = 1.0 / Math.sqrt(2.2 + d);
            double middleLayer = 1.0 / Math.sqrt(3.2 + d);
            double outerLayer = 1.0 / Math.sqrt(4.2 + d);
            double crackSize = 1.0 / Math.sqrt(2.0 + random.nextDouble() / 2.0 + (distributionPoints > 3 ? d : 0.0));
            boolean generateCrack = random.nextFloat() < 0.95;

            int invalidCount = 0;
            List<Pair<Position, Integer>> pointOffsets = new ArrayList<>();

            for (int i = 0; i < distributionPoints; i++) {
                int outerWallDistanceX = random.nextRange(4, 6);
                int outerWallDistanceY = random.nextRange(4, 6);
                int outerWallDistanceZ = random.nextRange(4, 6);
                Position wallPos = placePos.add(outerWallDistanceX, outerWallDistanceY, outerWallDistanceZ);
                int blockId = level.getBlockIdAt((int) wallPos.x, (int) wallPos.y, (int) wallPos.z);
                if (blockId == Block.AIR) {
                    if (++invalidCount > 1) {
                        return;
                    }
                }

                pointOffsets.add(Pair.of(wallPos, random.nextRange(1, 2)));
            }

            List<Position> crack = new ArrayList<>();

            if (generateCrack) {
                int randomInt = random.nextBoundedInt(4);
                int offset = distributionPoints * 2 + 1;

                if (randomInt == 0) {
                    crack.add(placePos.add(offset, 7, 0));
                    crack.add(placePos.add(offset, 5, 0));
                    crack.add(placePos.add(offset, 1, 0));
                } else if (randomInt == 1) {
                    crack.add(placePos.add(0, 7, offset));
                    crack.add(placePos.add(0, 5, offset));
                    crack.add(placePos.add(0, 1, offset));
                } else if (randomInt == 2) {
                    crack.add(placePos.add(offset, 7, offset));
                    crack.add(placePos.add(offset, 5, offset));
                    crack.add(placePos.add(offset, 1, offset));
                } else {
                    crack.add(placePos.add(0, 7, 0));
                    crack.add(placePos.add(0, 5, 0));
                    crack.add(placePos.add(0, 1, 0));
                }
            }

            List<Position> potentialPlacements = new ArrayList<>();

            for (Position pos : between(placePos.add(-16, -16, -16), placePos.add(16, 16, 16))) {
                double noiseValue = noise.getNoise3D(pos.getX(), pos.getY(), pos.getZ()) * 0.05;
                double pointOffset = 0.0;
                double crackOffset = 0.0;

                for (Pair<Position, Integer> pair : pointOffsets) {
                    pointOffset += 1.0 / (Math.sqrt(pos.distanceSquared(pair.first()) + pair.second()) + noiseValue);
                }

                for (Position crackPos : crack) {
                    crackOffset += 1.0 / (Math.sqrt(pos.distanceSquared(crackPos) + 2) + noiseValue);
                }

                if (!(pointOffset < outerLayer)) {
                    if (generateCrack && crackOffset >= crackSize && pointOffset < filling) {
                        level.setBlockAtLayer((int) pos.x, (int) pos.y, (int) pos.z, BlockLayer.NORMAL, Block.AIR);
                        level.setBlockAtLayer((int) pos.x, (int) pos.y, (int) pos.z, BlockLayer.WATERLOGGED, Block.AIR);
                    } else if (pointOffset >= filling) {
                        level.setBlockAtLayer((int) pos.x, (int) pos.y, (int) pos.z, BlockLayer.NORMAL, Block.AIR);
                        level.setBlockAtLayer((int) pos.x, (int) pos.y, (int) pos.z, BlockLayer.WATERLOGGED, Block.AIR);
                    } else if (pointOffset >= innerLayer) {
                        boolean useAlternateLayer0 = random.nextFloat() < 0.083;
                        if (useAlternateLayer0) {
                            level.setBlockAtLayer((int) pos.x, (int) pos.y, (int) pos.z, BlockLayer.NORMAL, Block.AMETHYST_BLOCK);
                            level.setBlockAtLayer((int) pos.x, (int) pos.y, (int) pos.z, BlockLayer.WATERLOGGED, Block.AIR);
                        } else {
                            level.setBlockAtLayer((int) pos.x, (int) pos.y, (int) pos.z, BlockLayer.NORMAL, Block.BUDDING_AMETHYST);
                            level.setBlockAtLayer((int) pos.x, (int) pos.y, (int) pos.z, BlockLayer.WATERLOGGED, Block.AIR);
                        }

                        if (!useAlternateLayer0 && random.nextFloat() < 0.35) {
                            potentialPlacements.add(pos.clone());
                        }
                    } else if (pointOffset >= middleLayer) {
                        level.setBlockAtLayer((int) pos.x, (int) pos.y, (int) pos.z, BlockLayer.NORMAL, Block.CALCITE);
                        level.setBlockAtLayer((int) pos.x, (int) pos.y, (int) pos.z, BlockLayer.WATERLOGGED, Block.AIR);
                    } else if (pointOffset >= outerLayer) {
                        level.setBlockAtLayer((int) pos.x, (int) pos.y, (int) pos.z, BlockLayer.NORMAL, Block.SMOOTH_BASALT);
                        level.setBlockAtLayer((int) pos.x, (int) pos.y, (int) pos.z, BlockLayer.WATERLOGGED, Block.AIR);
                    }
                }
            }

            for (Position potentialPos : potentialPlacements) {
                int blockId = INNER_PLACEMENTS.get(random.nextBoundedInt(INNER_PLACEMENTS.size()));

                for (BlockFace direction : BlockFace.values()) {
                    Vector3 side = potentialPos.getSideVec(direction);

                    if (level.getBlockIdAt((int) side.x, (int) side.y, (int) side.z) == Block.AIR) {
                        BlockAmethystBud amethyst = (BlockAmethystBud) Block.get(blockId);
                        amethyst.setBlockFace(direction);
                        level.setBlockAtLayer((int) side.x, (int) side.y, (int) side.z, BlockLayer.NORMAL, amethyst.getId(), amethyst.getDamage());
                        level.setBlockAtLayer((int) side.x, (int) side.y, (int) side.z, BlockLayer.WATERLOGGED, Block.AIR);
                        break;
                    }
                }
            }
        }
    }
}
