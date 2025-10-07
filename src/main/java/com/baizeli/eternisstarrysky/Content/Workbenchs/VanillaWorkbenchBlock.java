package com.baizeli.eternisstarrysky.Content.Workbenchs;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class VanillaWorkbenchBlock extends BaseEntityBlock {

    public VanillaWorkbenchBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VanillaWorkbenchBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (!level.isClientSide())
        {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof VanillaWorkbenchBlockEntity workbench)
            {
                MenuProvider containerProvider = new MenuProvider()
                {
                    @Override public Component getDisplayName() {return Component.translatable("block.eternisstarrysky.workbench");}
                    @Override public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {return new VanillaWorkbenchMenu(windowId, playerInventory, workbench, pos);}
                };
                NetworkHooks.openScreen((ServerPlayer) player, containerProvider, buf -> {
                    buf.writeBlockPos(pos);
                });
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock())
        {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof VanillaWorkbenchBlockEntity workbench) workbench.drops();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}