package net.mehvahdjukaar.ohmygoat.client;

import net.mehvahdjukaar.ohmygoat.common.Geep;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

public class GeepModel<T extends Geep> extends QuadrupedModel<T> {
    private float headXRot;

    public GeepModel(ModelPart modelPart) {
        super(modelPart, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);
    }

    public static LayerDefinition createBodyLayer() {
        int lefH = 0;
        CubeDeformation legDef = CubeDeformation.NONE;
        CubeDeformation bodyDef = CubeDeformation.NONE;

        return createLayer(lefH, legDef, bodyDef);
    }

    @NotNull
    private static LayerDefinition createLayer(int lefOffset, CubeDeformation legDef, CubeDeformation bodyDef) {
        MeshDefinition meshDefinition = new MeshDefinition();

        PartDefinition partDefinition = meshDefinition.getRoot();

        CubeListBuilder cubeListBuilder = CubeListBuilder.create().texOffs(0, 0)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10f - lefOffset, 4.0F, legDef);
        partDefinition.addOrReplaceChild("right_hind_leg", cubeListBuilder, PartPose.offset(-3.0F, 14, 7.0F));
        partDefinition.addOrReplaceChild("left_hind_leg", cubeListBuilder, PartPose.offset(3.0F, 14, 7.0F));
        partDefinition.addOrReplaceChild("right_front_leg", cubeListBuilder, PartPose.offset(-3.0F, 14, -5.0F));
        partDefinition.addOrReplaceChild("left_front_leg", cubeListBuilder, PartPose.offset(3.0F, 14, -5.0F));

        var head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(56, 0)
                        .addBox(-5.0F, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(56, 3)
                        .addBox(3.0F, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 5.0F, -8.0F));

        head.addOrReplaceChild("goat_head", CubeListBuilder.create().texOffs(32, 0)
                        .addBox(-3.0F, -6.0F, -10.0F, 6.0F, 6.0F, 10.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.6109F, 0.0F, 0.0F));

        partDefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, -0.0F, -8.0F, 8.0F, 9, 16, bodyDef),
                PartPose.offset(0.0F, 5.0F, 0.0F)
        );
        return LayerDefinition.create(meshDefinition, 64, 32);
    }

    public static LayerDefinition createFurLayer() {
        int lefH = 4;
        CubeDeformation legDef = new CubeDeformation(0.5f);
        CubeDeformation bodyDef = new CubeDeformation(1.75f);

        return createLayer(lefH, legDef, bodyDef);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        this.head.y = 5 + entity.getHeadEatPositionScale(partialTick) * 9.0F;
        this.headXRot = entity.getHeadEatAngleScale(partialTick);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.head.xRot = this.headXRot;
    }
}

