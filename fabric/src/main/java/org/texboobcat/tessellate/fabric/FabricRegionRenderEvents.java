package org.texboobcat.tessellate.fabric;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import org.texboobcat.tessellate.client.RegionOverlay;

public final class FabricRegionRenderEvents {

    private FabricRegionRenderEvents() {
    }

    public static void init() {
        LevelExtractionEvents.END_EXTRACTION.register(context -> RegionOverlay.renderWorld(
            context.camera(), context.deltaTracker()));
    }
}
