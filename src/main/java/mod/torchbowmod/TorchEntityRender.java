package mod.torchbowmod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import static mod.torchbowmod.TorchBowMod.MODID;

@Environment(EnvType.CLIENT)
public class TorchEntityRender extends ProjectileEntityRenderer {

    public TorchEntityRender(Context entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public Identifier getTexture(Entity entity) {
        return Identifier.of(MODID, "textures/entity/torch.png");
    }
}
