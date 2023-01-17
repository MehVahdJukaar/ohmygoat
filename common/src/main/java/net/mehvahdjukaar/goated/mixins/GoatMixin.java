package net.mehvahdjukaar.goated.mixins;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.goated.common.MakeLoveWithSheepBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.goat.GoatAi;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.Set;

@Mixin(GoatAi.class)
public abstract class GoatMixin {


    @ModifyArg(method = "initIdleActivity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/Brain;addActivityWithConditions(Lnet/minecraft/world/entity/schedule/Activity;Lcom/google/common/collect/ImmutableList;Ljava/util/Set;)V"))
    private static <E extends LivingEntity> ImmutableList<Pair<Integer, ? extends Behavior<? super E>>>
    registerGeepGoal(Activity activity, ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> tasks,
                     Set<Pair<MemoryModuleType<?>, MemoryStatus>> memoryStatuses) {
        ImmutableList.Builder<Pair<Integer, ? extends Behavior<? super E>>> b = ImmutableList.builder();
        b.addAll(tasks);
        b.add(Pair.of(3,(Behavior<? super E>)  new MakeLoveWithSheepBehavior(1)));
        return b.build();
    }
}
