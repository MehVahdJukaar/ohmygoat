package net.mehvahdjukaar.goated.common;

import dev.architectury.injectables.annotations.PlatformOnly;
import net.mehvahdjukaar.goated.Goated;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

    public static final ArmorMaterial ARMOR_MATERIAL = new ArmorMaterial() {

        private final Ingredient repair = Ingredient.of(Items.COPPER_INGOT);
        private final String id = Goated.res("goat").toString();

        @Override
        public int getDurabilityForSlot(EquipmentSlot slot) {
            return 13 * 15;
        }

        @Override
        public int getDefenseForSlot(EquipmentSlot slot) {
            return 2;
        }

        @Override
        public int getEnchantmentValue() {
            return 5;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return repair;
        }

        @Override
        public String getName() {
            return id;
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.02f;
        }
    };

    public BarbaricHelmetItem(Properties properties) {
        super(ARMOR_MATERIAL, EquipmentSlot.HEAD, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        tooltipComponents.add(Component.translatable( "tooltip.goated.barbaric_helmet")
                .withStyle(ChatFormatting.GRAY));
    }

    //@Override
    @PlatformOnly(PlatformOnly.FORGE)
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        applyEffects(player);
    }

    @Override
    @PlatformOnly(PlatformOnly.FABRIC)
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
