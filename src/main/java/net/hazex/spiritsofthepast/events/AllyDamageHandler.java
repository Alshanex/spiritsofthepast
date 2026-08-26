package net.hazex.spiritsofthepast.events;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.entities.pharaoh.PharaohEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = SpiritsofthePast.MODID)
public class AllyDamageHandler {

    @SubscribeEvent
    private static void onIncomingDamage(LivingIncomingDamageEvent event) {
        Entity victim = event.getEntity();
        if (!isPharaohSide(victim)) {
            return;
        }

        Entity attacker = resolveAttacker(event.getSource());
        if (attacker != null && attacker != victim && isPharaohSide(attacker)) {
            event.setCanceled(true);
        }
    }

    private static boolean isPharaohSide(Entity entity) {
        return entity instanceof PharaohEntity
                || entity.entityTags().contains(PharaohEntity.MINION_TAG);
    }

    @Nullable
    private static Entity resolveAttacker(DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker != null) {
            return attacker;
        }
        if (source.getDirectEntity() instanceof Projectile projectile) {
            return projectile.getOwner();
        }
        return source.getDirectEntity();
    }
}
