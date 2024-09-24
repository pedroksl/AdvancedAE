package net.pedroksl.advanced_ae.client.gui.widgets;

import net.minecraft.resources.ResourceLocation;
import net.pedroksl.advanced_ae.AdvancedAE;

import appeng.client.gui.style.Blitter;

public enum AAEIcon {
    ME_EXPORT_ON(0, 0),
    ME_EXPORT_OFF(16, 0),
    DIRECTION_OUTPUT(32, 0),

    TOOLBAR_BUTTON_BACKGROUND(176, 128, 18, 20),
    TOOLBAR_BUTTON_BACKGROUND_FOCUS(194, 128, 18, 20),
    TOOLBAR_BUTTON_BACKGROUND_HOVER(212, 128, 18, 20);

    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public static final ResourceLocation TEXTURE = AdvancedAE.makeId("textures/guis/states.png");
    public static final int TEXTURE_WIDTH = 256;
    public static final int TEXTURE_HEIGHT = 256;

    AAEIcon(int x, int y) {
        this(x, y, 16, 16);
    }

    AAEIcon(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Blitter getBlitter() {
        return Blitter.texture(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT).src(x, y, width, height);
    }
}
