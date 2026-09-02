package net.mehvahdjukaar.goated.platform;

import net.mehvahdjukaar.goated.common.BreakMemory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class GoatedPlatformStuffImpl {
    public static BreakMemory getBreakMemory(ServerLevel level, BlockPos pos, BlockState state) {
        return level.getData(GoatedForge.BREAK_DATA).getOrCreateBreakMemory(pos, state);
    }
}
