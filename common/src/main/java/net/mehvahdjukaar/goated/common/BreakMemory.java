package net.mehvahdjukaar.goated.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BreakMemory {

    public static final AtomicInteger NEXT_BREAKER_ID = new AtomicInteger();

    private static final LoadingCache<BlockPos, Integer> RESERVED_BREAKER_IDS = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public Integer load(BlockPos key) {
                    return null;
                }
            });

    private final BlockState state;
    private final BlockPos pos;
    private final int breakerId;
    private int breakProgress;
    private long lastTouchedTimestamp;

    public BreakMemory(BlockState state, BlockPos pos) {
        this(state, pos, 0);
    }

    public BreakMemory(BlockState state, BlockPos pos, int progress) {
        this.state = state;
        this.pos = pos;
        this.breakProgress = progress;

        Integer id = RESERVED_BREAKER_IDS.getIfPresent(pos);
        if (id == null) {
            id = (Integer.MAX_VALUE / 2) - NEXT_BREAKER_ID.incrementAndGet();
            RESERVED_BREAKER_IDS.put(pos, id);
        }
        this.breakerId = id;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setTimestamp(long lastTouchedTimestamp) {
        this.lastTouchedTimestamp = lastTouchedTimestamp;
    }

    public long getTimestamp() {
        return lastTouchedTimestamp;
    }

    public int getBreakProgress() {
        return breakProgress;
    }

    public void setBreakProgress(int breakProgress) {
        this.breakProgress = breakProgress;
    }

    public BlockState getState() {
        return state;
    }

    public int getBreakerId() {
        return breakerId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BreakMemory) obj;
        return Objects.equals(this.state, that.state) &&
                Objects.equals(this.breakerId, that.breakerId) &&
                Objects.equals(this.breakProgress, that.breakProgress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, breakerId, breakProgress);
    }

    public CompoundTag save() {
        var c = new CompoundTag();
        c.putShort("progress", (short) this.breakProgress);
        c.put("pos", NbtUtils.writeBlockPos(pos));
        return c;
    }

    public static BreakMemory load(CompoundTag c, Level level) {
        BlockPos pos = NbtUtils.readBlockPos(c.getCompound("pos"));
        int progress = c.getShort("progress");
        var m = new BreakMemory(level.getBlockState(pos), pos, progress);
        m.setTimestamp(level.getGameTime());
        return m;
    }
}