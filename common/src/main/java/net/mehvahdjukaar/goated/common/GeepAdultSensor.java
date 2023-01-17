package net.mehvahdjukaar.goated.common;


import com.google.common.collect.ImmutableSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.Optional;
import java.util.Set;

public class GeepAdultSensor extends Sensor<AgeableMob> {

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }

    @Override
    protected void doTick(ServerLevel level, AgeableMob entity) {
        entity.getBrain()
                .getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                .ifPresent(nearestVisibleLivingEntities -> this.setNearestVisibleAdult(entity, nearestVisibleLivingEntities));
    }

    private void setNearestVisibleAdult(AgeableMob mob, NearestVisibleLivingEntities nearbyEntities) {
        Optional<AgeableMob> optional = nearbyEntities.findClosest(livingEntity -> {
                    if (livingEntity.isBaby()) return false;
                    var t = livingEntity.getType();
                    return t == EntityType.SHEEP || t == EntityType.GOAT;
                })
                .map(AgeableMob.class::cast);
        mob.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT, optional);
    }
}

