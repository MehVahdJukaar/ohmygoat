package net.mehvahdjukaar.ohmygoat.client;

import net.mehvahdjukaar.ohmygoat.OhMyGoat;
import net.mehvahdjukaar.ohmygoat.OhMyGoatClient;
import net.mehvahdjukaar.ohmygoat.common.Geep;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SheepFurLayer;
import net.minecraft.resources.ResourceLocation;

public class GeepRenderer extends MobRenderer<Geep, GeepModel<Geep>> {
    private static final ResourceLocation LOCATION = OhMyGoat.res("textures/entity/geep/geep.png");

    public GeepRenderer(EntityRendererProvider.Context context) {
        super(context, new GeepModel<>(context.bakeLayer(OhMyGoatClient.GEEP)), 0.7F);
        this.addLayer(new GeepFurLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(Geep goat) {
        return LOCATION;
    }
}
