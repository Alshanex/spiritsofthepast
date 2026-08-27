package net.hazex.spiritsofthepast.items.Weapons;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import com.google.common.base.Suppliers;
import net.hazex.spiritsofthepast.entities.sandstone.SandfallEmitterEntity;
import net.hazex.spiritsofthepast.entities.sandstone.SandstoneBoltEntity;
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

    private static final int STORM_COOLDOWN = 200;
    private static final int BOLT_COOLDOWN = 10;

    public AnkhStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean storm = player.isShiftKeyDown();

        if (!level.isClientSide()) {
            if (storm) {
                summonStorm(level, player);
            } else {
                fireBolt(level, player);
            }
        }

        player.getCooldowns().addCooldown(stack, storm ? STORM_COOLDOWN : BOLT_COOLDOWN);
        return InteractionResult.SUCCESS;
    }

    private static void summonStorm(Level level, Player player) {
        Vec3 centre = player.position();
        SandfallEmitterEntity emitter = new SandfallEmitterEntity(level, centre.x, centre.y, centre.z);
        emitter.setOwner(player);
        level.addFreshEntity(emitter);

        level.playSound(null, centre.x, centre.y, centre.z, SoundEvents.SAND_PLACE, SoundSource.PLAYERS, 1.5F, 0.5F);
    }

    private static void fireBolt(Level level, Player player) {
        SandstoneBoltEntity bolt = new SandstoneBoltEntity(level, player, player.getX(), player.getEyeY() - 0.15, player.getZ());
        bolt.shoot(player.getLookAngle());
        level.addFreshEntity(bolt);

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SAND_HIT, SoundSource.PLAYERS, 1.0F, 1.4F);
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final Supplier<GeoItemRenderer<AnkhStaffItem>> renderer =
                    Suppliers.memoize(() -> new GeoItemRenderer<>(AnkhStaffItem.this));

            @Override
            public @Nullable GeoItemRenderer<AnkhStaffItem> getGeoItemRenderer() {
                return this.renderer.get();
            }
        });
    }
}
