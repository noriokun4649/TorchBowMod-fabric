package mod.torchbowmod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import static mod.torchbowmod.TorchBowMod.MODID;

@Environment(EnvType.CLIENT)
public class TorchEntityRender extends ProjectileEntityRenderer<TorchEntity> {

    private final ItemRenderer itemRenderer;
    public TorchEntityRender(Context entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.itemRenderer = entityRenderDispatcher.getItemRenderer();
    }

    @Override
    public Identifier getTexture(TorchEntity entity) {
        return Identifier.of(MODID, "textures/entity/torch.png");
    }

    @Override
    public void render(TorchEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        ItemStack itemStack = entity.getTorchItem();
        if(!(itemStack.getItem() instanceof BlockItem)) itemStack = Blocks.TORCH.asItem().getDefaultStack();
        if (!itemStack.isEmpty()) {
            var bakedModel = itemRenderer.getModel(itemStack, entity.getWorld(), null, 0);

            matrixStack.push();
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(partialTicks, entity.prevYaw, entity.getYaw()) - 90.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(partialTicks, entity.prevPitch, entity.getPitch()) - 90.0F));

            float f9 = (float)entity.shake - partialTicks;
            if (f9 > 0.0F) {
                float f10 = MathHelper.sin(f9 * 3.0F) * f9;
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f10));
            }

            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
            matrixStack.scale(1.5F, 1.5F, 1.5F);

            itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV, bakedModel);

            matrixStack.pop();
        }
    }
}
