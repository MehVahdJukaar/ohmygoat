package net.mehvahdjukaar.ohmygoat.forge;

import net.mehvahdjukaar.ohmygoat.common.BreakMemory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class OhMyGoatPlatformStuffImpl {
    public static BreakMemory getBreakMemory(ServerLevel level, BlockPos pos, BlockState state) {
        return level.getCapability(OhMyGoatForge.RAM_BREAK_CAP).resolve().get()
                .getOrCreateBreakMemory(pos, state);
    }
}
