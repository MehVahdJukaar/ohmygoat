package net.mehvahdjukaar.goated.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.goated.Goated;
import net.mehvahdjukaar.goated.GoatedClient;
import net.mehvahdjukaar.moonlight.fabric.MLFabricSetupCallbacks;

public class GoatedFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        Goated.commonInit();

        if (PlatHelper.getPhysicalSide().isClient()) {
            MLFabricSetupCallbacks.CLIENT_SETUP.add(GoatedClient::init);
            MLFabricSetupCallbacks.CLIENT_SETUP.add(GoatHelmetArmorRenderer::register);
        }

        MLFabricSetupCallbacks.COMMON_SETUP.add(Goated::commonSetup);

        ServerTickEvents.START_SERVER_TICK.register(FabricRamBreakingHandler::tick);

    }


}
