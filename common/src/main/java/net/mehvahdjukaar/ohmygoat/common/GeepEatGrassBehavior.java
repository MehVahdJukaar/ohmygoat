package net.mehvahdjukaar.ohmygoat.common;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

import java.util.function.Predicate;

public class GeepEatGrassBehavior extends Behavior<Animal> {
    private static final int EAT_ANIMATION_TICKS = 40;
    private static final Predicate<BlockState> IS_TALL_GRASS = BlockStatePredicate.forBlock(Blocks.GRASS);
    private static final Predicate<BlockState> IS_GRASS_BLOCK = BlockStatePredicate.forBlock(Blocks.GRASS_BLOCK);
    //TODO: fodder

    private static final int MAX_DURATION = 110;

    private int eatAnimationTick;

    public GeepEatGrassBehavior() {
        super(
                ImmutableMap.of(
                        MemoryModuleType.IS_PANICKING,
                        MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.WALK_TARGET,
                        MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.LOOK_TARGET,
                        MemoryStatus.REGISTERED
                ),
                MAX_DURATION
        );
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Animal owner) {
        if (owner.getRandom().nextInt(owner.isBaby() ? 50 : 1000) != 0) {
            return false;
        } else {
            BlockPos blockPos = owner.blockPosition();
            if (IS_TALL_GRASS.test(level.getBlockState(blockPos))) {
                return true;
            } else {
                return IS_GRASS_BLOCK.test(level.getBlockState(blockPos.below()));
            }
        }
    }

    @Override
    protected void start(ServerLevel level, Animal entity, long gameTime) {
        this.eatAnimationTick = EAT_ANIMATION_TICKS;
        level.broadcastEntityEvent(entity, (byte) 10);
        entity.getNavigation().stop();

        //hacky. cope
        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Animal entity, long gameTime) {
        return this.eatAnimationTick > 0;
    }

    @Override
    protected void tick(ServerLevel level, Animal owner, long gameTime) {
        //hacky. cope
        owner.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
        if (this.eatAnimationTick == 4) {
            BlockPos blockPos = owner.blockPosition();
            if (IS_TALL_GRASS.test(level.getBlockState(blockPos))) {
                if (level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    level.destroyBlock(blockPos, false);
                }

                owner.ate();
            } else {
                BlockPos below = blockPos.below();
                var belowState = level.getBlockState(below);
                if (IS_GRASS_BLOCK.test(belowState)) {
                    if (level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                        level.levelEvent(2001, below, Block.getId(belowState));
                        level.setBlock(below, Blocks.DIRT.defaultBlockState(), 2);
                    }

                    owner.ate();
                }
            }
        }
    }

    @Override
    protected void stop(ServerLevel level, Animal entity, long gameTime) {
        this.eatAnimationTick = 0;
    }
}

