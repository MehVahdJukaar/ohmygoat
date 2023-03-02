package net.mehvahdjukaar.goated.common;

import com.mojang.serialization.Dynamic;
import net.mehvahdjukaar.goated.Goated;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Geep extends Animal implements Shearable {
    public static final EntityDimensions LONG_JUMPING_DIMENSIONS = Goat.LONG_JUMPING_DIMENSIONS;

    protected static final List<SensorType<? extends Sensor<? super Geep>>> SENSOR_TYPES = List.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_ITEMS,
            Goated.GEEP_ADULT_SENSOR.get(),
            SensorType.HURT_BY,
            SensorType.GOAT_TEMPTATIONS
    );
    protected static final List<MemoryModuleType<?>> MEMORY_TYPES = List.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.ATE_RECENTLY,
            MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS,
            MemoryModuleType.LONG_JUMP_MID_JUMP,
            MemoryModuleType.TEMPTING_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_ADULT,
            MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
            MemoryModuleType.IS_TEMPTED,
            MemoryModuleType.IS_PANICKING
    );
    public static final int GOAT_FALL_DAMAGE_REDUCTION = 10;
    public static final EntityDataAccessor<Boolean> IS_SHEARED = SynchedEntityData.defineId(Geep.class, EntityDataSerializers.BOOLEAN);

    private int lowerHeadTick;
    private int eatAnimationTick;

    public Geep(EntityType<? extends Geep> entityType, Level level) {
        super(entityType, level);
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        var r = super.getDefaultLootTable();
        if (!this.isSheared()) r = new ResourceLocation(r.getNamespace(), r.getPath() + "_wool");
        return r;
    }

    @Override
    protected Brain.Provider<Geep> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<Geep> makeBrain(Dynamic<?> dynamic) {
        return GeepAI.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    protected int calculateFallDamage(float fallDistance, float damageMultiplier) {
        return super.calculateFallDamage(fallDistance, damageMultiplier) - GOAT_FALL_DAMAGE_REDUCTION;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return Goated.AMBIENT_SOUND.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return Goated.HURT_SOUND.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return Goated.DEATH_SOUND.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.GOAT_STEP, 0.15F, 1.0F);
    }

    protected SoundEvent getMilkingSound() {
        return Goated.MILK_SOUND.get();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return (AgeableMob) this.getType().create(level);
    }

    @Override
    protected void customServerAiStep() {
        this.getBrain().tick((ServerLevel) this.level, this);

        GeepAI.updateActivity(this);
        //on server??
        //  this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();

        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        if (this.level.isClientSide) {
            this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
        }

        super.aiStep();
    }

    public void handleEntityEvent(byte id) {
        if (id == 10) {
            this.eatAnimationTick = 40;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void ate() {
        super.ate();
        this.setSheared(false);
        if (this.isBaby()) {
            this.ageUp(60);
        }
    }

    @Override
    public Brain<Geep> getBrain() {
        return (Brain<Geep>) super.getBrain();
    }

    @Override
    public int getMaxHeadYRot() {
        return 15;
    }

    @Override
    public void setYHeadRot(float yHeadRot) {
        int i = this.getMaxHeadYRot();
        float f = Mth.degreesDifference(this.yBodyRot, yHeadRot);
        float g = Mth.clamp(f, (-i), i);
        super.setYHeadRot(this.yBodyRot + g);
    }

    @Override
    public SoundEvent getEatingSound(ItemStack stack) {
        return Goated.EAT_SOUND.get();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(Items.BUCKET) && !this.isBaby()) {
            player.playSound(this.getMilkingSound(), 1.0F, 1.0F);
            ItemStack itemStack2 = ItemUtils.createFilledResult(itemStack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, itemStack2);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        } else if (itemStack.is(Items.SHEARS)) {
            if (!this.level.isClientSide && this.readyForShearing()) {
                this.shear(SoundSource.PLAYERS);
                this.gameEvent(GameEvent.SHEAR, player);
                itemStack.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(hand));
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        } else {
            InteractionResult interactionResult = super.mobInteract(player, hand);
            if (interactionResult.consumesAction() && this.isFood(itemStack)) {
                this.level.playSound(null, this, this.getEatingSound(itemStack), SoundSource.NEUTRAL, 1.0F, Mth.randomBetween(this.level.random, 0.8F, 1.2F));
            }

            return interactionResult;
        }
    }

    @Override
    public void setInLove(@Nullable Player player) {
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        RandomSource randomSource = level.getRandom();
        GeepAI.initMemories(this, randomSource);
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return pose == Pose.LONG_JUMPING ? LONG_JUMPING_DIMENSIONS.scale(this.getScale()) : super.getDimensions(pose);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Sheared", this.isSheared());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSheared(compound.getBoolean("Sheared"));
    }

    public boolean isSheared() {
        return this.entityData.get(IS_SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.entityData.set(IS_SHEARED, sheared);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_SHEARED, false);
    }

    public float getRammingXHeadRot() {
        return this.lowerHeadTick / 20.0F * 30.0F * (float) (Math.PI / 180.0);
    }

    public float getHeadEatPositionScale(float partialTick) {
        if (this.eatAnimationTick <= 0) {
            return 0.0F;
        } else if (this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36) {
            return 1.0F;
        } else {
            return this.eatAnimationTick < 4
                    ? (this.eatAnimationTick - partialTick) / 4.0F
                    : -((this.eatAnimationTick - 40) - partialTick) / 4.0F;
        }
    }

    public float getHeadEatAngleScale(float partialTick) {
        if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
            float f = ((this.eatAnimationTick - 4) - partialTick) / 32.0F;
            return ((float) (Math.PI / 5)) + 0.21991149F * Mth.sin(f * 28.7F);
        } else {
            if (this.eatAnimationTick > 0) {
                return (float) (Math.PI / 5);
            } else return this.getViewXRot(partialTick) * (float) (Math.PI / 180.0);
        }
    }


    @Override
    public boolean readyForShearing() {
        return this.isAlive() && !this.isSheared() && !this.isBaby();
    }

    @Override
    public void shear(SoundSource source) {
        this.level.playSound(null, this, SoundEvents.SHEEP_SHEAR, source, 1.0F, 1.0F);
        this.setSheared(true);
        int i = 1 + this.random.nextInt(3);
        for (int j = 0; j < i; ++j) {
            ItemEntity itementity = this.spawnAtLocation(Items.WHITE_WOOL, 1);
            if (itementity != null) {
                itementity.setDeltaMovement(
                        itementity.getDeltaMovement()
                                .add(
                                        ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F),
                                        (this.random.nextFloat() * 0.05F),
                                        ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F)
                                )
                );
            }
        }
    }


    public static boolean checkGoatSpawnRules(EntityType<? extends Animal> goat, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(BlockTags.GOATS_SPAWNABLE_ON) && isBrightEnoughToSpawn(level, pos);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 9.0)
                .add(Attributes.MOVEMENT_SPEED, 0.215F);
    }
}
