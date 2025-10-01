package net.pedroksl.advanced_ae.api;

import org.jetbrains.annotations.Nullable;

import appeng.api.networking.IGridNode;
import appeng.api.storage.ILinkStatus;
import appeng.api.util.IConfigurableObject;

public interface IQuantumCrafterTermMenuHost extends IConfigurableObject {
    @Nullable
    IGridNode getGridNode();

    ILinkStatus getLinkStatus();
}
