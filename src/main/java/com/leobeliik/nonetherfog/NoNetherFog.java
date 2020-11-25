package com.leobeliik.nonetherfog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("nonetherfog")
public class NoNetherFog {

    private static KeyBinding zoom;
    private double maxZoom = 7D;
    private double zoomFOV = maxZoom;
    private double defaultFOV;

    public NoNetherFog() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
    }

    private void clientInit(final FMLClientSetupEvent event) {
        zoom = new KeyBinding(
                "Zoom key",
                KeyConflictContext.IN_GAME,
                InputMappings.INPUT_INVALID,
                "key.categories.misc");
        ClientRegistry.registerKeyBinding(zoom);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderFog(EntityViewRenderEvent.FogDensity event) {
        if (event.getInfo().getRenderViewEntity().world.getDimensionKey().getLocation().getPath().equals("the_nether")) {
            event.setDensity(0.005F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onFOVModifier(EntityViewRenderEvent.FOVModifier event) {
        defaultFOV = event.getFOV();
        if (NoNetherFog.isZooming()) {
            Minecraft.getInstance().gameSettings.smoothCamera = true;
            event.setFOV(zoomFOV);
        } else {
            Minecraft.getInstance().gameSettings.smoothCamera = false;
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput(InputEvent.KeyInputEvent event) {}

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onMouseScroll(InputEvent.MouseScrollEvent event) {
        double zoomIncrease = event.getScrollDelta();

        if (NoNetherFog.isZooming()) {
            if (event.getScrollDelta() > 0D) {
                zoomFOV = zoomFOV > maxZoom ? zoomFOV - zoomIncrease : maxZoom;
                event.setCanceled(true);
            } else if (event.getScrollDelta() < 0D) {
                zoomFOV = zoomFOV < defaultFOV ? zoomFOV + zoomIncrease * -1 : defaultFOV;
                event.setCanceled(true);
            }
        }
    }

    private static boolean isZooming() {
        return zoom.isKeyDown();
    }
}