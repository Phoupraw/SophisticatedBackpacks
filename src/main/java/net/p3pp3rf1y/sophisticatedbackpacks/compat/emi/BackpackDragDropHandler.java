package net.p3pp3rf1y.sophisticatedbackpacks.compat.emi;

import com.google.common.primitives.Ints;
import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.common.gui.IFilterSlot;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import net.p3pp3rf1y.sophisticatedcore.compat.jei.SetGhostSlotPayload;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pump.PumpUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.util.CapabilityHelper;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BackpackDragDropHandler<S extends StorageScreenBase<?>> implements EmiDragDropHandler<S> {
    public static Bounds toBounds(AbstractContainerScreen<?> screen, Slot s) {
        return new Bounds(screen.getGuiLeft() + s.x, screen.getGuiTop() + s.y, 17, 17);
    }
    public static Bounds toBounds(Position position) {
        return new Bounds(position.x(), position.y(), 17, 17);
    }
    private static boolean addFluidTargets(PumpUpgradeTab.Advanced pumpUpgradeTab, int x, int y, FluidStack fluidStack) {
        List<Position> slotTopLeftPositions = pumpUpgradeTab.getFluidFilterControl().getSlotTopLeftPositions();
        AtomicInteger slot = new AtomicInteger();
        for (slot.set(0); slot.get() < slotTopLeftPositions.size(); slot.incrementAndGet()) {
            Position position = slotTopLeftPositions.get(slot.get());
            if (toBounds(position).contains(x, y)) {
                pumpUpgradeTab.getFluidFilterControl().setFluid(slot.get(), fluidStack);
                return true;
            }
        }
        return false;
    }
    private static void addFluidTargets(PumpUpgradeTab.Advanced pumpUpgradeTab, GuiGraphics draw) {
        List<Position> slotTopLeftPositions = pumpUpgradeTab.getFluidFilterControl().getSlotTopLeftPositions();
        AtomicInteger slot = new AtomicInteger();
        for (slot.set(0); slot.get() < slotTopLeftPositions.size(); slot.incrementAndGet()) {
            Position position = slotTopLeftPositions.get(slot.get());
            Bounds b = toBounds(position);
            draw.fill(b.left(), b.top(), b.right(), b.bottom(), 0x8822BB33);
        }
    }
    @Override
    public boolean dropStack(S screen, EmiIngredient stack, int x, int y) {
        for (EmiStack emiStack : stack.getEmiStacks()) {
            if (emiStack.getKey() instanceof Item) {
                ItemStack ghostStack = emiStack.getItemStack();
                FluidStack fluidStack = CapabilityHelper.getFromCapability(ghostStack, Capabilities.FluidHandler.ITEM, null, fluidHandler -> fluidHandler.getTanks() > 0 ? fluidHandler.getFluidInTank(0) : FluidStack.EMPTY, FluidStack.EMPTY);
                if (!fluidStack.isEmpty()) {
                    if (screen.getUpgradeSettingsControl().getOpenTab().orElse(null) instanceof PumpUpgradeTab.Advanced pumpUpgradeTab) {
                        if (addFluidTargets(pumpUpgradeTab, x, y, fluidStack)) {
                            return true;
                        }
                    }
                } else {
                    StorageContainerMenuBase<?> container = screen.getMenu();
                    if (container.getOpenContainer().isPresent()) {
                        for (Slot s : container.getOpenContainer().get().getSlots()) {
                            if (s instanceof IFilterSlot && s.mayPlace(ghostStack) && toBounds(screen, s).contains(x, y)) {
                                PacketDistributor.sendToServer(new SetGhostSlotPayload(ghostStack, s.index));
                                return true;
                            }
                        }
                    }
                }
            } else if (emiStack.getKey() instanceof Fluid fluid) {
                if (screen.getUpgradeSettingsControl().getOpenTab().orElse(null) instanceof PumpUpgradeTab.Advanced pumpUpgradeTab) {
                    //noinspection deprecation
                    addFluidTargets(pumpUpgradeTab, x, y, new FluidStack(fluid.builtInRegistryHolder(), Ints.saturatedCast(emiStack.getAmount()), emiStack.getComponentChanges()));
                }
            }
            break;
        }
        return false;
    }
    @Override
    public void render(S screen, EmiIngredient dragged, GuiGraphics draw, int mouseX, int mouseY, float delta) {
        for (EmiStack emiStack : dragged.getEmiStacks()) {
            if (emiStack.getKey() instanceof Item) {
                ItemStack ghostStack = emiStack.getItemStack();
                FluidStack fluidStack = CapabilityHelper.getFromCapability(ghostStack, Capabilities.FluidHandler.ITEM, null, fluidHandler -> fluidHandler.getTanks() > 0 ? fluidHandler.getFluidInTank(0) : FluidStack.EMPTY, FluidStack.EMPTY);
                if (!fluidStack.isEmpty()) {
                    if (screen.getUpgradeSettingsControl().getOpenTab().orElse(null) instanceof PumpUpgradeTab.Advanced pumpUpgradeTab) {
                        addFluidTargets(pumpUpgradeTab, draw);
                    }
                } else {
                    StorageContainerMenuBase<?> container = screen.getMenu();
                    if (container.getOpenContainer().isPresent()) {
                        for (Slot s : container.getOpenContainer().get().getSlots()) {
                            if (s instanceof IFilterSlot && s.mayPlace(ghostStack)) {
                                Bounds b = toBounds(screen, s);
                                draw.fill(b.left(), b.top(), b.right(), b.bottom(), 0x8822BB33);
                            }
                        }
                    }
                }
            } else if (emiStack.getKey() instanceof Fluid) {
                if (screen.getUpgradeSettingsControl().getOpenTab().orElse(null) instanceof PumpUpgradeTab.Advanced pumpUpgradeTab) {
                    addFluidTargets(pumpUpgradeTab, draw);
                }
            }
            break;
        }
    }
}
