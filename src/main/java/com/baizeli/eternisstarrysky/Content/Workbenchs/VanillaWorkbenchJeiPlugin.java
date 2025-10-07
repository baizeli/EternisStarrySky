package com.baizeli.eternisstarrysky.Content.Workbenchs;

import com.baizeli.eternisstarrysky.Content.ModBlock;
import com.baizeli.eternisstarrysky.EternisStarrySky;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@JeiPlugin
public class VanillaWorkbenchJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.parse(EternisStarrySky.resource("vanilla_workbench_jei"));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new VanillaWorkbenchRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<VanillaWorkbenchRecipe> recipes = Minecraft.getInstance().level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.VANILLA_WORKBENCH_TYPE.get());
        registration.addRecipes(VanillaWorkbenchRecipeCategory.RECIPE_TYPE, recipes);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(VanillaWorkbenchMenu.class,
                ModMenuTypes.VANILLA_WORKBENCH_MENU.get(),
                VanillaWorkbenchRecipeCategory.RECIPE_TYPE,
                0, WorkbenchConfig.GRID_SIZE * WorkbenchConfig.GRID_SIZE, // 输入槽位
                WorkbenchConfig.GRID_SIZE * WorkbenchConfig.GRID_SIZE + 1, 36); // 背包槽位
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlock.workbench.get()),
                VanillaWorkbenchRecipeCategory.RECIPE_TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
    }
}