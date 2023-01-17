package net.mehvahdjukaar.goated.common;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.Animal;

import java.util.Optional;

public class MakeLoveWithSheepBehavior extends Behavior<Animal> {
    private static final int BREED_RANGE = 3;
    private static final int MIN_DURATION = 60;
    private static final int MAX_DURATION = 110;
    private final EntityType<? extends Animal> partnerType = EntityType.SHEEP;
    private final float speedModifier;
    private long spawnChildAtTime;

    public MakeLoveWithSheepBehavior(float f) {
        super(
                ImmutableMap.of(
                        MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
                        MemoryStatus.VALUE_PRESENT,
                        MemoryModuleType.BREED_TARGET,
                        MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.WALK_TARGET,
                        MemoryStatus.REGISTERED,
                        MemoryModuleType.LOOK_TARGET,
                        MemoryStatus.REGISTERED
                ),
                MAX_DURATION
        );
        this.speedModifier = f;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Animal owner) {
        return owner.isInLove() && this.findValidBreedPartner(owner).isPresent();
    }

    @Override
    protected void start(ServerLevel level, Animal entity, long gameTime) {
        Animal animal = this.findValidBreedPartner(entity).get();
        entity.getBrain().setMemory(MemoryModuleType.BREED_TARGET, animal);
        animal.getBrain().setMemory(MemoryModuleType.BREED_TARGET, entity);
        BehaviorUtils.lockGazeAndWalkToEachOther(entity, animal, this.speedModifier);
        int i = MIN_DURATION + entity.getRandom().nextInt(MAX_DURATION - MIN_DURATION);
        this.spawnChildAtTime = gameTime + i;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Animal entity, long gameTime) {
        if (!this.hasBreedTargetOfRightType(entity)) {
            return false;
        } else {
            Animal animal = this.getBreedTarget(entity);
            return animal.isAlive() && canMate(entity, animal) && BehaviorUtils.entityIsVisible(entity.getBrain(), animal) && gameTime <= this.spawnChildAtTime;
        }
    }

    private static boolean canMate(Animal entity, Animal animal) {
        return entity.isInLove() && animal.isInLove();
    }

    @Override
    protected void tick(ServerLevel level, Animal owner, long gameTime) {
        Animal animal = this.getBreedTarget(owner);
        BehaviorUtils.lockGazeAndWalkToEachOther(owner, animal, this.speedModifier);
        if (owner.closerThan(animal, BREED_RANGE)) {
            if (gameTime >= this.spawnChildAtTime) {
                BreedWithGoatGoal.spawnChildFromBreeding(level, owner, animal);
                owner.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
                animal.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
            }
        }
    }

    @Override
    protected void stop(ServerLevel level, Animal entity, long gameTime) {
        entity.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        entity.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        this.spawnChildAtTime = 0L;
    }

    private Animal getBreedTarget(Animal animal) {
        return (Animal) animal.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
    }

    private boolean hasBreedTargetOfRightType(Animal animal) {
        Brain<?> brain = animal.getBrain();
        return brain.hasMemoryValue(MemoryModuleType.BREED_TARGET)
                && (brain.getMemory(MemoryModuleType.BREED_TARGET).get()).getType() == this.partnerType;
    }

    private Optional<? extends Animal> findValidBreedPartner(Animal animal) {
        return animal.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).flatMap(t ->
                        t.findClosest(livingEntity -> livingEntity.getType() == this.partnerType &&
                                livingEntity instanceof Animal animal2 && canMate(animal, animal2)))
                .map(Animal.class::cast);
    }
}
