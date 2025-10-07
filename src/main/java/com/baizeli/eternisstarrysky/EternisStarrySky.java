package com.baizeli.eternisstarrysky;

import com.baizeli.eternisstarrysky.Content.ModBlock;
import com.baizeli.eternisstarrysky.Content.Workbenchs.*;
import com.baizeli.eternisstarrysky.Entity.ModEntities;
import com.baizeli.eternisstarrysky.Entity.SwordManCsdyRenderer;
import com.baizeli.eternisstarrysky.Items.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(EternisStarrySky.MOD_ID)
public class EternisStarrySky
{

    public static final String MOD_ID = "eternisstarrysky";
    public static final String MODID = MOD_ID; // 添加这个别名以保持兼容性
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);


    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("eternisstarrysky_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .title(Component.translatable("itemGroup.eternisstarrysky.eternisstarrysky_tab"))
            .icon(() -> ModItems.PURPLEITE_GALAXY_INGOT.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.ETERNIS_APPLE.get());
                output.accept(ModItems.PEACH.get());
                output.accept(ModItems.PURPLEITE_GALAXY_INGOT.get());
                output.accept(ModItems.WHISPER_OF_THE_PAST.get());
                output.accept(ModItems.PRIMOGEM.get());
                output.accept(ModItems.CREATE_STAR.get());
                output.accept(ModItems.GALAXY_SCROLL.get());
                output.accept(ModItems.IMPURE_FRUIT.get());
                output.accept(ModItems.PURE_FRUIT.get());

                output.accept(ModItems.INFINITY_SWORD.get());
                output.accept(ModItems.INFINITY_ETERNAL_HELMET.get());
                output.accept(ModItems.INFINITY_ETERNAL_CHESTPLATE.get());
                output.accept(ModItems.INFINITY_ETERNAL_LEGGINGS.get());
                output.accept(ModItems.INFINITY_ETERNAL_BOOTS.get());

                output.accept(ModItems.WORKBENCH.get());
            }).build());

    public EternisStarrySky(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModBlock.BLOCKS.register(modEventBus);
        SoundsRegister.SOUND_EVENTS.register(modEventBus);

        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            modEventBus.addListener(ModKeyBindings::onRegisterKeyMappings);
        }

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addAttribute); // 添加实体属性创建事件监听器

        MinecraftForge.EVENT_BUS.register(this);
        context.registerConfig(ModConfig.Type.COMMON, Configuration.SPECIFICATION);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            NetworkHandler.register();
            LOGGER.info("Fuck TTTTTT");
        });
    }


    // 添加实体属性创建事件处理程序
    private void addAttribute(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SWORD_MAN_CSDY.get(), 
                  Mob.createMobAttributes()
                     .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 4.4)
                     .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 10000.0)
                     .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 1600.0)
                     .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED, 20.0)
                     .add(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE, 128)
                     .build());
    }

    public static String resource(String location)
    {
        return MOD_ID + ":" + location;
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(() -> {
                MenuScreens.register(ModMenuTypes.VANILLA_WORKBENCH_MENU.get(), VanillaWorkbenchScreen::new);
                EntityRenderers.register(ModEntities.SWORD_MAN_CSDY.get(), SwordManCsdyRenderer::new);
            });
            MinecraftForge.registerConfigScreen(new ConfigurationFactory());
        }
    }
}