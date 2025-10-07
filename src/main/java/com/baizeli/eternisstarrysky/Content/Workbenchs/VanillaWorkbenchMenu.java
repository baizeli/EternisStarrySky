package com.baizeli.eternisstarrysky.Content.Workbenchs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Optional;

public class VanillaWorkbenchMenu extends AbstractContainerMenu {

    @Override public boolean stillValid(Player player) {return stillValid(access, player, blockEntity.getBlockState().getBlock());}
    public final VanillaWorkbenchBlockEntity blockEntity;
    private final Level level;
    private final ContainerLevelAccess access;

    public VanillaWorkbenchMenu(int windowId, Inventory inv, VanillaWorkbenchBlockEntity entity, BlockPos pos)
    {
        super(ModMenuTypes.VANILLA_WORKBENCH_MENU.get(), windowId);
        blockEntity = entity;
        this.level = inv.player.level();
        this.access = ContainerLevelAccess.create(level, pos);
        Container container = new SimpleContainer(81);
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler ->
        {
            for (int row = 0; row < WorkbenchConfig.GRID_SIZE; row++)
            {
                for (int col = 0; col < WorkbenchConfig.GRID_SIZE; col++)
                {
                    int index = row * WorkbenchConfig.GRID_SIZE + col;
                    int sx = EternisWorkbench.WORKBENCH_SLOT_X + col * 18;
                    int sy = EternisWorkbench.WORKBENCH_SLOT_Y + row * 18;
                    this.addSlot(new EternisWorkbenchSlot(iItemHandler, index, sx, sy));
                    // this.addSlot(new Slot(container, index, sx, sy));
                }
            }
            this.addSlot(new VanillaWorkbenchResultSlot(iItemHandler, WorkbenchConfig.RESULT_SLOT, 210, 99));
        });

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem())
        {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            int craftingSlotsEnd = WorkbenchConfig.GRID_SIZE * WorkbenchConfig.GRID_SIZE;
            int resultSlotIndex = craftingSlotsEnd;
            int inventoryStart = resultSlotIndex + 1;
            int inventoryEnd = inventoryStart + 36;

            if (index == resultSlotIndex)
            {
                // 添加材料消耗逻辑
                if (!this.moveItemStackTo(stack, inventoryStart, inventoryEnd, true)) {
                    return ItemStack.EMPTY;
                }

                access.execute((level, pos) -> {
                    stack.getItem().onCraftedBy(stack, level, playerIn);
                    // 消耗材料
                    blockEntity.consumeIngredientsForResult(playerIn, stack);
                });

                slot.onQuickCraft(stack, itemstack);
            }
            else if (index >= inventoryStart && index < inventoryEnd) {
                if (!this.moveItemStackTo(stack, 0, craftingSlotsEnd, false)) return ItemStack.EMPTY;
            }
            else if (index >= 0 && index < craftingSlotsEnd) {
                if (!this.moveItemStackTo(stack, inventoryStart, inventoryEnd, false)) return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemstack;
    }


    private void addPlayerInventory(Inventory playerInventory)
    {
        // 背包 (3x9)
        int px = ((EternisWorkbench.ASSETS_WORKBENCH_WIDTH - EternisWorkbench.ASSETS_INVENTORY_WIDTH) >> 1) + EternisWorkbench.INVENTORY_SLOT_X;
        int py = EternisWorkbench.ASSETS_WORKBENCH_HEIGHT + EternisWorkbench.INVENTORY_SLOT_Y;
        for (int i = 0; i < 3; ++i)
        {
            for (int l = 0; l < 9; ++l)
            {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, px + l * 18, py + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory)
    {
        // 快捷栏 (1x9)
        int px = ((EternisWorkbench.ASSETS_WORKBENCH_WIDTH - EternisWorkbench.ASSETS_INVENTORY_WIDTH) >> 1) + EternisWorkbench.INVENTORY_SLOT_X;
        int py = EternisWorkbench.ASSETS_WORKBENCH_HEIGHT + EternisWorkbench.INVENTORY_SLOT_Y;
        py += 3 * 18 + 4;
        for (int i = 0; i < 9; ++i)
        {
            this.addSlot(new Slot(playerInventory, i, px + i * 18, py));
        }
    }

    private class VanillaWorkbenchResultSlot extends SlotItemHandler {
        public VanillaWorkbenchResultSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            blockEntity.onResultTaken(player, stack);
            super.onTake(player, stack);
        }

        @Override
        public ItemStack remove(int amount) {
            if (hasItem()) {
                // 在移除之前先消耗材料
                consumeIngredientsForCrafting(amount);
            }
            return super.remove(amount);
        }

        private void consumeIngredientsForCrafting(int craftCount) {
            // 获取当前匹配的配方
            Optional<VanillaWorkbenchRecipe> recipeOpt = getCurrentRecipe();
            if (recipeOpt.isPresent()) {
                VanillaWorkbenchRecipe recipe = recipeOpt.get();

                // 消耗材料
                for (int i = 0; i < craftCount; i++) {
                    if (!hasValidRecipe()) break; // 如果材料不够了就停止
                    consumeIngredients(recipe);
                }
            }
        }

        private Optional<VanillaWorkbenchRecipe> getCurrentRecipe() {
            // 创建一个Container来检查配方
            Container craftingContainer = new SimpleContainer(WorkbenchConfig.GRID_SIZE * WorkbenchConfig.GRID_SIZE);

            // 从工作台slots复制物品到container
            for (int i = 0; i < WorkbenchConfig.GRID_SIZE * WorkbenchConfig.GRID_SIZE; i++) {
                if (i < VanillaWorkbenchMenu.this.slots.size()) {
                    craftingContainer.setItem(i, VanillaWorkbenchMenu.this.slots.get(i).getItem().copy());
                }
            }

            // 查找匹配的配方
            return level.getRecipeManager().getRecipeFor(ModRecipeTypes.VANILLA_WORKBENCH_TYPE.get(), craftingContainer, level);
        }

        private boolean hasValidRecipe() {
            return getCurrentRecipe().isPresent();
        }

        private void consumeIngredients(VanillaWorkbenchRecipe recipe) {
            // 根据配方消耗材料
            if (recipe.shaped) {
                consumeShapedIngredients(recipe);
            } else {
                consumeShapelessIngredients(recipe);
            }
        }

        private void consumeShapedIngredients(VanillaWorkbenchRecipe recipe) {
            NonNullList<Ingredient> recipeItems = recipe.getIngredients();
            int recipeWidth = recipe.width;
            int recipeHeight = recipe.height;
            int gridSize = WorkbenchConfig.GRID_SIZE;

            // 找到配方在工作台中的位置
            for (int startX = 0; startX <= gridSize - recipeWidth; startX++) {
                for (int startY = 0; startY <= gridSize - recipeHeight; startY++) {
                    if (matchesShapedAt(recipe, startX, startY)) {
                        // 在这个位置消耗材料
                        for (int i = 0; i < recipeItems.size(); i++) {
                            Ingredient ingredient = recipeItems.get(i);
                            if (!ingredient.isEmpty()) {
                                int recipeX = i % recipeWidth;
                                int recipeY = i / recipeWidth;
                                int slotIndex = (startY + recipeY) * gridSize + (startX + recipeX);

                                if (slotIndex < VanillaWorkbenchMenu.this.slots.size()) {
                                    Slot slot = VanillaWorkbenchMenu.this.slots.get(slotIndex);
                                    if (slot.hasItem() && ingredient.test(slot.getItem())) {
                                        slot.getItem().shrink(1);
                                    }
                                }
                            }
                        }
                        return;
                    }
                }
            }
        }

        private void consumeShapelessIngredients(VanillaWorkbenchRecipe recipe) {
            NonNullList<Ingredient> ingredientsToConsume = NonNullList.create();
            ingredientsToConsume.addAll(recipe.getIngredients());

            for (int i = 0; i < WorkbenchConfig.GRID_SIZE * WorkbenchConfig.GRID_SIZE && !ingredientsToConsume.isEmpty(); i++) {
                if (i < VanillaWorkbenchMenu.this.slots.size()) {
                    Slot slot = VanillaWorkbenchMenu.this.slots.get(i);
                    if (slot.hasItem()) {
                        for (int j = 0; j < ingredientsToConsume.size(); j++) {
                            if (ingredientsToConsume.get(j).test(slot.getItem())) {
                                slot.getItem().shrink(1);
                                ingredientsToConsume.remove(j);
                                break;
                            }
                        }
                    }
                }
            }
        }

        private boolean matchesShapedAt(VanillaWorkbenchRecipe recipe, int startX, int startY) {
            NonNullList<Ingredient> recipeItems = recipe.getIngredients();
            int recipeWidth = recipe.width;
            int recipeHeight = recipe.height;
            int gridSize = WorkbenchConfig.GRID_SIZE;

            for (int i = 0; i < recipeItems.size(); i++) {
                Ingredient ingredient = recipeItems.get(i);
                int recipeX = i % recipeWidth;
                int recipeY = i / recipeWidth;
                int slotIndex = (startY + recipeY) * gridSize + (startX + recipeX);

                if (slotIndex < VanillaWorkbenchMenu.this.slots.size()) {
                    ItemStack slotStack = VanillaWorkbenchMenu.this.slots.get(slotIndex).getItem();
                    if (!ingredient.test(slotStack)) {
                        return false;
                    }
                } else if (!ingredient.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }
}