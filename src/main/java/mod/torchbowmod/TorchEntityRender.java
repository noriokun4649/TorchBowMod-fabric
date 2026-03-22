package mod.torchbowmod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
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
    public void render(TorchRenderState renderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState){
        BlockState blockState = renderState.blockState;

        if (!blockState.isAir()) {
            matrixStack.push();
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(renderState.yaw - 90.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(renderState.pitch - 90.0F));
            matrixStack.translate(-0.5, 0, 0.5);
        }
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
        orderedRenderCommandQueue.submitBlock(matrixStack,blockState, renderState.light,OverlayTexture.DEFAULT_UV, renderState.outlineColor);

        matrixStack.pop();
    }
}
