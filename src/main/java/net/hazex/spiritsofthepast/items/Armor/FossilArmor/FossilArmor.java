package net.hazex.spiritsofthepast.items.Armor.FossilArmor;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.util.GeckoLibUtil;
import com.google.common.base.Suppliers;
import net.hazex.spiritsofthepast.registries.SotPEffectRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

@EventBusSubscriber
public class FossilArmor extends Item implements GeoItem {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public FossilArmor(Properties properties) {
        super(properties
                .rarity(Rarity.UNCOMMON)
        );
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
            private final Supplier<FossilArmorRenderer<?>> renderer = Suppliers.memoize(FossilArmorRenderer::new);

            @Nullable
            @Override
            public GeoArmorRenderer<?, ?> getGeoArmorRenderer(ItemStack itemStack, EquipmentSlot equipmentSlot) {
                return this.renderer.get();
            }
        });
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (!(entity instanceof Player player)) {
            return;
        }

        int pieces = 0;
        int amplifier = pieces - 1;
        MobEffectInstance currentEffect = player.getEffect(SotPEffectRegistry.FOSSIL_SET_BONUS);

        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                ItemStack armorStack = player.getItemBySlot(equipmentSlot);
                if (armorStack.getItem() instanceof FossilArmor) pieces++;
            }
        }

        if (pieces <= 0) {
            player.removeEffect(SotPEffectRegistry.FOSSIL_SET_BONUS);
            return;
        }

        if (currentEffect == null || currentEffect.getAmplifier() != amplifier || currentEffect.getDuration() < 10) {
            player.addEffect(new MobEffectInstance(SotPEffectRegistry.FOSSIL_SET_BONUS, 20, amplifier, false, true, true));
        }
    }


}