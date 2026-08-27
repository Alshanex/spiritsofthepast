package net.hazex.spiritsofthepast.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TombPartBlock extends Block {
    public static final MapCodec<TombPartBlock> CODEC = simpleCodec(TombPartBlock::new);

    public TombPartBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockPos core = AbstractTombBlock.findCore(level, pos);

        if (core == null) {
            return Shapes.block();
        }

        BlockState coreState = level.getBlockState(core);
        Direction facing = coreState.hasProperty(AbstractTombBlock.FACING)
                ? coreState.getValue(AbstractTombBlock.FACING)
                : Direction.NORTH;

        return AbstractTombBlock.tombShape(facing)
                .move(core.getX() - pos.getX(), 0, core.getZ() - pos.getZ());
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                       Player player, InteractionHand hand, BlockHitResult hit) {
        BlockPos core = AbstractTombBlock.findCore(level, pos);

        if (core == null) {
            return InteractionResult.PASS;
        }

        return PharaohsTombBlock.activate(level, core, level.getBlockState(core), stack, player);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockPos core = AbstractTombBlock.findCore(level, pos);

        if (core != null && !level.isClientSide()) {
            AbstractTombBlock.clearParts(level, core);
            level.destroyBlock(core, !player.getAbilities().instabuild, player);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }
}
