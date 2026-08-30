package net.hazex.spiritsofthepast.blocks.cracked_sandstone;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;

public class CrumblingSandstoneBlock extends Block {
    public static final MapCodec<CrumblingSandstoneBlock> CODEC = simpleCodec(CrumblingSandstoneBlock::new);

    public static final BooleanProperty CRACKED = BooleanProperty.create("cracked");

    public static final int CRACK_DELAY_TICKS = 10;
    public static final int COLLAPSE_DELAY_TICKS = 10;

    public CrumblingSandstoneBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.stateDefinition.any().setValue(CRACKED, false));
    }

    @Override
    public MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CRACKED);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);

        if (!(entity instanceof Player player) || player.isSpectator()) {
            return;
        }

        if (state.getValue(CRACKED)) {
            return;
        }

        if (level instanceof ServerLevel serverLevel
                && !serverLevel.getBlockTicks().hasScheduledTick(pos, this)) {
            serverLevel.scheduleTick(pos, this, CRACK_DELAY_TICKS);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(CRACKED)) {
            level.destroyBlock(pos, false);
            return;
        }

        if (!hasPlayerStandingOn(level, pos)) {
            return;
        }

        level.setBlock(pos, state.setValue(CRACKED, true), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.SAND_HIT, SoundSource.BLOCKS, 1.0F, 0.7F);
        level.scheduleTick(pos, this, COLLAPSE_DELAY_TICKS);
    }

    private static boolean hasPlayerStandingOn(ServerLevel level, BlockPos pos) {
        AABB area = new AABB(
                pos.getX(), pos.getY() + 1.0D, pos.getZ(),
                pos.getX() + 1.0D, pos.getY() + 1.6D, pos.getZ() + 1.0D);

        for (Player player : level.getEntitiesOfClass(Player.class, area)) {
            if (!player.isSpectator() && player.onGround()) {
                return true;
            }
        }

        return false;
    }
}
