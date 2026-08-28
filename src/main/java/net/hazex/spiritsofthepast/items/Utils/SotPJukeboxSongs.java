package net.hazex.spiritsofthepast.items.Utils;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.registries.SotPSoundRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.world.item.JukeboxSong;

public class SotPJukeboxSongs {
    public static final ResourceKey<JukeboxSong> DISTURBED_SANDS_KEY = createKey("disturbed_sands");

    public static void bootstrap(BootstrapContext<JukeboxSong> context) {
        register(context, DISTURBED_SANDS_KEY, ((Holder.Reference<SoundEvent>) SotPSoundRegistry.DISTURBED_SANDS.getDelegate()), 119, 15);
    }


    private static ResourceKey<JukeboxSong> createKey(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, name));
    }

    private static void register(BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> registryKey,
                                 final Holder.Reference<SoundEvent> soundEvent, int lengthInSeconds, int comparatorOutput) {
        context.register(registryKey, new JukeboxSong(soundEvent,
                Component.translatable(Util.makeDescriptionId("jukebox_song", registryKey.identifier())), lengthInSeconds, comparatorOutput));
    }
}