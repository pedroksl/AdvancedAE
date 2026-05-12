package net.pedroksl.advanced_ae.client.renderer;

import net.minecraft.util.FormattedCharSequence;

import appeng.api.orientation.BlockOrientation;
import appeng.client.api.AEKeyRenderState;
import appeng.client.renderer.part.MonitorRenderState;

public class ThroughputMonitorRenderState extends MonitorRenderState {
    public BlockOrientation orientation;
    public final AEKeyRenderState what = new AEKeyRenderState();
    public FormattedCharSequence text;
    public int textColor;
    public int textWidth;
    public String sign;
    public FormattedCharSequence throughput;
    public int throughputWidth;
    public int subColor;
}
