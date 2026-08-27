package net.hazex.spiritsofthepast.events;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;

@EventBusSubscriber(modid = SpiritsofthePast.MODID, value = Dist.CLIENT)
public class PharaohBossBarHandler {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "textures/gui/pharaoh_boss_bar.png");

    private static final String NAME_KEY = "entity.spiritsofthepast.pharaoh";

    private static final int TEXTURE_WIDTH = 192;
    private static final int TEXTURE_HEIGHT = 48;
    private static final int BAR_WIDTH = 192;
    private static final int BAR_HEIGHT = 24;

    private static final int VANILLA_BAR_WIDTH = 182;
    private static final int VANILLA_BAR_HEIGHT = 5;

    @SubscribeEvent
    private static void onBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        LerpingBossEvent bossEvent = event.getBossEvent();
        if (!isPharaohBar(bossEvent)) {
            return;
        }

        event.setCanceled(true);

        int x = event.getX() - (BAR_WIDTH - VANILLA_BAR_WIDTH) / 2;
        int y = event.getY();

        event.setIncrement(event.getIncrement() + (BAR_HEIGHT - VANILLA_BAR_HEIGHT));

        drawBar(event.getGuiGraphics(), x, y, bossEvent.getProgress());

        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        graphics.centeredText(Minecraft.getInstance().font, bossEvent.getName(),
                graphics.guiWidth() / 2, y - 9, -1);
    }

    private static boolean isPharaohBar(LerpingBossEvent bossEvent) {
        Component name = bossEvent.getName();
        return name.getContents() instanceof TranslatableContents contents
                && NAME_KEY.equals(contents.getKey());
    }

    private static void drawBar(GuiGraphicsExtractor graphics, int x, int y, float progress) {
        int filled = Math.round(BAR_WIDTH * Math.max(0.0F, Math.min(1.0F, progress)));

        blit(graphics, x, y, 0, 0, BAR_WIDTH, BAR_HEIGHT);

        if (filled > 0) {
            blit(graphics, x, y, 0, BAR_HEIGHT, filled, BAR_HEIGHT);
        }
    }

    private static void blit(GuiGraphicsExtractor graphics, int x, int y, int u, int v, int width, int height) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
                x, y,
                (float) u, (float) v,
                width, height,
                TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }
}
