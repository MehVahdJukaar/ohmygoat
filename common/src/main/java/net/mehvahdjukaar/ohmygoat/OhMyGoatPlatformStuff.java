package net.mehvahdjukaar.ohmygoat;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.ohmygoat.common.BreakMemory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class OhMyGoatPlatformStuff {

    @ExpectPlatform
    public static BreakMemory getBreakMemory(ServerLevel level, BlockPos toBreakPos, BlockState toBreak) {
        throw new AssertionError();
    }
}
