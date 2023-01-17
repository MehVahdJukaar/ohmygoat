package net.mehvahdjukaar.goated.mixins.forge;

import net.mehvahdjukaar.goated.common.Geep;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.IForgeShearable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(Geep.class)
public abstract class SelfGeepMixin extends Animal implements Shearable, IForgeShearable {

    protected SelfGeepMixin(EntityType<? extends Animal> arg, Level arg2) {
        super(arg, arg2);
    }

    @Override
    public boolean isShearable(@NotNull ItemStack item, Level world, BlockPos pos) {
        return this.readyForShearing();
    }

    @Override
    @NotNull
    public List<ItemStack> onSheared(@Nullable Player player, @NotNull ItemStack item, Level world, BlockPos pos, int fortune) {
        world.playSound(null, this, SoundEvents.SHEEP_SHEAR, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
        this.gameEvent(GameEvent.SHEAR, player);
        if (world.isClientSide) {
            return Collections.emptyList();
        } else {
            this.getEntityData().set(Geep.IS_SHEARED, true);
            int i = 1 + this.random.nextInt(3);
            List<ItemStack> items = new ArrayList<>();
            for (int j = 0; j < i; ++j) {
                items.add(new ItemStack(Items.WHITE_WOOL));
            }
            return items;
        }
    }
}
