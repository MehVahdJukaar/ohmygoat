package net.mehvahdjukaar.ohmygoat.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.ohmygoat.OhMyGoat;
import net.mehvahdjukaar.ohmygoat.OhMyGoatClient;
import net.mehvahdjukaar.ohmygoat.common.Geep;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class GeepFurLayer extends RenderLayer<Geep, GeepModel<Geep>> {
    private static final ResourceLocation LOCATION = OhMyGoat.res("textures/entity/geep/geep_fur.png");
    private final GeepModel<Geep> model;

    public GeepFurLayer(RenderLayerParent<Geep, GeepModel<Geep>> renderLayerParent, EntityModelSet entityModelSet) {
        super(renderLayerParent);
        this.model = new GeepModel<>(entityModelSet.bakeLayer(OhMyGoatClient.GEEP_FUR));
    }

    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, Geep livingEntity, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!livingEntity.isSheared()) {
            if (livingEntity.isInvisible()) {
                Minecraft minecraft = Minecraft.getInstance();
                boolean bl = minecraft.shouldEntityAppearGlowing(livingEntity);
                if (bl) {
                    (this.getParentModel()).copyPropertiesTo(this.model);
                    this.model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTicks);
                    this.model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                    VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.outline(LOCATION));
                    this.model
                            .renderToBuffer(
                                    matrixStack, vertexConsumer, packedLight, LivingEntityRenderer.getOverlayCoords(livingEntity, 0.0F), 0.0F, 0.0F, 0.0F, 1.0F
                            );
                }
            } else {
                float g;
                float h;
                float n;
                if (livingEntity.hasCustomName() && "jeb_".equals(livingEntity.getName().getString())) {
                    int i = 25;
                    int j = livingEntity.tickCount / 25 + livingEntity.getId();
                    int k = DyeColor.values().length;
                    int l = j % k;
                    int m = (j + 1) % k;
                    float f = ((float) (livingEntity.tickCount % 25) + partialTicks) / 25.0F;
                    float[] fs = Sheep.getColorArray(DyeColor.byId(l));
                    float[] gs = Sheep.getColorArray(DyeColor.byId(m));
                    g = fs[0] * (1.0F - f) + gs[0] * f;
                    h = fs[1] * (1.0F - f) + gs[1] * f;
                    n = fs[2] * (1.0F - f) + gs[2] * f;
                } else {
                    g = 1;
                    h = 1;
                    n = 2;
                }

                coloredCutoutModelCopyLayerRender(
                        this.getParentModel(),
                        this.model,
                        LOCATION,
                        matrixStack,
                        buffer,
                        packedLight,
                        livingEntity,
                        limbSwing,
                        limbSwingAmount,
                        ageInTicks,
                        netHeadYaw,
                        headPitch,
                        partialTicks,
                        g,
                        h,
                        n
                );
            }
        }
    }
}
