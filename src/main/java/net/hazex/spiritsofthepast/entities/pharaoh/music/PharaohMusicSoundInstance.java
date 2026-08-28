package net.hazex.spiritsofthepast.entities.pharaoh.music;

import net.hazex.spiritsofthepast.entities.pharaoh.PharaohEntity;
import net.hazex.spiritsofthepast.registries.SotPSoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class PharaohMusicSoundInstance extends AbstractTickableSoundInstance {

    private final PharaohEntity pharaoh;

    public PharaohMusicSoundInstance(PharaohEntity pharaoh) {
        super(SotPSoundRegistry.DISTURBED_SANDS.get(), SoundSource.MUSIC, RandomSource.create());
        this.pharaoh = pharaoh;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;

        this.relative = true;
        this.attenuation = Attenuation.NONE;
    }

    public PharaohEntity getPharaoh() {
        return this.pharaoh;
    }

    @Override
    public void tick() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null
                || this.pharaoh.isRemoved()
                || !this.pharaoh.isAlive()
                || this.pharaoh.level() != minecraft.level) {
            stop();
        }

        minecraft.getMusicManager().stopPlaying();
    }
}
