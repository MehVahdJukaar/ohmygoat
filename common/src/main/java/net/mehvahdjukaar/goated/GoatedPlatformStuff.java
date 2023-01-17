package net.mehvahdjukaar.goated;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.goated.common.BreakMemory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class GoatedPlatformStuff {

    @ExpectPlatform
    public static BreakMemory getBreakMemory(ServerLevel level, BlockPos toBreakPos, BlockState toBreak) {
        throw new AssertionError();
    }
}
