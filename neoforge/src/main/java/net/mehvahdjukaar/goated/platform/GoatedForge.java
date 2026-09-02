package net.mehvahdjukaar.goated.platform;

import net.mehvahdjukaar.goated.Goated;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * Author: MehVahdJukaar
 */
@Mod(Goated.MOD_ID)
public class GoatedForge {

    public GoatedForge(IEventBus bus) {
        Goated.commonInit();

        NeoForge.EVENT_BUS.register(this);
        ATTACHMENT_TYPES.register(bus);
    }

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(
            NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Goated.MOD_ID);

    // Serialization via INBTSerializable
    public static final Supplier<AttachmentType<RamBreakingData>> BREAK_DATA = ATTACHMENT_TYPES.register(
            "ram_breaking_progress", () -> AttachmentType.serializable(RamBreakingData::new).build());

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void tickEvent(LevelTickEvent.Post event) {
        Level l = event.getLevel();
        if (!l.isClientSide && l instanceof ServerLevel level) {
            level.getData(BREAK_DATA).validateAll(level);
        }
    }
}

