package net.mehvahdjukaar.goated.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.goated.Goated;
import net.mehvahdjukaar.goated.GoatedClient;
import net.mehvahdjukaar.goated.common.Geep;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SheepFurLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class GeepFurLayer extends RenderLayer<Geep, GeepModel<Geep>> {
    private static final ResourceLocation LOCATION = Goated.res("textures/entity/geep/geep_fur.png");
    private final GeepModel<Geep> model;

    public GeepFurLayer(RenderLayerParent<Geep, GeepModel<Geep>> renderLayerParent, EntityModelSet entityModelSet) {
        super(renderLayerParent);
        this.model = new GeepModel<>(entityModelSet.bakeLayer(GoatedClient.GEEP_FUR));
    }

    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, Geep livingEntity, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!livingEntity.isSheared()) {
            if (livingEntity.isInvisible()) {
                Minecraft minecraft = Minecraft.getInstance();
                boolean isGlowing = minecraft.shouldEntityAppearGlowing(livingEntity);
                if (isGlowing) {
                    (this.getParentModel()).copyPropertiesTo(this.model);
                    this.model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTicks);
                    this.model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                    VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.outline(LOCATION));
                    this.model
                            .renderToBuffer(
                                    matrixStack, vertexConsumer, packedLight,
                                    LivingEntityRenderer.getOverlayCoords(livingEntity, 0.0F),
                                    -1
                            );
                }
            } else {
                int color;
                if (livingEntity.hasCustomName() && "jeb_".equals(livingEntity.getName().getString())) {
                    int j = livingEntity.tickCount / 25 + livingEntity.getId();
                    int k = DyeColor.values().length;
                    int l = j % k;
                    int m = (j + 1) % k;
                    float f = ((float)(livingEntity.tickCount % 25) + partialTicks) / 25.0F;
                    int n = Sheep.getColor(DyeColor.byId(l));
                    int o = Sheep.getColor(DyeColor.byId(m));
                    color = FastColor.ARGB32.lerp(f, n, o);
                } else {
                    color = Sheep.getColor(DyeColor.WHITE);
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
                        color
                );
            }
        }
    }
}
