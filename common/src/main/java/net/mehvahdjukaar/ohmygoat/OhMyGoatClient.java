package net.mehvahdjukaar.ohmygoat;

import net.mehvahdjukaar.moonlight.api.platform.ClientPlatformHelper;
import net.mehvahdjukaar.ohmygoat.client.BarbaricHelmetModel;
import net.mehvahdjukaar.ohmygoat.client.GeepModel;
import net.mehvahdjukaar.ohmygoat.client.GeepRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;

public class OhMyGoatClient {

    public static final ModelLayerLocation GEEP = loc("geep");
    public static final ModelLayerLocation GEEP_FUR = loc("geep_fur");
    public static final ModelLayerLocation BARBARIC_HELMET = loc("barbaric_helmet");

    public static void init() {
        ClientPlatformHelper.addModelLayerRegistration(OhMyGoatClient::registerLayers);
        ClientPlatformHelper.addEntityRenderersRegistration(OhMyGoatClient::registerEntityRenderers);
    }

    public static void setup() {
    }

    private static ModelLayerLocation loc(String name) {
        return new ModelLayerLocation(OhMyGoat.res(name), name);
    }


    private static void registerLayers(ClientPlatformHelper.ModelLayerEvent event) {
        event.register(BARBARIC_HELMET, BarbaricHelmetModel::createBodyLayer);
        event.register(GEEP, GeepModel::createBodyLayer);
        event.register(GEEP_FUR, GeepModel::createFurLayer);
    }

    private static void registerEntityRenderers(ClientPlatformHelper.EntityRendererEvent event) {
        event.register(OhMyGoat.GEEP.get(), GeepRenderer::new);
    }


}
