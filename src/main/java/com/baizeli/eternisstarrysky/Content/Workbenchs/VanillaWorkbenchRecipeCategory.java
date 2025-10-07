package com.baizeli.eternisstarrysky.Content.Workbenchs;

import com.baizeli.eternisstarrysky.Content.ModBlock;
import com.baizeli.eternisstarrysky.EternisStarrySky;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class VanillaWorkbenchRecipeCategory implements IRecipeCategory<VanillaWorkbenchRecipe> {

    public static final ResourceLocation UID = ResourceLocation.parse(EternisStarrySky.resource("vanilla_workbench"));
    public static final RecipeType<VanillaWorkbenchRecipe> RECIPE_TYPE = new RecipeType<>(UID, VanillaWorkbenchRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public VanillaWorkbenchRecipeCategory(IGuiHelper helper) {
        // 增大背景以容纳更多槽位或使用滚动
        this.background = helper.drawableBuilder(
                EternisWorkbench.RESOURCE_WORKBENCH, 0, 0,
                EternisWorkbench.ASSETS_WORKBENCH_WIDTH, EternisWorkbench.ASSETS_WORKBENCH_HEIGHT
            )
            .setTextureSize(EternisWorkbench.ASSETS_WORKBENCH_WIDTH, EternisWorkbench.ASSETS_WORKBENCH_HEIGHT)
            .build();
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlock.workbench.get()));
    }

    @Override
    public RecipeType<VanillaWorkbenchRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.eternisstarrysky.vanilla_workbench");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, VanillaWorkbenchRecipe recipe, IFocusGroup focuses) {
        int maxDisplaySize = Math.min(9, WorkbenchConfig.GRID_SIZE);
        int displayWidth = Math.min(recipe.width, maxDisplaySize);
        int displayHeight = Math.min(recipe.height, maxDisplaySize);

        int recipeIndex = 0;

        for (int row = 0; row < 9; row++)
        {
            for (int col = 0; col < 9; col++)
            {
                Ingredient ingredient;// = recipe.getIngredients().get(recipeIndex);
                if (row < displayHeight && col < displayWidth)
                    ingredient = recipe.getIngredients().get(recipeIndex++);
                else
                    ingredient = Ingredient.EMPTY;
                int sx = EternisWorkbench.WORKBENCH_SLOT_X + col * 18;
                int sy = EternisWorkbench.WORKBENCH_SLOT_Y + row * 18;
                builder.addSlot(RecipeIngredientRole.INPUT, sx, sy)
                    .addIngredients(ingredient);
            }
        }

        // 添加结果槽位
        builder.addSlot(RecipeIngredientRole.OUTPUT, EternisWorkbench.WORKBENCH_RESULT_X, EternisWorkbench.WORKBENCH_RESULT_Y)
            .addItemStack(recipe.getResultItem(null));
    }
}
