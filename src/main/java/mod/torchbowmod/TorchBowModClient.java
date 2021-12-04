package mod.torchbowmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import static mod.torchbowmod.TorchBowMod.*;

@Environment(EnvType.CLIENT)
public class TorchBowModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricModelPredicateProviderRegistry.register(TORCH_BOW_ITEM, new Identifier("pull"), (itemStack, world, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 20.0F;
        });
        FabricModelPredicateProviderRegistry.register(TORCH_BOW_ITEM, new Identifier("pulling"), (itemStack, world, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
        });
        EntityRendererRegistry.register(TORCH, TorchEntityRender::new);
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

