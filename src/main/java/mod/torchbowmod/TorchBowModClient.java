package mod.torchbowmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import static mod.torchbowmod.TorchBowMod.MODID;
import static mod.torchbowmod.TorchBowMod.TORCH;

@Environment(EnvType.CLIENT)
public class TorchBowModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(TORCH, TorchEntityRender::new);
        ClientPlayNetworking.registerGlobalReceiver(new Identifier(MODID, "spawntorch"), (client, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            client.execute(() -> {
                ClientWorld world = client.world;
                PlayerEntity playerEntity = client.player;
                TorchEntity torchEntity = new TorchEntity(world, playerEntity);
                torchEntity.setPos(x, y, z);
                torchEntity.setId(entityId);
                torchEntity.updateTrackedPosition(x, y, z);
                if (world != null) world.addEntity(entityId, torchEntity);
            });
        });
    }
}

