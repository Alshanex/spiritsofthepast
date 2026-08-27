package net.hazex.spiritsofthepast.blocks.tomb;

import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import net.hazex.spiritsofthepast.registries.SotPBlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PharaohsTombBlockEntity extends BlockEntity implements GeoBlockEntity {
    private static final RawAnimation SPAWNING = RawAnimation.begin().thenPlayAndHold("spawning");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public PharaohsTombBlockEntity(BlockPos pos, BlockState state) {
        super(SotPBlockEntityRegistry.PHARAOHS_TOMB_BE.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(test -> {
            BlockState state = getBlockState();

            if (state.hasProperty(PharaohsTombBlock.OPENING) && state.getValue(PharaohsTombBlock.OPENING)) {
                return test.setAndContinue(SPAWNING);
            }

            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
