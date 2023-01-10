package net.mehvahdjukaar.ohmygoat.forge;

import net.mehvahdjukaar.ohmygoat.common.BreakMemory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//provider & instance. Only one instance is attached to a world at a time
public class RamBreakingCap implements ICapabilitySerializable<CompoundTag> {

    private static final int MAX_TIME = 20 * 10;

    private final LazyOptional<RamBreakingCap> lazyOptional = LazyOptional.of(() -> this);

    private final Map<BlockPos, BreakMemory> breakProgress = new HashMap<>();
    private final ServerLevel level;

    RamBreakingCap(ServerLevel level) {
        this.level = level;
    }

    public void invalidate() {
        lazyOptional.invalidate();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return OhMyGoatForge.RAM_BREAK_CAP.orEmpty(cap, lazyOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag total = new CompoundTag();
        var l = new ListTag();
        breakProgress.values().forEach(s -> l.add(s.save()));
        total.put("RamBreakProgress", l);
        return total;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        var l = tag.getList("RamBreakProgress", 10);
        for (var t : l) {
            var m = BreakMemory.load((CompoundTag) t, this.level);
            breakProgress.put(m.getPos(), m);
        }
    }

    public void validateAll() {
        if (!breakProgress.isEmpty()) {
            var values = new ArrayList<>(breakProgress.keySet());
            for (BlockPos pos : values) {
                var m = breakProgress.get(pos);
                if (m == null){
                    continue;
                }
                if (level.getBlockState(pos) != m.getState() || level.getGameTime() - m.getTimestamp() > MAX_TIME) {
                    level.destroyBlockProgress(m.getBreakerId(), pos, -1);
                    breakProgress.remove(pos);
                }
            }
        }
    }

    public BreakMemory getOrCreateBreakMemory(BlockPos pos, BlockState state) {
        var memory = breakProgress.get(pos);
        if (memory == null || memory.getState() != state) {
            memory = new BreakMemory(state, pos);
            breakProgress.put(pos, memory);
        }
        return memory;
    }

}