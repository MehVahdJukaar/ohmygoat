package net.mehvahdjukaar.goated.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.mehvahdjukaar.goated.Goated;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;

public class GoatedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Goated.commonInit();
        ServerTickEvents.START_SERVER_TICK.register(FabricRamBreakingHandler::tick);

        if (PlatHelper.getPhysicalSide().isClient()) {
            ClientHelper.addClientSetup(GoatHelmetArmorRenderer::register);
        }
    }


}
