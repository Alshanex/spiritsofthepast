package net.hazex.spiritsofthepast.entities.pharaoh.music;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.entities.pharaoh.PharaohEntity;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = SpiritsofthePast.MODID, value = Dist.CLIENT)
public class PharaohMusicHandler {

    @Nullable
    private static PharaohMusicSoundInstance current;

    @SubscribeEvent
    private static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof PharaohEntity pharaoh)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (current != null && minecraft.getSoundManager().isActive(current)) {
            if (current.getPharaoh() == pharaoh) {
                return;
            }
            minecraft.getSoundManager().stop(current);
        }

        current = new PharaohMusicSoundInstance(pharaoh);
        minecraft.getSoundManager().play(current);

        minecraft.getMusicManager().stopPlaying();
    }
}
