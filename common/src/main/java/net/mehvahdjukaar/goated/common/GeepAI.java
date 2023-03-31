package net.mehvahdjukaar.goated.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.goated.Goated;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Set;

public class GeepAI {
    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(5, 16);
    private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT = 1.25F;
    private static final float SPEED_MULTIPLIER_WHEN_TEMPTED = 1.25F;
    private static final float SPEED_MULTIPLIER_WHEN_PANICKING = 2.0F;
    private static final float SPEED_MULTIPLIER_WHEN_PREPARING_TO_RAM = 1.25F;
    private static final UniformInt TIME_BETWEEN_LONG_JUMPS = UniformInt.of(700, 1400);
    public static final int MAX_LONG_JUMP_HEIGHT = 3;
    public static final int MAX_LONG_JUMP_WIDTH = 4;
    public static final float MAX_JUMP_VELOCITY = 1.5F;
    private static final TargetingConditions RAM_TARGET_CONDITIONS = TargetingConditions.forCombat()
            .selector(
                    livingEntity -> !livingEntity.getType().equals(EntityType.GOAT)
                            && livingEntity.level.getWorldBorder().isWithinBounds(livingEntity.getBoundingBox())
            );

    protected static void initMemories(Geep goat, RandomSource random) {
        goat.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, TIME_BETWEEN_LONG_JUMPS.sample(random));
    }

    protected static Brain<Geep> makeBrain(Brain<Geep> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initLongJumpActivity(brain);
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<Geep> brain) {
        brain.addActivity(
                Activity.CORE,
                0,
                ImmutableList.of(
                        new Swim(0.8F),
                        new AnimalPanic(2.0F),
                        new LookAtTargetSink(45, 90),
                        new MoveToTargetSink(),
                        new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
                        new CountDownCooldownTicks(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS)
                )
        );
    }

    private static void initIdleActivity(Brain<Geep> brain) {
        brain.addActivityWithConditions(
                Activity.IDLE,
                ImmutableList.of(
                        Pair.of(0, new RunSometimes<>(new SetEntityLookTarget(EntityType.PLAYER, 6.0F), UniformInt.of(30, 60))),
                        Pair.of(1, new FollowTemptation(livingEntity -> 1.25F)),
                        Pair.of(2, new BabyFollowAdult<>(ADULT_FOLLOW_RANGE, 1.25F)),
                        Pair.of(3, new GeepEatGrassBehavior()),
                        Pair.of(
                                3,
                                new RunOne<>(
                                        ImmutableList.of(
                                                Pair.of(new RandomStroll(1.0F), 2),
                                                Pair.of(new SetWalkTargetFromLookTarget(1.0F, 3), 2),
                                                Pair.of(new DoNothing(30, 60), 1)
                                        )
                                )
                        )
                ),
                ImmutableSet.of(Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT))
        );
    }

    private static void initLongJumpActivity(Brain<Geep> brain) {
        brain.addActivityWithConditions(
                Activity.LONG_JUMP,
                ImmutableList.of(
                        Pair.of(0, new LongJumpMidJump(TIME_BETWEEN_LONG_JUMPS, SoundEvents.GOAT_STEP)),
                        Pair.of(1,
                                new LongJumpToRandomPos<>(
                                        TIME_BETWEEN_LONG_JUMPS, MAX_LONG_JUMP_HEIGHT, MAX_LONG_JUMP_WIDTH, MAX_JUMP_VELOCITY,
                                        goat -> Goated.LONG_JUMP_SOUND.get()
                                )
                        )
                ),
                ImmutableSet.of(
                        Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT)
                )
        );
    }

    public static void updateActivity(Geep brain) {
        brain.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.LONG_JUMP, Activity.IDLE));
    }

    public static Ingredient getTemptations() {
        return Ingredient.of(Items.WHEAT);
    }
}