package net.mehvahdjukaar.goated;

import net.mehvahdjukaar.goated.client.BarbaricHelmetModel;
import net.mehvahdjukaar.goated.client.GeepModel;
import net.mehvahdjukaar.goated.client.GeepRenderer;
import net.mehvahdjukaar.moonlight.api.platform.ClientPlatformHelper;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class GoatedClient {

    public static final ModelLayerLocation GEEP = loc("geep");
    public static final ModelLayerLocation GEEP_FUR = loc("geep_fur");
    public static final ModelLayerLocation BARBARIC_HELMET = loc("barbaric_helmet");

    public static void init() {
        ClientPlatformHelper.addModelLayerRegistration(GoatedClient::registerLayers);
        ClientPlatformHelper.addEntityRenderersRegistration(GoatedClient::registerEntityRenderers);
    }

    public static void setup() {
    }

    private static ModelLayerLocation loc(String name) {
        return new ModelLayerLocation(Goated.res(name), name);
    }


    private static void registerLayers(ClientPlatformHelper.ModelLayerEvent event) {
        event.register(BARBARIC_HELMET, BarbaricHelmetModel::createBodyLayer);
        event.register(GEEP, GeepModel::createBodyLayer);
        event.register(GEEP_FUR, GeepModel::createFurLayer);
    }

    private static void registerEntityRenderers(ClientPlatformHelper.EntityRendererEvent event) {
        event.register(Goated.GEEP.get(), GeepRenderer::new);
    }


}
