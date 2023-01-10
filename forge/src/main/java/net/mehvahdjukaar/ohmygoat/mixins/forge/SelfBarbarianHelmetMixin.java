package net.mehvahdjukaar.ohmygoat.mixins.forge;

import com.google.common.base.Suppliers;
import net.mehvahdjukaar.ohmygoat.OhMyGoatClient;
import net.mehvahdjukaar.ohmygoat.client.BarbaricHelmetModel;
import net.mehvahdjukaar.ohmygoat.common.BarbaricHelmetItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(BarbaricHelmetItem.class)
public abstract class SelfBarbarianHelmetMixin extends Item {

    protected SelfBarbarianHelmetMixin(Properties arg) {
        super(arg);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {

            private final Supplier<HumanoidModel<?>> model = Suppliers.memoize(() -> new BarbaricHelmetModel(
                    Minecraft.getInstance().getEntityModels().bakeLayer(OhMyGoatClient.BARBARIC_HELMET))
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
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "ohmygoat:textures/models/armor/barbaric_helmet.png";
    }
}
