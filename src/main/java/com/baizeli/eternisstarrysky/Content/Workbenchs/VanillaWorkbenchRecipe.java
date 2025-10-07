package com.baizeli.eternisstarrysky.Content.Workbenchs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Map;

public class VanillaWorkbenchRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final String group;
    private final CraftingBookCategory category;
    private final NonNullList<Ingredient> recipeItems;
    public ItemStack result;
    public boolean shaped;
    public int width;
    public int height;

    public VanillaWorkbenchRecipe(ResourceLocation id, String group, CraftingBookCategory category, NonNullList<Ingredient> recipeItems, ItemStack result, boolean shaped, int width, int height)
    {
        this.id = id;
        this.group = group;
        this.category = category;
        this.recipeItems = recipeItems;
        this.result = result;
        this.shaped = shaped;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean matches(Container container, Level level)
    {
        return matchesShaped(container);
//        if (shaped) return matchesShaped(container);
//        else return matchesShapeless(container);
    }

    private boolean matchesShaped(Container container)
    {
        for (int i = 0; i <= WorkbenchConfig.GRID_SIZE - width; i++)
        {
            for (int j = 0; j <= WorkbenchConfig.GRID_SIZE - height; j++)
            {
                if (matchesShapedAt(container, i, j, true) || matchesShapedAt(container, i, j, false)) return true;
            }
        }
        return false;
    }

    private boolean matchesShapedAt(Container container, int startX, int startY, boolean mirrored)
    {
        for (int x = 0; x < WorkbenchConfig.GRID_SIZE; x++)
        {
            for (int y = 0; y < WorkbenchConfig.GRID_SIZE; y++)
            {
                int recipeX = x - startX;
                int recipeY = y - startY;
                Ingredient ingredient = Ingredient.EMPTY;

                if (recipeX >= 0 && recipeY >= 0 && recipeX < width && recipeY < height)
                {
                    if (mirrored) ingredient = recipeItems.get(width - recipeX - 1 + recipeY * width);
                    else ingredient = recipeItems.get(recipeX + recipeY * width);
                }

                ItemStack stack = container.getItem(x + y * WorkbenchConfig.GRID_SIZE);
                if (!ingredient.test(stack)) return false;
            }
        }
        return true;
    }

    private boolean matchesShapeless(Container container)
    {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.addAll(recipeItems);

        for (int i = 0; i < container.getContainerSize(); i++)
        {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty())
            {
                boolean matched = false;
                for (int j = 0; j < ingredients.size(); j++)
                {
                    if (ingredients.get(j).test(stack))
                    {
                        ingredients.remove(j);
                        matched = true;
                        break;
                    }
                }
                if (!matched) return false;
            }
        }
        return ingredients.isEmpty();
    }

    @Override public ItemStack assemble(Container container, RegistryAccess registryAccess) {return result.copy();}

    @Override public boolean canCraftInDimensions(int width, int height) {return width >= WorkbenchConfig.GRID_SIZE && height >= WorkbenchConfig.GRID_SIZE;}

    @Override public ItemStack getResultItem(RegistryAccess registryAccess) {return result;}

    @Override public ResourceLocation getId() {return id;}

    @Override public RecipeSerializer<?> getSerializer() {return ModRecipeSerializers.VANILLA_WORKBENCH_RECIPE.get();}

    @Override public RecipeType<?> getType() {return ModRecipeTypes.VANILLA_WORKBENCH_TYPE.get();}

    @Override public String getGroup() {return group;}

    @Override public NonNullList<Ingredient> getIngredients() {return recipeItems;}

    public static class Serializer implements RecipeSerializer<VanillaWorkbenchRecipe>
    {
        @Override public VanillaWorkbenchRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            String group = GsonHelper.getAsString(json, "group", "");
            CraftingBookCategory category = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(json, "category", null), CraftingBookCategory.MISC);

            if (json.has("pattern")) return fromJsonShaped(recipeId, json, group, category);
            else return fromJsonShapeless(recipeId, json, group, category);
        }

        private VanillaWorkbenchRecipe fromJsonShaped(ResourceLocation recipeId, JsonObject json, String group, CraftingBookCategory category)
        {
            JsonArray pattern = GsonHelper.getAsJsonArray(json, "pattern");
            String[] astring = new String[pattern.size()];

            for (int i = 0; i < astring.length; ++i)
            {
                String s = GsonHelper.convertToString(pattern.get(i), "pattern[" + i + "]");
                astring[i] = s;
            }

            JsonObject keyJson = GsonHelper.getAsJsonObject(json, "key");
            Map<String, Ingredient> map = keyFromJson(keyJson);

            int width = astring[0].length();
            int height = astring.length;
            NonNullList<Ingredient> ingredients = dissolvePattern(astring, map, width, height);
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            return new VanillaWorkbenchRecipe(recipeId, group, category, ingredients, result, true, width, height);
        }

        private VanillaWorkbenchRecipe fromJsonShapeless(ResourceLocation recipeId, JsonObject json, String group, CraftingBookCategory category)
        {
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientsJson.size(), Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); ++i) ingredients.set(i, Ingredient.fromJson(ingredientsJson.get(i)));

            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new VanillaWorkbenchRecipe(recipeId, group, category, ingredients, result, false, 0, 0);
        }

        private static Map<String, Ingredient> keyFromJson(JsonObject keyJson)
        {
            Map<String, Ingredient> map = new java.util.HashMap<>();
            for (Map.Entry<String, com.google.gson.JsonElement> entry : keyJson.entrySet()) map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
            map.put(" ", Ingredient.EMPTY);
            return map;
        }

        private static NonNullList<Ingredient> dissolvePattern(String[] pattern, Map<String, Ingredient> keys, int patternWidth, int patternHeight) {NonNullList<Ingredient> ingredients = NonNullList.withSize(patternWidth * patternHeight, Ingredient.EMPTY);

            for (int i = 0; i < pattern.length; ++i)
            {
                String line = pattern[i];
                for (int j = 0; j < line.length(); ++j)
                {
                    String key = String.valueOf(line.charAt(j));
                    Ingredient ingredient = keys.get(key);
                    ingredients.set(j + patternWidth * i, ingredient);
                }
            }
            return ingredients;
        }

        @Override public @Nullable VanillaWorkbenchRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);
            boolean shaped = buffer.readBoolean();
            int width = shaped ? buffer.readVarInt() : 0;
            int height = shaped ? buffer.readVarInt() : 0;

            int ingredientCount = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);

            for (int i = 0; i < ingredientCount; i++) ingredients.set(i, Ingredient.fromNetwork(buffer));

            ItemStack result = buffer.readItem();

            return new VanillaWorkbenchRecipe(recipeId, group, category, ingredients, result, shaped, width, height);
        }

        @Override public void toNetwork(FriendlyByteBuf buffer, VanillaWorkbenchRecipe recipe)
        {
            buffer.writeUtf(recipe.group);
            buffer.writeEnum(recipe.category);
            buffer.writeBoolean(recipe.shaped);
            if (recipe.shaped)
            {
                buffer.writeVarInt(recipe.width);
                buffer.writeVarInt(recipe.height);
            }

            buffer.writeVarInt(recipe.recipeItems.size());
            for (Ingredient ingredient : recipe.recipeItems) ingredient.toNetwork(buffer);

            buffer.writeItem(recipe.result);
        }
    }
}