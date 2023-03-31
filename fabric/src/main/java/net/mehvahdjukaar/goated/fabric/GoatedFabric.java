package net.mehvahdjukaar.goated.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.mehvahdjukaar.goated.Goated;
import net.mehvahdjukaar.goated.GoatedClient;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.fabric.FabricSetupCallbacks;

public class GoatedFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        Goated.commonInit();

        if (PlatformHelper.getEnv().isClient()) {
            FabricSetupCallbacks.CLIENT_SETUP.add(GoatedClient::init);
            FabricSetupCallbacks.CLIENT_SETUP.add(GoatHelmetArmorRenderer::register);
        }

        FabricSetupCallbacks.COMMON_SETUP.add(Goated::commonSetup);

        ServerTickEvents.START_SERVER_TICK.register(FabricRamBreakingHandler::tick);

    }


}
