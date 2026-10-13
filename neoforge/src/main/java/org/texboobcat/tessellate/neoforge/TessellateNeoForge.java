package org.texboobcat.tessellate.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import org.texboobcat.tessellate.Tessellate;

@Mod(Tessellate.MODID)
public final class TessellateNeoForge {

    public TessellateNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(TessellateNeoForgeConfig::onLoad);
        modContainer.registerConfig(ModConfig.Type.LOCAL, TessellateNeoForgeConfig.SPEC);
        org.texboobcat.tessellate.neoforge.gametest.RegionalChunkGameTests.register(modEventBus);
        Tessellate.init();
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            TessellateNeoForgeClient.init();
        }
    }
}
