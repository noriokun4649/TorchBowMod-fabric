package mod.torchbowmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.EntityRendererRegistryImpl;

import static mod.torchbowmod.TorchBowMod.TORCH;

@Environment(EnvType.CLIENT)
public class TorchBowModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistryImpl.register(TORCH, TorchEntityRender::new);
    }
}

