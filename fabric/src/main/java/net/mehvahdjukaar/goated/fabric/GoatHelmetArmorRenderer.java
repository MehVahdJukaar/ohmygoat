package net.mehvahdjukaar.goated.fabric;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.mehvahdjukaar.goated.Goated;
import net.mehvahdjukaar.goated.GoatedClient;
import net.mehvahdjukaar.goated.client.BarbaricHelmetModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class GoatHelmetArmorRenderer implements ArmorRenderer {

    private final Supplier<HumanoidModel<?>> model = Suppliers.memoize(() -> new BarbaricHelmetModel(
            Minecraft.getInstance().getEntityModels().bakeLayer(GoatedClient.BARBARIC_HELMET))
    );

    private static final ResourceLocation LOCATION = new ResourceLocation("goated:textures/models/armor/barbaric_helmet.png");

    @Override
    public void render(PoseStack poseStack, MultiBufferSource vertexConsumers, ItemStack stack, LivingEntity entity,
                       EquipmentSlot slot, int light, HumanoidModel<LivingEntity> original) {
        ((HumanoidModel)original).copyPropertiesTo(model.get());
        model.get().renderToBuffer(poseStack, vertexConsumers.getBuffer(RenderType.entityCutout(
                LOCATION
        )), light, OverlayTexture.NO_OVERLAY, 1, 1, 1f, 1);
    }

    public static void register() {
        ArmorRenderer.register(new GoatHelmetArmorRenderer(), Goated.BARBARIC_HELMET.get());
    }
}
