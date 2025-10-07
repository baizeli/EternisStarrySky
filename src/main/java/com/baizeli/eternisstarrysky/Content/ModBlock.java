package com.baizeli.eternisstarrysky.Content;

import com.baizeli.eternisstarrysky.Content.Workbenchs.VanillaWorkbenchBlock;
import com.baizeli.eternisstarrysky.EternisStarrySky;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlock {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EternisStarrySky.MOD_ID);

    public static final RegistryObject<Block> workbench = BLOCKS.register("workbench",
            () -> new VanillaWorkbenchBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(3.0f, 1200)
                    .requiresCorrectToolForDrops()));
}
