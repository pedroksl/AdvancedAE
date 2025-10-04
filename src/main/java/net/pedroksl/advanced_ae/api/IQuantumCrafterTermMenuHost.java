package net.pedroksl.advanced_ae.api;

import appeng.api.storage.ILinkStatus;
import appeng.api.storage.ISubMenuHost;
import appeng.api.util.IConfigurableObject;

public interface IQuantumCrafterTermMenuHost extends IConfigurableObject, ISubMenuHost {
    ILinkStatus getLinkStatus();
}
