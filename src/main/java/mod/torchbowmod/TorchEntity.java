package mod.torchbowmod;

import net.minecraft.block.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static mod.torchbowmod.TorchBowMod.*;
import static net.minecraft.entity.EntityType.LIGHTNING_BOLT;
import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;
import static net.minecraft.util.math.Direction.DOWN;
import static net.minecraft.util.math.Direction.UP;

public class TorchEntity extends PersistentProjectileEntity {

    protected TorchEntity(EntityType<? extends TorchEntity> entityType, World world, ItemStack itemStack) {
        super(entityType, world, itemStack);
    }

    public TorchEntity(World worldIn, LivingEntity livingEntity, ItemStack itemStack) {
        super(TORCH, livingEntity, worldIn, itemStack);
    }

    public TorchEntity(EntityType<TorchEntity> torchEntityEntityType, World world) {
        this(torchEntityEntityType, world, new ItemStack(Blocks.TORCH));
    }

    public TorchEntity(World worldIn, LivingEntity livingEntity) {
        this(worldIn, livingEntity, new ItemStack(Blocks.TORCH));
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
            BlockState blockstate = this.getWorld().getBlockState(blockHitResult.getBlockPos());
            if (getWorld().getBlockState(statePos).getBlock() == Blocks.TNT){
                tntIgnite(blockHitResult);
            }else {
                setTorch(blockHitResult, blockstate, blockHitResult);
            }
        }
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
                BlockState torch_state = Blocks.WALL_TORCH.getDefaultState();
                BlockPos setBlockPos = getPosOfFace(blockpos, face);
                if (isBlockAIR(setBlockPos)) {
                    if (face == UP) {
                        torch_state = Blocks.TORCH.getDefaultState();
                        getWorld().setBlockState(setBlockPos, torch_state);
                    } else if (face != DOWN) {
                        getWorld().setBlockState(setBlockPos, torch_state.with(HORIZONTAL_FACING, face));
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
        Block getBlock = this.getWorld().getBlockState(pos).getBlock();
        if (getBlock instanceof PlantBlock) return true;
        Block[] a = {Blocks.CAVE_AIR, Blocks.AIR, Blocks.SNOW, Blocks.VINE};//空気だとみなすブロックリスト
        for (Block target : a) {
            if (getBlock == target) return true;
        }
        return false;
    }
}
