package net.mehvahdjukaar.goated.client;

import net.mehvahdjukaar.goated.Goated;
import net.mehvahdjukaar.goated.GoatedClient;
import net.mehvahdjukaar.goated.common.Geep;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GeepRenderer extends MobRenderer<Geep, GeepModel<Geep>> {
    private static final ResourceLocation LOCATION = Goated.res("textures/entity/geep/geep.png");

    public GeepRenderer(EntityRendererProvider.Context context) {
        super(context, new GeepModel<>(context.bakeLayer(GoatedClient.GEEP)), 0.7F);
        this.addLayer(new GeepFurLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(Geep goat) {
        return LOCATION;
    }
}
