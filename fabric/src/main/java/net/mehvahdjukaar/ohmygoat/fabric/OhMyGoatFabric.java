package net.mehvahdjukaar.ohmygoat.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.mehvahdjukaar.ohmygoat.OhMyGoat;
import net.mehvahdjukaar.ohmygoat.OhMyGoatClient;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.fabric.FabricSetupCallbacks;
import net.minecraft.server.MinecraftServer;

public class OhMyGoatFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        OhMyGoat.commonInit();

        if (PlatformHelper.getEnv().isClient()) {
            FabricSetupCallbacks.CLIENT_SETUP.add(OhMyGoatClient::init);
            FabricSetupCallbacks.COMMON_SETUP.add(
                    ()->ArmorRenderer.register(new GoatHelmetArmorRenderer(), OhMyGoat.BARBARIAN_HELMET.get()));
        }

        FabricSetupCallbacks.COMMON_SETUP.add(OhMyGoat::commonSetup);

        ServerTickEvents.START_SERVER_TICK.register(FabricRamBreakingHandler::tick);

    }


}
