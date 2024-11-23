package net.pedroksl.advanced_ae.common.helpers;

import net.minecraft.util.FastColor;

public class ColorContainer {

    private int color;
    private float red;
    private float green;
    private float blue;
    private float alpha;

    public ColorContainer(int color) {
        this.color = color;
        calcColors();
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int color() {
        return this.color;
    }

    public float r() {
        return this.red;
    }

    public float g() {
        return this.green;
    }

    public float b() {
        return this.blue;
    }

    public float a() {
        return this.alpha;
    }

    private void calcColors() {
        this.red = (float) FastColor.ARGB32.red(color) / 255.0F;
        this.green = (float) FastColor.ARGB32.green(color) / 255.0F;
        this.blue = (float) FastColor.ARGB32.blue(color) / 255.0F;
        this.alpha = (float) FastColor.ARGB32.alpha(color) / 255.0F;
    }
}
