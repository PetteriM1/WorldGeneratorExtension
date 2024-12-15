package worldgeneratorextension.global.template;

import cn.nukkit.math.BlockVector3;

public interface StructureTemplate {

    BlockVector3 getSize();

    boolean isInvalid();

    void clean();
}
