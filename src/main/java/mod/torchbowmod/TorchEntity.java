package mod.torchbowmod;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static mod.torchbowmod.TorchBowMod.ITEM_TO_WALL_BLOCK;
import static mod.torchbowmod.TorchBowMod.TORCH;
import static net.minecraft.entity.EntityType.LIGHTNING_BOLT;
import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;
import static net.minecraft.util.math.Direction.DOWN;
import static net.minecraft.util.math.Direction.UP;

public class TorchEntity extends PersistentProjectileEntity {
    private static final TrackedData<ItemStack> TORCH_ITEM =
            DataTracker.registerData(TorchEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    public TorchEntity(EntityType<TorchEntity> entityEntityType, World world) {
        super(TORCH, world);
    }

    public TorchEntity(World world, LivingEntity shooter, ItemStack itemStack, ItemStack shotFrom) {
        super(TORCH ,shooter,world,itemStack,shotFrom);
        this.dataTracker.set(TORCH_ITEM, itemStack);
    }

    @Override
    protected void setStack(ItemStack stack) {
        super.setStack(stack);
        this.dataTracker.set(TORCH_ITEM, stack);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(TORCH_ITEM, this.getDefaultItemStack());
    }
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof CreeperEntity creeper){
            creeperIgnite(creeper);
        }
        if (entity instanceof LivingEntity livingentity) {
            if (!this.getWorld().isClient && this.getPierceLevel() <= 0) {
                livingentity.setStuckArrowCount(livingentity.getStuckArrowCount() - 1);
            }
        }
        entity.setOnFireFor(5);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        HitResult.Type raytraced$type = blockHitResult.getType();
        if (raytraced$type == HitResult.Type.BLOCK) {
            var statePos = blockHitResult.getBlockPos();
            BlockState blockstate = this.getWorld().getBlockState(blockHitResult.getBlockPos());
            if (getWorld().getBlockState(statePos).getBlock() == Blocks.TNT){
                tntIgnite(blockHitResult);
            }else {
                setTorch(blockHitResult, blockstate, blockHitResult);
            }
        }
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Blocks.TORCH);
    }


    private void creeperIgnite(CreeperEntity creeper){
        if (Math.random() < 0.05) {
            creeper.ignite();
            var bolt = new LightningEntity(LIGHTNING_BOLT, getWorld());
            bolt.setPosition(creeper.getBlockPos().toCenterPos());
            getWorld().spawnEntity(bolt);
        } else if (Math.random() < 0.3) {
            creeper.ignite();
        }
    }

    private void tntIgnite(BlockHitResult blockHitResult){
        var world = getWorld();
        var blockPos = blockHitResult.getBlockPos();
        TntBlock.primeTnt(world, blockPos);
        world.removeBlock(blockPos, false);
        this.remove(RemovalReason.KILLED);
    }
    private void setTorch(BlockHitResult bloatwares, BlockState blockstate, HitResult raytracedResultIn) {
        BlockPos blockpos = bloatwares.getBlockPos();
        if (!blockstate.isAir()) {
            if (!getWorld().isClient) {
                Direction face = ((BlockHitResult) raytracedResultIn).getSide();
                BlockState wallBlockState = getWallBlockState();
                BlockPos setBlockPos = getPosOfFace(blockpos, face);
                if (isBlockAIR(setBlockPos)) {
                    if (face == UP) {
                        getWorld().setBlockState(setBlockPos, getBlockState());
                    } else if (face != DOWN) {
                        getWorld().setBlockState(setBlockPos, wallBlockState.with(HORIZONTAL_FACING, face));
                    }else{
                        return;
                    }
                    this.remove(RemovalReason.KILLED);
                }
            }
        }
    }

    private BlockState getWallBlockState(){
        if (this.getItemStack().getItem() instanceof BlockItem blockItem){
            return ITEM_TO_WALL_BLOCK.get(blockItem).getDefaultState();
        }
        return Blocks.WALL_TORCH.getDefaultState();
    }
    private BlockState getBlockState(){
        if (this.getItemStack().getItem() instanceof BlockItem blockItem){
            return blockItem.getBlock().getDefaultState();
        }
        return Blocks.TORCH.getDefaultState();
    }

    private BlockPos getPosOfFace(BlockPos blockPos, Direction face) {
        return switch (face) {
            case UP -> blockPos.up();
            case EAST -> blockPos.east();
            case WEST -> blockPos.west();
            case SOUTH -> blockPos.south();
            case NORTH -> blockPos.north();
            case DOWN -> blockPos.down();
        };
    }

    private boolean isBlockAIR(BlockPos pos) {
        Block getBlock = this.getWorld().getBlockState(pos).getBlock();
        if (getBlock instanceof PlantBlock) return true;
        Block[] a = {Blocks.CAVE_AIR, Blocks.AIR, Blocks.SNOW, Blocks.VINE};//空気だとみなすブロックリスト
        for (Block target : a) {
            if (getBlock == target) return true;
        }
        return false;
    }

    public ItemStack getTorchItem(){
        return this.dataTracker.get(TORCH_ITEM);
    }
}