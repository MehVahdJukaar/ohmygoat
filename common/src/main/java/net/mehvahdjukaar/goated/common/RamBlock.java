package net.mehvahdjukaar.goated.common;

import net.mehvahdjukaar.goated.GoatedPlatformStuff;
import net.mehvahdjukaar.moonlight.api.block.IPistonMotionReact;
import net.mehvahdjukaar.moonlight.api.block.WaterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RamBlock extends WaterBlock implements IPistonMotionReact {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE_X = Block.box(0.0D, 0.0D, 2.0D, 16.0D, 16.0D, 14.0D);
    private static final VoxelShape SHAPE_Z = Block.box(2.0D, 0.0D, 0.0D, 14.0D, 16.0D, 16.0D);
    private static final float MAX_HARDNESS = 50; //obsidian speed


    public RamBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z ? SHAPE_Z : SHAPE_X;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void onMoved(BlockState state, Level level, BlockPos pos, Direction direction, boolean extending, PistonMovingBlockEntity tile) {
        if (extending && canBreakInDir(state, direction) && level instanceof ServerLevel sl) {
            BlockPos toBreakPos = pos.relative(direction);
            BlockState toBreak = level.getBlockState(toBreakPos);
            if (!toBreak.isAir()) {
                float blockHardness = toBreak.getDestroySpeed(level, toBreakPos);
                level.playSound(null, pos, SoundEvents.GOAT_RAM_IMPACT, SoundSource.BLOCKS, 0.8f, level.random.nextFloat() * 0.25F + 0.65F);

                if (blockHardness < MAX_HARDNESS && blockHardness != -1) {
                    BreakMemory memory = GoatedPlatformStuff.getBreakMemory(sl, toBreakPos, toBreak);
                    memory.setTimestamp(level.getGameTime());
                    float speed = 6;
                    int breakProgress = memory.getBreakProgress();
                    breakProgress += Mth.clamp((int) (speed / blockHardness), 1, 10 - breakProgress);
                    memory.setBreakProgress(breakProgress);
                    if (breakProgress >= 10) {
                        boolean drop = !toBreak.requiresCorrectToolForDrops() || Items.IRON_PICKAXE.isCorrectToolForDrops(toBreak);
                        level.destroyBlock(toBreakPos, drop, null);
                        memory.setTimestamp(0); //this effectively invalidates it
                        level.gameEvent(null, GameEvent.BLOCK_DESTROY, toBreakPos);
                        level.destroyBlockProgress(memory.getBreakerId(), toBreakPos, -1);
                        return; //no sound
                    } else {
                        level.destroyBlockProgress(memory.getBreakerId(), toBreakPos, breakProgress);
                    }
                }
                level.playSound(null, pos, toBreak.getSoundType().getHitSound(), SoundSource.BLOCKS, 1.25f, 0.75f);
            }
        }
    }

    private static boolean canBreakInDir(BlockState state, Direction direction) {
        var axis = direction.getAxis();
        return axis == Direction.Axis.Y || axis == state.getValue(FACING).getAxis();
    }

    @Override
    public boolean ticksWhileMoved() {
        return true;
    }

    @Override
    public void moveTick(BlockState movedState, Level level, BlockPos pos, AABB aabb, PistonMovingBlockEntity tile) {
        if (tile.isExtending()) {
            Direction dir = tile.getDirection();
            float i = 1 - tile.getProgress(0);
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, aabb.move(dir.getStepX() * i, dir.getStepY() * i, dir.getStepZ() * i))) {
                float strength = 1;
                strength *= 1.0 - e.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                if (strength > 0.0) {
                    e.hasImpulse = true;
                    Vec3 vec3 = e.getDeltaMovement();
                    Vec3 impulse = new Vec3(-dir.getStepX(), -dir.getStepY(), -dir.getStepZ()).scale(strength);
                    double dy;
                    if (impulse.y != 0) {
                        dy = vec3.y / 2.0 - impulse.y;
                    } else {
                        dy = e.isOnGround() ? Math.min(0.4, vec3.y / 2.0 + strength) : vec3.y;
                    }
                    //TODO: fix piston bug
                    e.setDeltaMovement(vec3.x / 2.0 - impulse.x, dy, vec3.z / 2.0 - impulse.z);
                    e.hurt(DamageSource.GENERIC, 1);
                }
                //   p.knockback(1+0.5*i, -dir.getStepX(), );
            }
        }
    }
}
