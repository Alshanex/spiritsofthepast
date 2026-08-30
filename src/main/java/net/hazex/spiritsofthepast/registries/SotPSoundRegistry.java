package net.hazex.spiritsofthepast.registries;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SotPSoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SpiritsofthePast.MODID);

    public static final Supplier<SoundEvent> SANDSTONE_IMPACT = SOUND_EVENTS.register("sandstone_impact",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "sandstone_impact")));

    public static final Supplier<SoundEvent> SUMMON_MINIONS = SOUND_EVENTS.register("summon_minions",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "summon_minions")));

    public static final Supplier<SoundEvent> PHARAOH_SUMMON = SOUND_EVENTS.register("pharaoh_summon",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "pharaoh_summon")));
    public static final Supplier<SoundEvent> PHARAOH_DEATH = SOUND_EVENTS.register("pharaoh_death",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "pharaoh_death")));
    public static final Supplier<SoundEvent> PHARAOH_FIREBALL = SOUND_EVENTS.register("pharaoh_fireball",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "pharaoh_fireball")));
    public static final Supplier<SoundEvent> PHARAOH_SPAWN = SOUND_EVENTS.register("pharaoh_spawn",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "pharaoh_spawn")));
    public static final Supplier<SoundEvent> PHARAOH_SWING = SOUND_EVENTS.register("pharaoh_swing",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "pharaoh_swing")));
    public static final Supplier<SoundEvent> PHARAOH_HEAL = SOUND_EVENTS.register("pharaoh_heal",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "pharaoh_heal")));
    public static final Supplier<SoundEvent> PHARAOH_SANDSTORM = SOUND_EVENTS.register("pharaoh_sandstorm",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "pharaoh_sandstorm")));


    public static final DeferredHolder<SoundEvent, SoundEvent> DISTURBED_SANDS = registerJukeboxSong("disturbed_sands");
    public static final DeferredHolder<SoundEvent, SoundEvent> DISTURBED_SANDS_END = registerJukeboxSong("disturbed_sands_end");
    public static final DeferredHolder<SoundEvent, SoundEvent> DISTURBED_SANDS_LOOP = registerJukeboxSong("disturbed_sands_loop");
    public static final ResourceKey<JukeboxSong> DISTURBED_SANDS_KEY = createSong("disturbed_sands");


    private static ResourceKey<JukeboxSong> createSong(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, name));
    }

    private static DeferredHolder<SoundEvent, SoundEvent> registerJukeboxSong(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, name)));
    }


    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}