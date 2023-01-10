package net.mehvahdjukaar.ohmygoat.fabric;

import net.mehvahdjukaar.ohmygoat.common.BreakMemory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class OhMyGoatPlatformStuffImpl {
    public static BreakMemory getBreakMemory(ServerLevel level, BlockPos toBreakPos, BlockState toBreak) {
        return FabricRamBreakingHandler.getOrCreateBreakMemory(level, toBreakPos, toBreak);
    }
}
