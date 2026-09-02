package net.mehvahdjukaar.goated.common;

import net.mehvahdjukaar.goated.Goated;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BarbaricHelmetItem extends ArmorItem {

    protected final MutableComponent TOOLTIP = Component.translatable("tooltip.goated.barbaric_helmet")
            .withStyle(ChatFormatting.GRAY);

    public BarbaricHelmetItem(Properties properties) {
        super(Goated.BARBARIC_ARMOR_MATERIAL.getHolder(), Type.HELMET, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(TOOLTIP);
    }

    //@Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        applyEffects(player);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (entity instanceof LivingEntity le && le.getItemBySlot(EquipmentSlot.HEAD)==stack) {
            applyEffects(le);
        }
    }

    private static void applyEffects(LivingEntity le) {
        float i = le.getHealth();
        if (i <= SPEED_2_THRESHOLD) {
            le.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1, false, false, true));
        } else if (i < SPEED_1_THRESHOLD) {
            le.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0, false, false, true));
        }

        if (i <= STRENGTH_2_THRESHOLD) {
            le.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 1, false, false, true));
        } else if (i < STRENGTH_1_THRESHOLD) {
            le.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 0, false, false, true));
        }
    }

    private static final float SPEED_1_THRESHOLD = 6;
    private static final float STRENGTH_1_THRESHOLD = 5;
    private static final float SPEED_2_THRESHOLD = 2f;
    private static final float STRENGTH_2_THRESHOLD = 3f;
}
