package com.baizeli.eternisstarrysky.Mixin;

import com.baizeli.eternisstarrysky.Content.Workbenchs.VanillaWorkbenchRecipeCategory;
import com.baizeli.eternisstarrysky.Content.Workbenchs.VoidTexture;
import mezz.jei.api.gui.drawable.IScalableDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.util.ImmutablePoint2i;
import mezz.jei.library.gui.ingredients.CycleTicker;
import mezz.jei.library.gui.recipes.RecipeLayout;
import mezz.jei.library.gui.recipes.ShapelessIcon;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(value = RecipeLayout.class,remap = false)
public class RecipeLayoutMixin
{
	@Mutable
	@Final
	@Shadow
	private IScalableDrawable recipeBackground;

	@Inject(
		at = @At("RETURN"),
		method = "<init>(Lmezz/jei/api/recipe/category/IRecipeCategory;Ljava/util/Collection;Ljava/lang/Object;Lmezz/jei/api/gui/drawable/IScalableDrawable;ILmezz/jei/library/gui/recipes/ShapelessIcon;Lmezz/jei/common/util/ImmutablePoint2i;Ljava/util/List;Ljava/util/List;Lmezz/jei/library/gui/ingredients/CycleTicker;Lmezz/jei/api/recipe/IFocusGroup;)V"
	)
	private void init(IRecipeCategory recipeCategory, Collection recipeCategoryDecorators, Object recipe, IScalableDrawable recipeBackground, int recipeBorderPadding, ShapelessIcon shapelessIcon, ImmutablePoint2i recipeTransferButtonPos, List recipeCategorySlots, List allSlots, CycleTicker cycleTicker, IFocusGroup focuses, CallbackInfo ci)
	{
		if (recipeCategory instanceof VanillaWorkbenchRecipeCategory)
			this.recipeBackground = new VoidTexture();
	}
}
