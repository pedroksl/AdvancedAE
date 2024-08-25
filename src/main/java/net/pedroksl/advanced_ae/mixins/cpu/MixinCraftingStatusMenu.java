package net.pedroksl.advanced_ae.mixins.cpu;

import org.spongepowered.asm.mixin.Mixin;

import appeng.menu.me.crafting.CraftingStatusMenu;

@Mixin(value = CraftingStatusMenu.class, remap = false)
public class MixinCraftingStatusMenu {}
