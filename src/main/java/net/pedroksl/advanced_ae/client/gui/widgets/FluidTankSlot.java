package net.pedroksl.advanced_ae.client.gui.widgets;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.helpers.ColorContainer;
import net.pedroksl.advanced_ae.network.packet.FluidTankItemUsePacket;

import appeng.api.stacks.GenericStack;
import appeng.client.gui.AEBaseScreen;
import appeng.core.localization.Tooltips;

public class FluidTankSlot extends AbstractWidget {

    private final AbstractContainerScreen<?> screen;
    private TextureAtlasSprite fluidTexture;
    private FluidStack content = FluidStack.EMPTY;
    private final int maxLevel;
    private boolean disableRender = false;
    private final int index;

    public FluidTankSlot(
            AbstractContainerScreen<?> screen,
            int index,
            int x,
            int y,
            int width,
            int height,
            int maxLevel,
            Component message) {
        super(x, y, width, height, message);
        this.maxLevel = maxLevel;
        this.screen = screen;
        this.index = index;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        var stack = screen.getMenu().getCarried();
        if (!stack.isEmpty()) {
            var cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (cap != null) {
                FluidStack fluidStack = cap.getFluidInTank(0);
                if (fluidStack.is(this.content.getFluid()) || fluidStack.isEmpty() || this.content.isEmpty()) {
                    var actualButton = screen instanceof AEBaseScreen<?> baseScreen
                            ? (baseScreen.isHandlingRightClick() ? 1 : 0)
                            : button;
                    PacketDistributor.sendToServer(new FluidTankItemUsePacket(this.index, actualButton));
                }
            }
        }
    }

    @Override
    public void playDownSound(SoundManager handler) {}

    public void playDownSound(SoundManager handler, boolean isInsert) {
        if (isInsert) {
            handler.play(SimpleSoundInstance.forUI(SoundEvents.BUCKET_EMPTY, 1.0F, 1.0F));
        } else {
            handler.play(SimpleSoundInstance.forUI(SoundEvents.BUCKET_FILL, 1.0F, 1.0F));
        }
    }

    @Override
    public boolean isValidClickButton(int button) {
        return button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (content == null || fluidTexture == null || this.disableRender) return;

        var argb = new ColorContainer(
                IClientFluidTypeExtensions.of(this.content.getFluid()).getTintColor());
        guiGraphics.setColor(argb.r(), argb.g(), argb.b(), argb.a());

        float levels = content.getAmount() / 1000f / maxLevel;
        var usedY = (int) Math.ceil(levels * this.height);

        float tiles = (float) usedY / this.width;

        var currentY = this.getY() + this.height;
        for (var x = 0; x < tiles; x++) {
            if (tiles - x > 1) {
                var size = this.width;
                guiGraphics.blit(this.getX(), currentY - size, 0, size, size, this.fluidTexture);
                currentY -= size;
            } else {
                var size = (int) Math.ceil((tiles - x) * this.width);
                guiGraphics.blit(this.getX(), currentY - size, 0, this.width, size, this.fluidTexture);
            }
        }
        guiGraphics.setColor(1, 1, 1, 1);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {}

    public void setFluidStack(FluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            this.disableRender = true;
            updateTooltip(fluidStack);
            return;
        }

        this.disableRender = false;
        boolean updateTexture = this.content.isEmpty() || fluidStack.getFluid() != this.content.getFluid();
        this.content = fluidStack;

        updateTooltip(fluidStack);

        if (updateTexture && !this.content.isEmpty()) {
            IClientFluidTypeExtensions properties = IClientFluidTypeExtensions.of(this.content.getFluid());
            ResourceLocation texture = properties.getStillTexture(this.content);
            this.fluidTexture = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(texture);
        }
    }

    private void updateTooltip(FluidStack stack) {
        if (stack.isEmpty()) {
            setTooltip(Tooltip.create(Tooltips.of(
                    AAEText.TankEmpty.text(),
                    Component.literal("\n"),
                    AAEText.TankAmount.text(0, 16).withStyle(Tooltips.NUMBER_TEXT))));
            return;
        }

        var genericStack = GenericStack.fromFluidStack(content);
        if (genericStack != null) {
            setTooltip(Tooltip.create(Tooltips.of(
                    stack.getHoverName(),
                    Component.literal("\n"),
                    Tooltips.ofAmount(genericStack),
                    Component.literal("\n"),
                    Component.literal(getModDisplayNameFromId(genericStack.what().getModId()))
                            .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC))));
        }
    }

    private static String getModDisplayNameFromId(String modId) {
        var container = ModList.get().getModContainerById(modId);

        if (modId.equals("c")) {
            return "Common";
        }
        else if ((container = ModList.get().getModContainerById(modId)).isPresent()) {
            return container.get().getModInfo().getDisplayName();
        } else {
            container = ModList.get().getModContainerById(modId.replace('_', '-'));
            return container.isPresent() ? container.get().getModInfo().getDisplayName() : WordUtils.capitalizeFully(modId.replace('_', ' '));
        }
    }
}
