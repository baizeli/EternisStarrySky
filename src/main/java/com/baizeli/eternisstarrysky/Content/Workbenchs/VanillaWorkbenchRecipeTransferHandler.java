package com.baizeli.eternisstarrysky.Content.Workbenchs;

import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.library.transfer.BasicRecipeTransferHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VanillaWorkbenchRecipeTransferHandler extends BasicRecipeTransferHandler<VanillaWorkbenchMenu, VanillaWorkbenchRecipe>
{

    public VanillaWorkbenchRecipeTransferHandler(IConnectionToServer serverConnection, IStackHelper stackHelper, IRecipeTransferHandlerHelper handlerHelper, IRecipeTransferInfo<VanillaWorkbenchMenu, VanillaWorkbenchRecipe> transferInfo)
    {
        super(serverConnection, stackHelper, handlerHelper, transferInfo);
    }

    @Override
    public Class<? extends VanillaWorkbenchMenu> getContainerClass() {
        return VanillaWorkbenchMenu.class;
    }

    @Override
    public Optional<MenuType<VanillaWorkbenchMenu>> getMenuType() {
        return Optional.of(ModMenuTypes.VANILLA_WORKBENCH_MENU.get());
    }

    @Override
    public RecipeType<VanillaWorkbenchRecipe> getRecipeType() {
        return VanillaWorkbenchRecipeCategory.RECIPE_TYPE;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(VanillaWorkbenchMenu container, VanillaWorkbenchRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {

        if (doTransfer) {
            return transferRecipeItems(container, recipe, player);
        } else {
            return validateTransfer(container, recipe, player);
        }
    }

    private @Nullable IRecipeTransferError validateTransfer(VanillaWorkbenchMenu container, VanillaWorkbenchRecipe recipe, Player player) {
        Map<Ingredient, Integer> requiredItems = new HashMap<>();

        for (Ingredient ingredient : recipe.getIngredients()) {
            if (!ingredient.isEmpty()) {
                requiredItems.put(ingredient, requiredItems.getOrDefault(ingredient, 0) + 1);
            }
        }

        for (Map.Entry<Ingredient, Integer> entry : requiredItems.entrySet()) {
            int available = 0;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (entry.getKey().test(stack)) {
                    available += stack.getCount();
                }
            }

        }

        return null;
    }

    private @Nullable IRecipeTransferError transferRecipeItems(VanillaWorkbenchMenu container, VanillaWorkbenchRecipe recipe, Player player) {

        clearCraftingGrid(container, player);

        if (recipe.shaped) {
            return transferShapedRecipe(container, recipe, player);
        } else {
            return transferShapelessRecipe(container, recipe, player);
        }
    }

    private void clearCraftingGrid(VanillaWorkbenchMenu container, Player player) {
        int craftingSlotsEnd = WorkbenchConfig.GRID_SIZE * WorkbenchConfig.GRID_SIZE;

        for (int i = 0; i < craftingSlotsEnd; i++) {
            Slot slot = container.getSlot(i);
            if (slot.hasItem()) {
                ItemStack stack = slot.remove(slot.getItem().getCount());
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
            }
        }
    }

    private @Nullable IRecipeTransferError transferShapedRecipe(VanillaWorkbenchMenu container, VanillaWorkbenchRecipe recipe, Player player) {

        int bestStartX = 0;
        int bestStartY = 0;

        // 尝试居中放置
        if (recipe.width < WorkbenchConfig.GRID_SIZE) {
            bestStartX = (WorkbenchConfig.GRID_SIZE - recipe.width) / 2;
        }
        if (recipe.height < WorkbenchConfig.GRID_SIZE) {
            bestStartY = (WorkbenchConfig.GRID_SIZE - recipe.height) / 2;
        }

        for (int recipeY = 0; recipeY < recipe.height; recipeY++) {
            for (int recipeX = 0; recipeX < recipe.width; recipeX++) {
                int ingredientIndex = recipeX + recipeY * recipe.width;

                if (ingredientIndex < recipe.getIngredients().size()) {
                    Ingredient ingredient = recipe.getIngredients().get(ingredientIndex);

                    if (!ingredient.isEmpty()) {
                        int slotIndex = (bestStartX + recipeX) + (bestStartY + recipeY) * WorkbenchConfig.GRID_SIZE;

                        ItemStack foundStack = findMatchingStackInInventory(player, ingredient);
                        if (!foundStack.isEmpty()) {
                            ItemStack singleItem = foundStack.split(1);
                            container.getSlot(slotIndex).set(singleItem);
                        }
                    }
                }
            }
        }

        return null;
    }

    private @Nullable IRecipeTransferError transferShapelessRecipe(VanillaWorkbenchMenu container, VanillaWorkbenchRecipe recipe, Player player) {

        int slotIndex = 0;

        for (Ingredient ingredient : recipe.getIngredients()) {
            if (!ingredient.isEmpty() && slotIndex < WorkbenchConfig.GRID_SIZE * WorkbenchConfig.GRID_SIZE) {

                ItemStack foundStack = findMatchingStackInInventory(player, ingredient);
                if (!foundStack.isEmpty()) {
                    ItemStack singleItem = foundStack.split(1);
                    container.getSlot(slotIndex).set(singleItem);
                }

                slotIndex++;
            }
        }

        return null;
    }

    private ItemStack findMatchingStackInInventory(Player player, Ingredient ingredient) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (ingredient.test(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
