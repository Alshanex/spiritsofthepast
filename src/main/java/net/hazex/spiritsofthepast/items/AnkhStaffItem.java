package net.hazex.spiritsofthepast.items;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import com.google.common.base.Suppliers;
import net.hazex.spiritsofthepast.entities.SandfallEmitterEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AnkhStaffItem extends Item implements GeoItem {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static final int COOLDOWN = 200;

    public AnkhStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            Vec3 centre = player.position();
            SandfallEmitterEntity emitter = new SandfallEmitterEntity(level, centre.x, centre.y, centre.z);
            emitter.setOwner(player);
            level.addFreshEntity(emitter);

            level.playSound(null, centre.x, centre.y, centre.z, SoundEvents.SAND_PLACE, SoundSource.PLAYERS, 1.5F, 0.5F);
        }

        player.getCooldowns().addCooldown(stack, COOLDOWN);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        // We can fill this in later
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final Supplier<GeoItemRenderer<AnkhStaffItem>> renderer = Suppliers.memoize(() -> new GeoItemRenderer<>(AnkhStaffItem.this));

            @Override
            public @Nullable GeoItemRenderer<AnkhStaffItem> getGeoItemRenderer() {
                return this.renderer.get();
            }
        });
    }

}
