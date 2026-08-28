package net.hazex.spiritsofthepast.blocks.tomb;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.model.DefaultedBlockGeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import com.google.common.base.Suppliers;
import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.entities.sandstone.SandfallEmitterEntity;
import net.hazex.spiritsofthepast.entities.sandstone.SandstoneBoltEntity;
import net.minecraft.resources.Identifier;
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

public class PharaohsTombItem extends Item implements GeoItem {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public PharaohsTombItem(Properties properties) {
        super(properties);
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
            // Defer creation of our renderer then cache it so that it doesn't get instantiated too early
            private final Supplier<GeoItemRenderer<PharaohsTombItem>> renderer = Suppliers.memoize(
                    () -> new GeoItemRenderer<PharaohsTombItem>(new DefaultedBlockGeoModel<>(Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "pharaohs_tomb"))));

            @Nullable
            @Override
            public GeoItemRenderer<PharaohsTombItem> getGeoItemRenderer() {
                return this.renderer.get();
            }
        });
    }
}
