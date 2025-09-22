package mod.torchbowmod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.state.ArrowEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;

@Environment(EnvType.CLIENT)
public class TorchRenderState extends ArrowEntityRenderState {
    public BlockState blockState;
    public final ItemRenderState itemRenderState = new ItemRenderState();

    public void update(Entity entity, ItemStack stack, ItemModelManager itemModelManager) {
        itemModelManager.updateForNonLivingEntity(this.itemRenderState, stack, ModelTransformationMode.GROUND, entity);
    }

}
