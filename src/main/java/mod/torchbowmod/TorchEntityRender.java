package mod.torchbowmod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import static mod.torchbowmod.TorchBowMod.MODID;

@Environment(EnvType.CLIENT)
public class TorchEntityRender extends ProjectileEntityRenderer<TorchEntity, TorchRenderState> {

    private final ItemModelManager itemModelManager;

    public TorchEntityRender(Context entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.itemModelManager = entityRenderDispatcher.getItemModelManager();
    }

    @Override
    protected Identifier getTexture(TorchRenderState state) {
        return Identifier.of(MODID, "textures/entity/torch.png");
    }

    @Override
    public TorchRenderState createRenderState() {
        return new TorchRenderState();
    }

    @Override
    public void updateRenderState(TorchEntity persistentProjectileEntity, TorchRenderState projectileEntityRenderState, float f) {
        super.updateRenderState(persistentProjectileEntity, projectileEntityRenderState, f);
        projectileEntityRenderState.update(persistentProjectileEntity, persistentProjectileEntity.getTorchItem(), this.itemModelManager);
        if(persistentProjectileEntity.getTorchItem().getItem() instanceof BlockItem blockItem){
            projectileEntityRenderState.blockState = blockItem.getBlock().getDefaultState();
        }else{
            projectileEntityRenderState.blockState = Blocks.TORCH.getDefaultState();
        }
    }

    @Override
    public void render(TorchRenderState renderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {

        BlockState blockState = renderState.blockState;

        if (!blockState.isAir()) {
            matrixStack.push();
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(renderState.yaw - 90.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(renderState.pitch - 90.0F));

            float f9 = renderState.shake;
            if (f9 > 0.0F) {
                float f10 = MathHelper.sin(f9 * 3.0F) * f9;
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f10));
            }
        }
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));

        ItemRenderState itemRenderState = renderState.itemRenderState;
        itemRenderState.render(matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV);

        matrixStack.pop();
    }
}
