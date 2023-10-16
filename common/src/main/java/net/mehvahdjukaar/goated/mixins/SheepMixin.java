package net.mehvahdjukaar.goated.mixins;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.mehvahdjukaar.goated.common.BreedWithGoatGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheep.class)
public abstract class SheepMixin extends Animal {

    protected SheepMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "getHeadEatAngleScale", at = @At("HEAD"))
    public void capturePartialTicks(float partialTick, CallbackInfoReturnable<Float> cir,
                                    @Share("partialTicks") LocalFloatRef pt) {
        pt.set(partialTick);
    }

    //fixing vanilla bug
    @Redirect(method = "getHeadEatAngleScale", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Sheep;getXRot()F"),
            require = 0)
    public float fixXRotLerp(Sheep instance,  @Share("partialTicks") LocalFloatRef pt) {
        return instance.getViewXRot(pt.get());
    }

    @Inject(method = "registerGoals", at = @At("HEAD"))
    public void registerGeepGoal(CallbackInfo ci) {
        this.goalSelector.addGoal(4,new BreedWithGoatGoal( (Sheep) (Object)this, 1));
    }
}
