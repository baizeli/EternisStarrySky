package com.baizeli.eternisstarrysky.Content.Workbenchs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class VanillaWorkbenchBlockEntity extends BlockEntity implements MenuProvider
{
    @Override public Component getDisplayName() {return Component.translatable("container.eternisstarrysky.vanilla_workbench");}
    @Override public @Nullable AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {return new VanillaWorkbenchMenu(windowId, playerInventory, this, worldPosition);}
    public VanillaWorkbenchBlockEntity(BlockPos pos, BlockState state) {super(ModBlockEntities.VANILLA_WORKBENCH.get(), pos, state);}
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private final ItemStackHandler itemHandler = new ItemStackHandler(WorkbenchConfig.TOTAL_SLOTS)
    {
        @Override protected void onContentsChanged(int slot)
        {
            setChanged();
            if (slot < WorkbenchConfig.TOTAL_CRAFTING_SLOTS) updateRecipe();
        }
    };

    @Override public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return lazyItemHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override public void onLoad()
    {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override public void invalidateCaps()
    {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void updateRecipe()
    {
        if (level == null || level.isClientSide()) return;

        SimpleContainer container = new SimpleContainer(WorkbenchConfig.TOTAL_CRAFTING_SLOTS);
        for (int i = 0; i < WorkbenchConfig.TOTAL_CRAFTING_SLOTS; i++) container.setItem(i, itemHandler.getStackInSlot(i));

        Optional<VanillaWorkbenchRecipe> recipe = level.getRecipeManager().getRecipeFor(ModRecipeTypes.VANILLA_WORKBENCH_TYPE.get(), container, level);

        if (recipe.isPresent())
        {
            ItemStack result = recipe.get().assemble(container, level.registryAccess());
            itemHandler.setStackInSlot(WorkbenchConfig.RESULT_SLOT, result);
        }
        else itemHandler.setStackInSlot(WorkbenchConfig.RESULT_SLOT, ItemStack.EMPTY);
    }

    public void onResultTaken(Player player, ItemStack result)
    {
        if (level == null || level.isClientSide()) return;

        SimpleContainer container = new SimpleContainer(WorkbenchConfig.TOTAL_CRAFTING_SLOTS);
        for (int i = 0; i < WorkbenchConfig.TOTAL_CRAFTING_SLOTS; i++) container.setItem(i, itemHandler.getStackInSlot(i));

        Optional<VanillaWorkbenchRecipe> recipe = level.getRecipeManager().getRecipeFor(ModRecipeTypes.VANILLA_WORKBENCH_TYPE.get(), container, level);

        if (recipe.isPresent()) {
            // 消耗材料
            for (int i = 0; i < WorkbenchConfig.TOTAL_CRAFTING_SLOTS; i++)
            {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (!stack.isEmpty())
                {
                    stack.shrink(1);
                    itemHandler.setStackInSlot(i, stack);
                }
            }
            updateRecipe();
        }
    }

    public void drops()
    {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) inventory.setItem(i, itemHandler.getStackInSlot(i));
        Containers.dropContents(level, worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        nbt.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }

    public Optional<VanillaWorkbenchRecipe> getCurrentRecipe() {
        if (level == null) return Optional.empty();

        Container container = new SimpleContainer(itemHandler.getSlots() - 1); // 排除结果槽位
        for (int i = 0; i < container.getContainerSize(); i++) {
            container.setItem(i, itemHandler.getStackInSlot(i));
        }

        return level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.VANILLA_WORKBENCH_TYPE.get(), container, level);
    }

    private boolean matchesShapedAt(VanillaWorkbenchRecipe recipe, int startX, int startY, boolean mirrored) {
        for (int x = 0; x < WorkbenchConfig.GRID_SIZE; x++) {
            for (int y = 0; y < WorkbenchConfig.GRID_SIZE; y++) {
                int recipeX = x - startX;
                int recipeY = y - startY;
                Ingredient ingredient = Ingredient.EMPTY;

                if (recipeX >= 0 && recipeY >= 0 && recipeX < recipe.width && recipeY < recipe.height) {
                    if (mirrored) {
                        ingredient = recipe.getIngredients().get(recipe.width - recipeX - 1 + recipeY * recipe.width);
                    } else {
                        ingredient = recipe.getIngredients().get(recipeX + recipeY * recipe.width);
                    }
                }

                ItemStack stack = itemHandler.getStackInSlot(x + y * WorkbenchConfig.GRID_SIZE);
                if (!ingredient.test(stack)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean matchesShapedRecipe(VanillaWorkbenchRecipe recipe) {
        for (int i = 0; i <= WorkbenchConfig.GRID_SIZE - recipe.width; i++) {
            for (int j = 0; j <= WorkbenchConfig.GRID_SIZE - recipe.height; j++) {
                if (matchesShapedAt(recipe, i, j, false) || matchesShapedAt(recipe, i, j, true)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void consumeIngredientsForResult(Player player, ItemStack result) {
        Optional<VanillaWorkbenchRecipe> recipeOpt = getCurrentRecipe();
        if (recipeOpt.isPresent()) {
            VanillaWorkbenchRecipe recipe = recipeOpt.get();

            if (recipe.shaped) {
                consumeShapedIngredients(recipe);
            } else {
                consumeShapelessIngredients(recipe);
            }

            setChanged();
            if (level != null) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    }

    private void consumeShapedIngredients(VanillaWorkbenchRecipe recipe) {
        for (int i = 0; i <= WorkbenchConfig.GRID_SIZE - recipe.width; i++) {
            for (int j = 0; j <= WorkbenchConfig.GRID_SIZE - recipe.height; j++) {
                boolean normalMatch = matchesShapedAt(recipe, i, j, false);
                boolean mirroredMatch = matchesShapedAt(recipe, i, j, true);

                if (normalMatch || mirroredMatch) {
                    consumeAtPosition(recipe, i, j, mirroredMatch);
                    return;
                }
            }
        }
    }

    private void consumeShapelessIngredients(VanillaWorkbenchRecipe recipe) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.addAll(recipe.getIngredients());

        for (int i = 0; i < itemHandler.getSlots() - 1; i++) { // -1 因为最后一个是结果槽位
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                for (int j = 0; j < ingredients.size(); j++) {
                    if (ingredients.get(j).test(stack)) {
                        itemHandler.extractItem(i, 1, false);
                        ingredients.remove(j);
                        break;
                    }
                }
            }
        }
    }

    private void consumeAtPosition(VanillaWorkbenchRecipe recipe, int startX, int startY, boolean mirrored) {
        for (int x = 0; x < WorkbenchConfig.GRID_SIZE; x++) {
            for (int y = 0; y < WorkbenchConfig.GRID_SIZE; y++) {
                int recipeX = x - startX;
                int recipeY = y - startY;

                if (recipeX >= 0 && recipeY >= 0 && recipeX < recipe.width && recipeY < recipe.height) {
                    Ingredient ingredient;
                    if (mirrored) {
                        ingredient = recipe.getIngredients().get(recipe.width - recipeX - 1 + recipeY * recipe.width);
                    } else {
                        ingredient = recipe.getIngredients().get(recipeX + recipeY * recipe.width);
                    }

                    if (!ingredient.isEmpty()) {
                        int slotIndex = x + y * WorkbenchConfig.GRID_SIZE;
                        itemHandler.extractItem(slotIndex, 1, false);
                    }
                }
            }
        }
    }
}