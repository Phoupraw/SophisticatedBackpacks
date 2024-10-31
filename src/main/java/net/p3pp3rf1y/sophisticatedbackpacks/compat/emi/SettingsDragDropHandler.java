package net.p3pp3rf1y.sophisticatedbackpacks.compat.emi;

import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.p3pp3rf1y.sophisticatedcore.client.gui.SettingsScreen;
import net.p3pp3rf1y.sophisticatedcore.compat.jei.SetMemorySlotPayload;
import net.p3pp3rf1y.sophisticatedcore.settings.memory.MemorySettingsTab;

import static net.p3pp3rf1y.sophisticatedbackpacks.compat.emi.BackpackDragDropHandler.toBounds;

public class SettingsDragDropHandler<S extends SettingsScreen> implements EmiDragDropHandler<S> {
    @Override
    public boolean dropStack(S screen, EmiIngredient stack, int x, int y) {
        for (EmiStack emiStack : stack.getEmiStacks()) {
            if (emiStack.getKey() instanceof Item) {
                if (screen.getSettingsTabControl().getOpenTab().orElse(null) instanceof MemorySettingsTab) {
                    ItemStack ghostStack = emiStack.getItemStack();
                    for (Slot s : screen.getMenu().getStorageInventorySlots()) {
                        if (s.getItem().isEmpty() && toBounds(screen, s).contains(x, y)) {
                            screen.startMouseDragHandledByOther();
                            PacketDistributor.sendToServer(new SetMemorySlotPayload(ghostStack, s.index));
                            screen.stopMouseDragHandledByOther();
                            return true;
                        }
                    }
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
                screen.startMouseDragHandledByOther();
                if (screen.getSettingsTabControl().getOpenTab().orElse(null) instanceof MemorySettingsTab tab) {
                    for (Slot s : screen.getMenu().getStorageInventorySlots()) {
                        if (s.getItem().isEmpty()) {
                            Bounds b = toBounds(screen, s);
                            draw.fill(b.left(), b.top(), b.right(), b.bottom(), 0x8822BB33);
                        }
                    }
                }
            }
            break;
        }
    }
}
