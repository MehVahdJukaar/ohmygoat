package net.mehvahdjukaar.goated.common;

import net.mehvahdjukaar.goated.Goated;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BreedWithGoatGoal extends BreedGoal {
    private static final double RANGE = 4.0;
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat()
            .range(RANGE).ignoreLineOfSight();

    public BreedWithGoatGoal(Sheep animal, double d) {
        super(animal, d, Goat.class);
    }

    @Override
    public boolean canUse() {
        if (!this.animal.isInLove()) {
            return false;
        } else {
            this.partner = this.getFreePartner();
            return this.partner != null;
        }
    }


    @Nullable
    private Animal getFreePartner() {
        List<? extends Animal> list = this.level
                .getNearbyEntities(Goat.class, PARTNER_TARGETING, this.animal,
                        this.animal.getBoundingBox().inflate(RANGE));
        double d = Double.MAX_VALUE;
        Animal animal = null;

        for (Animal animal2 : list) {
            if (this.animal.isInLove() && animal2.isInLove() && this.animal.distanceToSqr(animal2) < d) {
                animal = animal2;
                d = this.animal.distanceToSqr(animal2);
            }
        }

        return animal;
    }

    @Override
    protected void breed() {
        spawnChildFromBreeding((ServerLevel) this.level, this.animal, this.partner);
    }

    public static void spawnChildFromBreeding(ServerLevel level, Animal mother, Animal father) {
        AgeableMob ageableMob = Goated.GEEP.get().create(level);
        if (ageableMob != null) {
            ServerPlayer serverPlayer = mother.getLoveCause();
            if (serverPlayer == null && father.getLoveCause() != null) {
                serverPlayer = father.getLoveCause();
            }

            if (serverPlayer != null) {
                serverPlayer.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverPlayer, mother, father, ageableMob);
                Advancement advancement = level.getServer().getAdvancements()
                        .getAdvancement(new ResourceLocation("goated:husbandry/breed_a_geep"));
                if (advancement != null) {
                    if (!serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone()) {
                        serverPlayer.getAdvancements().award(advancement, "unlock");
                    }
                }
            }

            mother.setAge(6000);
            father.setAge(6000);
            mother.resetLove();
            father.resetLove();
            ageableMob.setBaby(true);
            ageableMob.moveTo(mother.getX(), mother.getY(), mother.getZ(), 0.0F, 0.0F);
            level.addFreshEntityWithPassengers(ageableMob);
            level.broadcastEntityEvent(mother, (byte) 18);
            if (level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                level.addFreshEntity(new ExperienceOrb(level, mother.getX(), mother.getY(), mother.getZ(), mother.getRandom().nextInt(7) + 1));
            }
        }
    }
}