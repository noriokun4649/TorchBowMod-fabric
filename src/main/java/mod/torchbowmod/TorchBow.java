package mod.torchbowmod;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static mod.torchbowmod.TorchBowMod.*;


public class TorchBow extends BowItem {

    public static final Predicate<ItemStack> TORCH = itemStack -> itemStack.isOf(Blocks.TORCH.asItem());
    public static final Predicate<ItemStack> TORCH_ARROW = itemStack -> itemStack.isOf(TORCH_ARROW_ITEM.asItem());
    public static final Predicate<ItemStack> MULCH_TORCH = itemStack -> itemStack.isOf(MULCH_TORCH_ITEM.asItem());
    public static final Predicate<ItemStack> TORCH_GROUP = TORCH.or(TORCH_ARROW).or(MULCH_TORCH);

    private static class Offsets {
        private final float X;
        private final float Y;

        Offsets(float x,float y){
            this.X = x;
            this.Y = y;
        }
    }

    public TorchBow(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) {
            return false;
        } else {
            ItemStack itemStack = playerEntity.getProjectileType(stack);
            if (itemStack.isEmpty()) {
                return false;
            } else {
                int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
                float f = getPullProgress(i);
                if ((double)f < 0.1) {
                    return false;
                } else {
                    List<ItemStack> list = load(stack, itemStack, playerEntity);
                    if (world instanceof ServerWorld serverWorld) {
                        if (!list.isEmpty()) {
                            if (list.getFirst().isOf(MULCH_TORCH_ITEM.asItem())){
                                ItemStack item = list.getFirst().copy();
                                list.addAll(Collections.nCopies(8, item));
                            }
                            this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 3.0F, 1.0F, f == 1.0F, null);
                        }
                    }

                    world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                    return true;
                }
            }
        }
    }

    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        float offsetX = 0F;
        float offsetY = 0F;
        if (index < 9){
            float range = 10F;
            Offsets[] offsets = {
                    new Offsets(0F, 0F),
                    new Offsets(-range, -range),
                    new Offsets(-range, 0.0F),
                    new Offsets(-range, range),
                    new Offsets(0.0F, -range),
                    new Offsets(0.0F, range),
                    new Offsets(range, -range),
                    new Offsets(range, 0.0F),
                    new Offsets(range, range)
            };
            offsetX = offsets[index].X;
            offsetY = offsets[index].Y;
        }
        projectile.setVelocity(shooter, shooter.getPitch() + offsetX, shooter.getYaw() + yaw + offsetY, 0.0F, speed, divergence);
    }


    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return TORCH_GROUP;
    }

    @Override
    public int getRange() {
        return 15;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    protected ProjectileEntity createArrowEntity(World worldIn, LivingEntity livingEntity, ItemStack weaponStack, ItemStack pickupItem, boolean critical) {
        if (pickupItem.isOf(MULCH_TORCH_ITEM.asItem())) pickupItem = Items.TORCH.getDefaultStack();
        return new TorchEntity(worldIn, livingEntity, pickupItem.copyWithCount(1), weaponStack);
    }
}