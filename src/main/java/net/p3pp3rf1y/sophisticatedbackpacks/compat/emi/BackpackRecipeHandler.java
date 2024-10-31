package net.p3pp3rf1y.sophisticatedbackpacks.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.registry.EmiRecipeFiller;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.anvil.AnvilUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.common.gui.ICraftingContainer;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.*;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stonecutter.StonecutterRecipeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stonecutter.StonecutterUpgradeContainer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BackpackRecipeHandler implements StandardRecipeHandler<BackpackContainer> {
    public static final Set<EmiRecipeCategory> ALL_SUPPORTEDS = Set.of(
      VanillaEmiRecipeCategories.CRAFTING,
      VanillaEmiRecipeCategories.STONECUTTING,
      VanillaEmiRecipeCategories.SMELTING,
      VanillaEmiRecipeCategories.BLASTING,
      VanillaEmiRecipeCategories.SMOKING,
      VanillaEmiRecipeCategories.ANVIL_REPAIRING
    );
    private static EmiRecipe emiRecipe;
    private static BackpackContainer handler;
    public static @Nullable StonecutterRecipeContainer getStonecutterContainer(StorageContainerMenuBase<?> self) {
        StonecutterUpgradeContainer firstContainer = null;
        for (UpgradeContainerBase<?, ?> container : self.getUpgradeContainers().values()) {
            if (container instanceof StonecutterUpgradeContainer container1) {
                if (container.isOpen()) {
                    return container1.getRecipeContainer();
                } else if (firstContainer == null) {
                    firstContainer = container1;
                }
            }
        }
        return firstContainer == null ? null : firstContainer.getRecipeContainer();
    }
    public static @Nullable AnvilUpgradeContainer getAnvilContainer(StorageContainerMenuBase<?> self) {
        AnvilUpgradeContainer firstContainer = null;
        for (UpgradeContainerBase<?, ?> container : self.getUpgradeContainers().values()) {
            if (container instanceof AnvilUpgradeContainer container1) {
                if (container.isOpen()) {
                    return container1;
                } else if (firstContainer == null) {
                    firstContainer = container1;
                }
            }
        }
        return firstContainer;
    }
    public static @Nullable CookingUpgradeContainer<?, ? extends CookingUpgradeWrapper<?, ? extends net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase<?>, ?>> getCookingContainer(StorageContainerMenuBase<?> self) {
        CookingUpgradeContainer<?, ? extends CookingUpgradeWrapper<?, ? extends net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase<?>, ?>> firstContainer = null;
        for (UpgradeContainerBase<?, ?> container : self.getUpgradeContainers().values()) {
            if (container instanceof CookingUpgradeContainer<?, ? extends CookingUpgradeWrapper<?, ? extends net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase<?>, ?>> container1) {
                if (container.isOpen()) {
                    return container1;
                } else if (firstContainer == null) {
                    firstContainer = container1;
                }
            }
        }
        return firstContainer;
    }
    public static @Nullable AutoCookingUpgradeContainer<?, ? extends AutoCookingUpgradeWrapper<?, ? extends UpgradeItemBase<?>, ?>> getAutoCookingContainer(StorageContainerMenuBase<?> self) {
        AutoCookingUpgradeContainer<?, ? extends AutoCookingUpgradeWrapper<?, ? extends UpgradeItemBase<?>, ?>> firstContainer = null;
        for (UpgradeContainerBase<?, ?> container : self.getUpgradeContainers().values()) {
            if (container instanceof AutoCookingUpgradeContainer<?, ? extends AutoCookingUpgradeWrapper<?, ? extends UpgradeItemBase<?>, ?>> container1) {
                if (container.isOpen()) {
                    return container1;
                } else if (firstContainer == null) {
                    firstContainer = container1;
                }
            }
        }
        return firstContainer;
    }
    public static @Nullable CookingLogicContainer<?> getCookingLogic(StorageContainerMenuBase<?> self) {
        var container = getCookingContainer(self);
        if (container != null) {
            return container.getSmeltingLogicContainer();
        }
        return null;
    }
    private static boolean addCraftingSlots(BackpackContainer handler, Collection<? super Slot> consumer) {
        Optional<? extends UpgradeContainerBase<?, ?>> potentialCraftingContainer = handler.getOpenOrFirstCraftingContainer();
        if (potentialCraftingContainer.isEmpty()) {
            return false;
        }
        UpgradeContainerBase<?, ?> openOrFirstCraftingContainer = potentialCraftingContainer.get();
        if (openOrFirstCraftingContainer instanceof ICraftingContainer cc) {
            consumer.addAll(cc.getRecipeSlots());
            return true;
        }
        return false;
    }
    private static boolean addStonecutterSlots(BackpackContainer handler, Collection<? super Slot> consumer) {
        var container = getStonecutterContainer(handler);
        if (container == null) {
            return false;
        }
        consumer.add(container.getInputSlot());
        return true;
    }
    private static boolean addAnvilSlots(BackpackContainer handler, Collection<? super Slot> consumer) {
        var container = getAnvilContainer(handler);
        if (container == null) {
            return false;
        }
        consumer.addAll(container.getSlots().subList(0, 2));
        return true;
    }
    private static boolean addCookingSlots(BackpackContainer handler, Collection<? super Slot> consumer, @Nullable EmiRecipeCategory category) {
        UpgradeContainerBase<?, ?> container = getCookingContainer(handler);
        if (container == null) {
            return false;
        }
        UpgradeContainerType<?, ?> type = container.getType();
        if (category == null ||
          category == VanillaEmiRecipeCategories.SMELTING && (ModItems.SMELTING_TYPE.equals(type)) ||
          category == VanillaEmiRecipeCategories.SMOKING && (ModItems.SMOKING_TYPE.equals(type)) ||
          category == VanillaEmiRecipeCategories.BLASTING && (ModItems.BLASTING_TYPE.equals(type))
        ) {
            consumer.add(getCookingLogic(handler).getCookingSlots().getFirst());
            return true;
        }
        return true;
    }
    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        emiRecipe = recipe;
        EmiRecipeCategory category = recipe.getCategory();
        if (handler == null) {
            return ALL_SUPPORTEDS.contains(category);
        }
        List<Slot> dummy = new ObjectArrayList<>();
        if (category == VanillaEmiRecipeCategories.CRAFTING && addCraftingSlots(handler, dummy)) {
            return true;
        }
        if (category == VanillaEmiRecipeCategories.STONECUTTING && addStonecutterSlots(handler, dummy)) {
            return true;
        }
        if (category == VanillaEmiRecipeCategories.ANVIL_REPAIRING && addAnvilSlots(handler, dummy)) {
            return true;
        }
        var cookingContainer = getCookingContainer(handler);
        if (cookingContainer != null) {
            var type = cookingContainer.getType();
            if (category == VanillaEmiRecipeCategories.SMELTING && (ModItems.SMELTING_TYPE.equals(type))) {
                return true;
            }
            if (category == VanillaEmiRecipeCategories.SMOKING && ModItems.SMOKING_TYPE.equals(type)) {
                return true;
            }
            if (category == VanillaEmiRecipeCategories.BLASTING && ModItems.BLASTING_TYPE.equals(type)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public List<Slot> getInputSources(BackpackContainer handler) {
        BackpackRecipeHandler.handler = handler;
        List<Slot> inputSources = new ObjectArrayList<>();
        LocalPlayer player = Minecraft.getInstance().player;
        for (Slot slot : handler.realInventorySlots) {
            if (slot.mayPickup(player)) {
                inputSources.add(slot);
            }
        }
        if (emiRecipe == null) {
            addCraftingSlots(handler, inputSources);
            addStonecutterSlots(handler, inputSources);
            addCookingSlots(handler, inputSources, null);
            addAnvilSlots(handler, inputSources);
        } else {
            inputSources.addAll(getCraftingSlots(emiRecipe, handler));
        }
        return inputSources;
    }
    @Override
    public List<Slot> getCraftingSlots(BackpackContainer handler) {
        BackpackRecipeHandler.handler = handler;
        if (emiRecipe != null) {
            return getCraftingSlots(emiRecipe, handler);
        }
        List<Slot> craftingSlots = new ObjectArrayList<>();
        addCraftingSlots(handler, craftingSlots);
        addStonecutterSlots(handler, craftingSlots);
        addCookingSlots(handler, craftingSlots, null);
        addAnvilSlots(handler, craftingSlots);
        return craftingSlots;
    }
    @Override
    public List<Slot> getCraftingSlots(EmiRecipe recipe, BackpackContainer handler) {
        emiRecipe = recipe;
        BackpackRecipeHandler.handler = handler;
        List<Slot> craftingSlots = new ObjectArrayList<>();
        EmiRecipeCategory category = recipe.getCategory();
        if (category == VanillaEmiRecipeCategories.CRAFTING) {
            if (addCraftingSlots(handler, craftingSlots)) {
                return craftingSlots;
            }
        } else if (category == VanillaEmiRecipeCategories.STONECUTTING) {
            if (addStonecutterSlots(handler, craftingSlots)) {
                return craftingSlots;
            }
        } else if (category == VanillaEmiRecipeCategories.ANVIL_REPAIRING) {
            if (addAnvilSlots(handler, craftingSlots)) {
                return craftingSlots;
            }
        } else if (addCookingSlots(handler, craftingSlots, category)) {
            return craftingSlots;
        }
        return List.of();
    }
    @Override
    public @Nullable Slot getOutputSlot(BackpackContainer handler) {
        BackpackRecipeHandler.handler = handler;
        EmiRecipeCategory category = emiRecipe == null ? null : emiRecipe.getCategory();
        if (category == null || category == VanillaEmiRecipeCategories.CRAFTING) {
            Optional<? extends UpgradeContainerBase<?, ?>> potentialCraftingContainer = handler.getOpenOrFirstCraftingContainer();
            if (!potentialCraftingContainer.isEmpty()) {
                UpgradeContainerBase<?, ?> openOrFirstCraftingContainer = potentialCraftingContainer.get();
                if (openOrFirstCraftingContainer instanceof CraftingUpgradeContainer cc && cc.getSlots().size() > 9) {
                    Slot slot = cc.getSlots().get(9);
                    if (slot instanceof ResultSlot) {
                        return slot;
                    }
                }
            }
        }
        if (category == null || category == VanillaEmiRecipeCategories.STONECUTTING) {
            var container = getStonecutterContainer(handler);
            if (container != null) {
                return container.getOutputSlot();
            }
        }
        if (category == null || category == VanillaEmiRecipeCategories.ANVIL_REPAIRING) {
            var container = getAnvilContainer(handler);
            if (container != null) {
                return container.getSlots().get(2);
            }
        }
        return null;
    }
    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<BackpackContainer> context) {
        emiRecipe = recipe;
        BackpackContainer handler = context.getScreenHandler();
        BackpackRecipeHandler.handler = handler;
        AbstractContainerScreen<BackpackContainer> screen = context.getScreen();
        List<ItemStack> stacks = EmiRecipeFiller.getStacks(this, recipe, screen, context.getAmount());
        if (stacks != null) {
            Minecraft.getInstance().setScreen(screen);
            EmiRecipeCategory category = recipe.getCategory();
            EmiCraftContext.Destination destination = context.getDestination();
            if (category == VanillaEmiRecipeCategories.STONECUTTING && destination != EmiCraftContext.Destination.NONE) {
                craft(recipe, new EmiCraftContext<>(screen, context.getInventory(), context.getType(), EmiCraftContext.Destination.NONE, context.getAmount()));
            }
            boolean craftResult = EmiRecipeFiller.clientFill(this, recipe, screen, stacks, destination);
            if (category == VanillaEmiRecipeCategories.STONECUTTING && destination == EmiCraftContext.Destination.NONE) {
                StonecutterRecipeContainer container = getStonecutterContainer(handler);
                if (container != null) {
                    for (var iterator = container.getRecipeList().listIterator(); iterator.hasNext(); ) {
                        int index = iterator.nextIndex();
                        RecipeHolder<StonecutterRecipe> recipeHolder = iterator.next();
                        if (recipeHolder.id().equals(recipe.getId())) {
                            container.selectRecipe(index);
                            break;
                        }
                    }
                }
            }
            return craftResult;
        }
        return false;
    }
}
