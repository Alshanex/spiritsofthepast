package net.hazex.spiritsofthepast.blocks.tomb;

import net.hazex.spiritsofthepast.registries.SotPBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTombBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_Z = box(-6, 0, -18, 22, 16, 34);
    private static final VoxelShape SHAPE_X = box(-18, 0, -6, 34, 16, 22);

    protected AbstractTombBlock(Properties properties) {
        super(properties);
    }

    private static VoxelShape boundingBox(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Shapes.create(new AABB(x1 / 16.0, y1 / 16.0, z1 / 16.0, x2 / 16.0, y2 / 16.0, z2 / 16.0));
    }

    public static VoxelShape tombShape(Direction facing) {
        return facing.getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return tombShape(state.getValue(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public static List<BlockPos> partPositions(BlockPos core) {
        List<BlockPos> positions = new ArrayList<>(8);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx != 0 || dz != 0) {
                    positions.add(core.offset(dx, 0, dz));
                }
            }
        }

        return positions;
    }

    public static void clearParts(Level level, BlockPos corePos) {
        for (BlockPos part : partPositions(corePos)) {
            if (level.getBlockState(part).is(SotPBlockRegistry.TOMB_PART.get())) {
                level.setBlock(part, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    public static @Nullable BlockPos findCore(BlockGetter level, BlockPos partPos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                BlockPos candidate = partPos.offset(dx, 0, dz);

                if (level.getBlockState(candidate).getBlock() instanceof AbstractTombBlock) {
                    return candidate;
                }
            }
        }

        return null;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos core = context.getClickedPos();

        for (BlockPos part : partPositions(core)) {
            if (!level.getBlockState(part).canBeReplaced(context)) {
                return null;
            }
        }

        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        BlockState partState = SotPBlockRegistry.TOMB_PART.get().defaultBlockState();

        for (BlockPos part : partPositions(pos)) {
            level.setBlock(part, partState, Block.UPDATE_ALL);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        clearParts(level, pos);

        return super.playerWillDestroy(level, pos, state, player);
    }
}
