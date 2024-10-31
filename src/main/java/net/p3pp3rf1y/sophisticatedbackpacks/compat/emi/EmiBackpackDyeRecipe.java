package net.p3pp3rf1y.sophisticatedbackpacks.compat.emi;

import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.util.ColorHelper;

import java.util.List;
import java.util.Random;

public class EmiBackpackDyeRecipe extends EmiPatternCraftingRecipe {
    private static List<? extends Item> getDyes(Random random) {
        List<Item> dyes = new ObjectArrayList<>();
        HolderSet.Named<Item> tag = BuiltInRegistries.ITEM.getOrCreateTag(Tags.Items.DYES);
        for (int i = 0; i < 9; i++) {
            if (i == 4) {
                dyes.add(Items.AIR);
                continue;
            }
            int j = random.nextInt(1 - tag.size(), tag.size());
            dyes.add(j < 0 ? Items.AIR : tag.get(j).value());
        }
        return dyes;
    }
    public EmiBackpackDyeRecipe() {
        this(List.of(EmiIngredient.of(Ingredient.of(ModItems.BACKPACK.get(), ModItems.COPPER_BACKPACK.get(), ModItems.IRON_BACKPACK.get(), ModItems.GOLD_BACKPACK.get(), ModItems.DIAMOND_BACKPACK.get(), ModItems.NETHERITE_BACKPACK.get())), EmiIngredient.of(Tags.Items.DYES)), EmiStack.of(ModItems.BACKPACK.get()), ResourceLocation.fromNamespaceAndPath(SophisticatedBackpacks.MOD_ID, "/crafting/dying"), false);
    }
    //public EmiBackpackDyeRecipe(boolean left) {
    //    this(List.of(EmiStack.of(ModItems.BACKPACK.get()), EmiIngredient.of(Tags.Items.DYES)), EmiStack.of(ModItems.BACKPACK.get()), ResourceLocation.fromNamespaceAndPath(SophisticatedBackpacks.MOD_ID, "/crafting/dying/" + (left ? "left" : "right")), true);
    //}
    public EmiBackpackDyeRecipe(List<EmiIngredient> input, EmiStack output, ResourceLocation id, boolean shapeless) {
        super(input, output, id, shapeless);
    }
    @Override
    public SlotWidget getInputWidget(int slot, int x, int y) {
        return new GeneratedSlotWidget(random -> {
            var dyes = getDyes(random);
            if (slot == 4) {
                List<EmiStack> emiStacks = getInputs().getFirst().getEmiStacks();
                return emiStacks.get(random.nextInt(emiStacks.size()));
            }
            if (slot < dyes.size()) {
                return EmiStack.of(dyes.get(slot));
            }
            return EmiStack.EMPTY;
        }, unique, x, y);
    }
    @Override
    public SlotWidget getOutputWidget(int x, int y) {
        return new GeneratedSlotWidget(random -> {
            var dyes = getDyes(random);
            List<DyeColor> clothColors = new ObjectArrayList<>(5);
            List<DyeColor> trimColors = new ObjectArrayList<>(5);
            for (int i = 0; i < dyes.size(); i++) {
                DyeColor color = DyeColor.getColor(dyes.get(i).getDefaultInstance());
                if (color != null) {
                    (i % 3 <= 1 ? clothColors : trimColors).add(color);
                }
            }
            List<EmiStack> emiStacks = getInputs().getFirst().getEmiStacks();
            ItemStack stack = emiStacks.get(random.nextInt(emiStacks.size())).getItemStack();
            int clothColor = ColorHelper.calculateColor(BackpackWrapper.DEFAULT_MAIN_COLOR, BackpackWrapper.DEFAULT_MAIN_COLOR, clothColors);
            int trimColor = ColorHelper.calculateColor(BackpackWrapper.DEFAULT_ACCENT_COLOR, BackpackWrapper.DEFAULT_ACCENT_COLOR, trimColors);
            BackpackWrapper.fromStack(stack).setColors(clothColor, trimColor);
            return EmiStack.of(stack);
        }, unique, x, y);
    }
}
