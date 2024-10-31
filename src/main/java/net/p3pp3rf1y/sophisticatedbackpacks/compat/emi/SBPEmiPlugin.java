package net.p3pp3rf1y.sophisticatedbackpacks.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackSettingsScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.client.gui.SettingsScreen;
import net.p3pp3rf1y.sophisticatedcore.util.ColorHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@EmiEntrypoint
public final class SBPEmiPlugin implements EmiPlugin {
    public static @NotNull Bounds toBounds(Rect2i rect) {
        return new Bounds(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }
    private static Pair<Integer, Integer> getComparisonData(EmiStack stack) {
        IBackpackWrapper wrapper = BackpackWrapper.fromStack(stack.getItemStack());
        return Pair.of(wrapper.getMainColor(), wrapper.getAccentColor());
    }
    private static void addExclusionArea(Screen screen0, Consumer<Bounds> consumer) {
        if (screen0 instanceof BackpackScreen screen) {
            if (screen.getUpgradeSlotsRectangle().isPresent()) {
                consumer.accept(toBounds(screen.getUpgradeSlotsRectangle().get()));
            }
            for (Rect2i rect : screen.getUpgradeSettingsControl().getTabRectangles()) {
                consumer.accept(toBounds(rect));
            }
            if (screen.getSortButtonsRectangle().isPresent()) {
                consumer.accept(toBounds(screen.getSortButtonsRectangle().get()));
            }
        } else if (screen0 instanceof BackpackSettingsScreen screen) {
            for (Rect2i rect : screen.getSettingsTabControl().getTabRectangles()) {
                consumer.accept(toBounds(rect));
            }
        }
    }
    private static void registerGuiHandlers(EmiRegistry registry) {
        registry.addGenericExclusionArea(SBPEmiPlugin::addExclusionArea);
        registry.addDragDropHandler(BackpackScreen.class, new BackpackDragDropHandler<>());
        registry.addDragDropHandler(SettingsScreen.class, new SettingsDragDropHandler<>());
    }
    private static void registerRecipeCatalysts(EmiRegistry registry) {
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ModItems.CRAFTING_UPGRADE.get()));
        registry.addWorkstation(VanillaEmiRecipeCategories.STONECUTTING, EmiStack.of(ModItems.STONECUTTER_UPGRADE.get()));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMELTING, EmiStack.of(ModItems.SMELTING_UPGRADE.get()));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMELTING, EmiStack.of(ModItems.AUTO_SMELTING_UPGRADE.get()));
        registry.addWorkstation(VanillaEmiRecipeCategories.BLASTING, EmiStack.of(ModItems.BLASTING_UPGRADE.get()));
        registry.addWorkstation(VanillaEmiRecipeCategories.BLASTING, EmiStack.of(ModItems.AUTO_BLASTING_UPGRADE.get()));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMOKING, EmiStack.of(ModItems.SMOKING_UPGRADE.get()));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMOKING, EmiStack.of(ModItems.AUTO_SMOKING_UPGRADE.get()));
        registry.addWorkstation(VanillaEmiRecipeCategories.ANVIL_REPAIRING, EmiStack.of(ModItems.ANVIL_UPGRADE.get()));
    }
    private static void registerRecipes(EmiRegistry registry) {
        addSingleColorRecipes(registry);
        addMultipleColorsRecipe(registry);
    }
    private static void registerItemSubtypes(EmiRegistry registry) {
        for (Supplier<BackpackItem> item0 : Arrays.asList(ModItems.BACKPACK, ModItems.COPPER_BACKPACK, ModItems.IRON_BACKPACK, ModItems.GOLD_BACKPACK, ModItems.DIAMOND_BACKPACK, ModItems.NETHERITE_BACKPACK)) {
            registry.setDefaultComparison(item0.get(), Comparison.compareData(SBPEmiPlugin::getComparisonData));
        }
    }
    private static void addSingleColorRecipes(EmiRegistry registry) {
        for (DyeColor color : DyeColor.values()) {
            ItemStack backpackOutput = ModItems.BACKPACK.get().getDefaultInstance();
            BackpackWrapper.fromStack(backpackOutput).setColors(color.getTextureDiffuseColor(), color.getTextureDiffuseColor());
            List<EmiIngredient> ingredients = new ObjectArrayList<>(2);
            ingredients.add(EmiStack.of(ModItems.BACKPACK.get()));
            ingredients.add(EmiIngredient.of(color.getTag()));
            
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SophisticatedBackpacks.MOD_ID, "/single_color/" + color.getSerializedName());
            registry.addRecipe(new EmiCraftingRecipe(ingredients, EmiStack.of(backpackOutput), id, true));
        }
    }
    private static void addMultipleColorsRecipe(EmiRegistry registry) {
        List<EmiIngredient> ingredients = new ObjectArrayList<>(6);
        ingredients.add(EmiIngredient.of(DyeColor.YELLOW.getTag()));
        ingredients.add(EmiStack.of(ModItems.BACKPACK.get()));
        ingredients.add(EmiStack.EMPTY);
        ingredients.add(EmiIngredient.of(DyeColor.LIME.getTag()));
        ingredients.add(EmiIngredient.of(DyeColor.BLUE.getTag()));
        ingredients.add(EmiIngredient.of(DyeColor.BLACK.getTag()));
        
        ItemStack backpackOutput = new ItemStack(ModItems.BACKPACK.get());
        int clothColor = ColorHelper.calculateColor(BackpackWrapper.DEFAULT_MAIN_COLOR, BackpackWrapper.DEFAULT_MAIN_COLOR, List.of(
          DyeColor.BLUE, DyeColor.YELLOW, DyeColor.LIME
        ));
        int trimColor = ColorHelper.calculateColor(BackpackWrapper.DEFAULT_ACCENT_COLOR, BackpackWrapper.DEFAULT_ACCENT_COLOR, List.of(
          DyeColor.BLUE, DyeColor.BLACK
        ));
        
        BackpackWrapper.fromStack(backpackOutput).setColors(clothColor, trimColor);
        
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SophisticatedBackpacks.MOD_ID, "multiple_colors");
        registry.addRecipe(new EmiCraftingRecipe(ingredients, EmiStack.of(backpackOutput), id, true));
    }
    @Override
    public void register(EmiRegistry registry) {
        registerItemSubtypes(registry);
        registerRecipes(registry);
        registerRecipeCatalysts(registry);
        registerGuiHandlers(registry);
        registry.addRecipeHandler(ModItems.BACKPACK_CONTAINER_TYPE.get(), new BackpackRecipeHandler());
    }
}
