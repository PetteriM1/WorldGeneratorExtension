package worldgeneratorextension.global.math;

import cn.nukkit.math.NukkitRandom;

public final class Mth {

    public static int nextInt(NukkitRandom random, int origin, int bound) {
        return origin >= bound ? origin : random.nextBoundedInt(bound - origin + 1) + origin;
    }
}
