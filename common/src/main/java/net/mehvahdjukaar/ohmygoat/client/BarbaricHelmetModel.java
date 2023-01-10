package net.mehvahdjukaar.ohmygoat.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class BarbaricHelmetModel extends HumanoidModel<LivingEntity> {

    public BarbaricHelmetModel(ModelPart root) {
        super(root);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float scale = 10 / 8f;
        this.head.xScale *= scale;
        this.head.yScale *= scale;
        this.head.zScale *= scale;
        this.head.render(poseStack, buffer, packedLight, packedOverlay);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0);
        PartDefinition partDefinition = meshDefinition.getRoot();

        float o = 1f-0.375f;

        PartDefinition head = partDefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F+o, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE),
                PartPose.ZERO
        );
        head.addOrReplaceChild("horn_r", CubeListBuilder.create()
                        .texOffs(0, 24).addBox(-8.0F, -8.0F+o, -1.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                        .texOffs(0, 28).addBox(-8.0F, -6.0F+o, -1.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE),
                PartPose.ZERO);

        head.addOrReplaceChild("horn_l", CubeListBuilder.create()
                        .texOffs(0, 28).addBox(-8.0F, -6.0F+o, -1.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                        .texOffs(0, 24).addBox(-8.0F, -8.0F+o, -1.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE),
                PartPose.rotation(0.0F, 3.1416F, 0.0F));

        return LayerDefinition.create(meshDefinition, 32, 32);
    }


}