package net.mehvahdjukaar.goated.mixins.forge;

import net.mehvahdjukaar.goated.common.RamBlock;
import net.mehvahdjukaar.moonlight.api.misc.OptionalMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.quark.api.IMagnetMoveAction;

@OptionalMixin("vazkii.quark.api.IMagnetMoveAction")
@Mixin(RamBlock.class)
public abstract class SelfCompatQuarkRamBlockMixin implements IMagnetMoveAction {

    @Override
    public void onMagnetMoved(Level level, BlockPos pos, Direction direction, BlockState state, BlockEntity blockEntity) {
        RamBlock.tryBreakAfterMove(state, level, pos, direction);
    }
}
