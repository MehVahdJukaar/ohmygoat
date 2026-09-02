package net.mehvahdjukaar.goated.platform;

import net.mehvahdjukaar.goated.common.BreakMemory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//provider & instance. Only one instance is attached to a world at a time
public class RamBreakingData implements INBTSerializable<CompoundTag> {

    private static final int MAX_TIME = 20 * 10;

    private final Map<BlockPos, BreakMemory> breakProgress = new HashMap<>();
    private ListTag lazyList = null;

    public void validateAll(ServerLevel level) {
        if (lazyList != null) {
            for (var t : lazyList) {
                var m = BreakMemory.load((CompoundTag) t, level);
                breakProgress.put(m.getPos(), m);
            }
            lazyList = null;
        }

        if (!breakProgress.isEmpty()) {
            var values = new ArrayList<>(breakProgress.keySet());
            for (BlockPos pos : values) {
                var m = breakProgress.get(pos);
                if (m == null) {
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

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider arg) {
        CompoundTag total = new CompoundTag();
        var l = new ListTag();
        breakProgress.values().forEach(s -> l.add(s.save()));
        total.put("RamBreakProgress", l);
        return total;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider arg, CompoundTag tag) {
        this.lazyList = tag.getList("RamBreakProgress", 10);
    }
}