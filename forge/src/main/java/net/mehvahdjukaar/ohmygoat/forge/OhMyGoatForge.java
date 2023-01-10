package net.mehvahdjukaar.ohmygoat.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.ohmygoat.OhMyGoat;
import net.mehvahdjukaar.ohmygoat.OhMyGoatClient;
import net.mehvahdjukaar.ohmygoat.client.BarbaricHelmetModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.data.ForgeLootTableProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Author: MehVahdJukaar
 */
@Mod(OhMyGoat.MOD_ID)
public class OhMyGoatForge {

    public OhMyGoatForge() {
        OhMyGoat.commonInit();

        if (PlatformHelper.getEnv().isClient()) {
            OhMyGoatClient.init();
        }
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(OhMyGoatForge::setup);
    }

    public static void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(OhMyGoat::commonSetup);
    }


    public static final Capability<RamBreakingCap> RAM_BREAK_CAP = CapabilityManager.get(new CapabilityToken<>() {});


    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(RamBreakingCap.class);
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Level> event) {
        if(event.getObject() instanceof ServerLevel serverLevel) {
            RamBreakingCap capability = new RamBreakingCap(serverLevel);
            event.addCapability(OhMyGoat.res("ram_break_progress"), capability);
            event.addListener(capability::invalidate);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void tickEvent(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel level) {
            level.getCapability(RAM_BREAK_CAP).ifPresent(RamBreakingCap::validateAll);
        }
    }


}

