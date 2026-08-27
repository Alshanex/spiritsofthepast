package net.hazex.spiritsofthepast.blocks.tomb;

import net.minecraft.util.StringRepresentable;

public enum TombPhase implements StringRepresentable {
    DORMANT("dormant"),
    OPENING("opening"),
    ACTIVATED("activated");

    private final String name;

    TombPhase(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
