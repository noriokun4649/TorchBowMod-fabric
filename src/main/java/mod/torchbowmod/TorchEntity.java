package mod.torchbowmod;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static mod.torchbowmod.TorchBowMod.TORCH;
import static net.minecraft.entity.EntityType.LIGHTNING_BOLT;
import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;
import static net.minecraft.util.math.Direction.DOWN;
import static net.minecraft.util.math.Direction.UP;

public class TorchEntity extends PersistentProjectileEntity {

    public TorchEntity(EntityType<TorchEntity> entityEntityType, World world) {
        super(TORCH, world);
    }

    public TorchEntity(World world, LivingEntity shooter, ItemStack itemStack, ItemStack shotFrom) {
        super(TORCH ,shooter,world,itemStack,shotFrom);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof CreeperEntity creeper){
            creeperIgnite(creeper);
        }
        entity.setOnFireFor(5);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        HitResult.Type raytraced$type = blockHitResult.getType();
        if (raytraced$type == HitResult.Type.BLOCK) {
            var statePos = blockHitResult.getBlockPos();
            BlockState blockstate = this.getEntityWorld().getBlockState(blockHitResult.getBlockPos());
            if (getEntityWorld().getBlockState(statePos).getBlock() == Blocks.TNT){
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
            var bolt = new LightningEntity(LIGHTNING_BOLT, getEntityWorld());
            bolt.setPosition(creeper.getBlockPos().toCenterPos());
            getEntityWorld().spawnEntity(bolt);
        } else if (Math.random() < 0.3) {
            creeper.ignite();
        }
    }

    private void tntIgnite(BlockHitResult blockHitResult){
        var world = getEntityWorld();
        var blockPos = blockHitResult.getBlockPos();
        TntBlock.primeTnt(world, blockPos);
        world.removeBlock(blockPos, false);
        this.remove(RemovalReason.KILLED);
    }
    private void setTorch(BlockHitResult bloatwares, BlockState blockstate, HitResult raytracedResultIn) {
        BlockPos blockpos = bloatwares.getBlockPos();
        if (!blockstate.isAir()) {
            if (!getEntityWorld().isClient()) {
                Direction face = ((BlockHitResult) raytracedResultIn).getSide();
                BlockState torch_state = Blocks.WALL_TORCH.getDefaultState();
                BlockPos setBlockPos = getPosOfFace(blockpos, face);
                if (isBlockAIR(setBlockPos)) {
                    if (face == UP) {
                        torch_state = Blocks.TORCH.getDefaultState();
                        getEntityWorld().setBlockState(setBlockPos, torch_state);
                    } else if (face != DOWN) {
                        getEntityWorld().setBlockState(setBlockPos, torch_state.with(HORIZONTAL_FACING, face));
                    }else{
                        return;
                    }
                    this.remove(RemovalReason.KILLED);
                }
            }
        }
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
        Block getBlock = this.getEntityWorld().getBlockState(pos).getBlock();
        if (getBlock instanceof PlantBlock) return true;
        Block[] a = {Blocks.CAVE_AIR, Blocks.AIR, Blocks.SNOW, Blocks.VINE};//空気だとみなすブロックリスト
        for (Block target : a) {
            if (getBlock == target) return true;
        }
        return false;
    }
}
