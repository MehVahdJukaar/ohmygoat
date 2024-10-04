package net.mehvahdjukaar.goated.mixins.neoforge;

import com.google.common.base.Suppliers;
import net.mehvahdjukaar.goated.Goated;
import net.mehvahdjukaar.goated.GoatedClient;
import net.mehvahdjukaar.goated.client.BarbaricHelmetModel;
import net.mehvahdjukaar.goated.common.BarbaricHelmetItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(BarbaricHelmetItem.class)
public abstract class SelfBarbarianHelmetMixin extends Item {
    @Unique
    private static final ResourceLocation GOATED_TEXTURE = Goated.res("textures/models/armor/barbaric_helmet.png");

    protected SelfBarbarianHelmetMixin(Properties arg) {
        super(arg);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {

            private final Supplier<HumanoidModel<?>> model = Suppliers.memoize(() -> new BarbaricHelmetModel(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GoatedClient.BARBARIC_HELMET))
            );

            @Override
            public @NotNull Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot,
                                                       HumanoidModel<?> original) {
                var m = model.get();
                ((HumanoidModel)original).copyPropertiesTo(m);
               return m;
            }
        });
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return GOATED_TEXTURE;
    }

}
