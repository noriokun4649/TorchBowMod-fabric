package mod.torchbowmod;

import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static mod.torchbowmod.TorchBowMod.*;
import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;
import static net.minecraft.util.math.Direction.DOWN;
import static net.minecraft.util.math.Direction.UP;

public class TorchEntity extends PersistentProjectileEntity {
    private boolean isArrowMode = false;

    protected TorchEntity(EntityType<? extends TorchEntity> entityType, World world) {
        super(entityType, world);
    }

    public TorchEntity(World worldIn, LivingEntity livingEntity) {
        super(TORCH, livingEntity, worldIn);
    }

    public TorchEntity(World worldIn, LivingEntity livingEntity, boolean arrowMode) {
        super(TORCH, livingEntity, worldIn);
        isArrowMode = arrowMode;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.setOnFireFor(5);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        HitResult.Type raytraced$type = blockHitResult.getType();
        if (raytraced$type == HitResult.Type.BLOCK) {
            BlockState blockstate = this.world.getBlockState(blockHitResult.getBlockPos());
            setTorch(blockHitResult, blockstate, blockHitResult);
        }
    }


    private void setTorch(BlockHitResult bloatwares, BlockState blockstate, HitResult raytracedResultIn) {
        BlockPos blockpos = bloatwares.getBlockPos();
        if (!blockstate.isAir()) {
            if (!world.isClient) {
                Direction face = ((BlockHitResult) raytracedResultIn).getSide();
                BlockState torch_state = Blocks.WALL_TORCH.getDefaultState();
                BlockPos setBlockPos = getPosOfFace(blockpos, face);
                if (isBlockAIR(setBlockPos)) {
                    if (face == UP) {
                        torch_state = Blocks.TORCH.getDefaultState();
                        world.setBlockState(setBlockPos, torch_state);
                    } else if (face != DOWN) {
                        world.setBlockState(setBlockPos, torch_state.with(HORIZONTAL_FACING, face));
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
        Block getBlock = this.world.getBlockState(pos).getBlock();
        if (getBlock instanceof PlantBlock) return true;
        Block[] a = {Blocks.CAVE_AIR, Blocks.AIR, Blocks.SNOW, Blocks.VINE};//空気だとみなすブロックリスト
        for (Block target : a) {
            if (getBlock == target) return true;
        }
        return false;
    }

    @Override
    protected ItemStack asItemStack() {
        return isArrowMode ? new ItemStack(TORCH_ARROW_ITEM) : new ItemStack(Blocks.TORCH);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
        byteBuf.writeInt(this.getId());
        byteBuf.writeDouble(this.getX());
        byteBuf.writeDouble(this.getY());
        byteBuf.writeDouble(this.getZ());
        return new CustomPayloadS2CPacket(new Identifier(MODID, "spawntorch"), byteBuf);
    }
}
