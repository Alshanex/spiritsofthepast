package net.hazex.spiritsofthepast.blocks.tomb;

import com.mojang.serialization.MapCodec;
import net.hazex.spiritsofthepast.entities.pharaoh.PharaohEntity;
import net.hazex.spiritsofthepast.registries.SotPBlockRegistry;
import net.hazex.spiritsofthepast.registries.SotPEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PharaohsTombBlock extends AbstractTombBlock {
    public static final MapCodec<PharaohsTombBlock> CODEC = simpleCodec(PharaohsTombBlock::new);

    public static final BooleanProperty OPENING = BooleanProperty.create("opening");

    public static final int ANIMATION_TICKS = 100;

    public static final Item KEY_ITEM = Items.STICK;

    private static final double SPAWN_OFFSET = 1.25D;

    public PharaohsTombBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPENING, false));
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPENING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PharaohsTombBlockEntity(pos, state);
    }

    // Interaction

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                       Player player, InteractionHand hand, BlockHitResult hit) {
        return activate(level, pos, state, stack, player);
    }

    public static InteractionResult activate(Level level, BlockPos corePos, BlockState coreState, ItemStack stack, Player player) {
        if (!(coreState.getBlock() instanceof PharaohsTombBlock)) {
            return InteractionResult.PASS;
        }

        if (coreState.getValue(OPENING)) {
            return InteractionResult.PASS;
        }

        if (!stack.is(KEY_ITEM)) {
            return InteractionResult.PASS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        serverLevel.setBlock(corePos, coreState.setValue(OPENING, true), Block.UPDATE_ALL);

        serverLevel.scheduleTick(corePos, coreState.getBlock(), ANIMATION_TICKS);

        serverLevel.playSound(null, corePos, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.BLOCKS, 1.0F, 0.4F);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }


    // End of the animation

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(OPENING)) {
            return;
        }

        Direction facing = state.getValue(FACING);

        level.setBlock(pos, SotPBlockRegistry.EMPTY_TOMB.get().defaultBlockState().setValue(FACING, facing),
                Block.UPDATE_ALL);

        spawnOccupant(level, pos, facing);
    }

    private static void spawnOccupant(ServerLevel level, BlockPos pos, Direction facing) {
        PharaohEntity pharaohEntity = new PharaohEntity(SotPEntityRegistry.PHARAOH.get(), level);

        if (pharaohEntity == null) {
            return;
        }

        Vec3 spawnPos = Vec3.atBottomCenterOf(pos)
                .add(facing.getStepX() * SPAWN_OFFSET, 0.0D, facing.getStepZ() * SPAWN_OFFSET);

        pharaohEntity.setPos(spawnPos.x, spawnPos.y + 1, spawnPos.z);
        pharaohEntity.setYRot(facing.toYRot());
        pharaohEntity.setYHeadRot(facing.toYRot());
        pharaohEntity.setYBodyRot(facing.toYRot());
        pharaohEntity.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), EntitySpawnReason.TRIGGERED, null);
        pharaohEntity.setPersistenceRequired();

        level.addFreshEntity(pharaohEntity);
    }
}
