package org.texboobcat.tessellate.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import org.texboobcat.tessellate.Tessellate;
import org.texboobcat.tessellate.client.RegionOverlay;

@EventBusSubscriber(modid = Tessellate.MODID, value = Dist.CLIENT)
public final class NeoForgeRegionRenderEvents {

    private NeoForgeRegionRenderEvents() {
    }

    @SubscribeEvent
    public static void renderWorld(ExtractLevelRenderStateEvent event) {
        RegionOverlay.renderWorld(event.getCamera(), event.getDeltaTracker());
    }
}
