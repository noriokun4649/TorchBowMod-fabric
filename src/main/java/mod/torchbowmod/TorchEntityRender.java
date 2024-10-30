package mod.torchbowmod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.state.ArrowEntityRenderState;
import net.minecraft.util.Identifier;

import static mod.torchbowmod.TorchBowMod.MODID;

@Environment(EnvType.CLIENT)
public class TorchEntityRender extends ProjectileEntityRenderer<TorchEntity, ArrowEntityRenderState> {

    public TorchEntityRender(Context entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    protected Identifier getTexture(ArrowEntityRenderState state) {
        return Identifier.of(MODID, "textures/entity/torch.png");
    }

    @Override
    public ArrowEntityRenderState createRenderState() {
        return new ArrowEntityRenderState();
    }
}
