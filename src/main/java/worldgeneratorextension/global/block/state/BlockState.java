package worldgeneratorextension.global.block.state;

import cn.nukkit.block.Block;
import worldgeneratorextension.global.math.Rotation;

public class BlockState {

    public static final BlockState AIR = new BlockState(0);

    private final int id;
    private final int meta;

    public BlockState(int id) {
        this(id, 0);
    }

    public BlockState(int id, int meta) {
        this.id = id;
        this.meta = meta;
    }

    public int getId() {
        return this.id;
    }

    public int getMeta() {
        return this.meta;
    }

    public Block getBlock() {
        return Block.get(this.id, this.meta);
    }

    public BlockState rotate(Rotation rot) {
        switch (rot) {
            case CLOCKWISE_90:
                return new BlockState(this.id, Rotation.clockwise90(this.id, this.meta));
            case CLOCKWISE_180:
                return new BlockState(this.id, Rotation.clockwise180(this.id, this.meta));
            case COUNTERCLOCKWISE_90:
                return new BlockState(this.id, Rotation.counterclockwise90(this.id, this.meta));
            case NONE:
            default:
                return this;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BlockState) {
            BlockState o = (BlockState) obj;
            return this.id == o.id && this.meta == o.meta;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id << 6 | this.meta;
    }
}
