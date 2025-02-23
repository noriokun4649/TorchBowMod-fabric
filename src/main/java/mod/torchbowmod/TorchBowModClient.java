package mod.torchbowmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import static mod.torchbowmod.TorchBowMod.*;

@Environment(EnvType.CLIENT)
public class TorchBowModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(TORCH, TorchEntityRender::new);
    }
}

