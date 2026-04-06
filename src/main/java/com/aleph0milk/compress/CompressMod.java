package com.aleph0milk.compress;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(CompressMod.MODID)
public class CompressMod {
    public static final String MODID = "alephs_compress";

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = 
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static final RegistryObject<SimpleCraftingRecipeSerializer<DynamicCompressionRecipe>> COMPRESSION_SERIALIZER = 
        SERIALIZERS.register("dynamic_compression", () -> new SimpleCraftingRecipeSerializer<>(DynamicCompressionRecipe::new));

    public static final RegistryObject<SimpleCraftingRecipeSerializer<DynamicDecompressionRecipe>> DECOMPRESSION_SERIALIZER = 
        SERIALIZERS.register("dynamic_decompression", () -> new SimpleCraftingRecipeSerializer<>(DynamicDecompressionRecipe::new));

    public CompressMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        SERIALIZERS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    // --- 圧縮アイテムの全機能を封じるイベント群 ---

    // 1. 設置および右クリック使用の制限
    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (CompressionUtils.isCompressed(event.getItemStack())) {
            event.setCanceled(true); // 設置を阻止
        }
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (CompressionUtils.isCompressed(event.getItemStack())) {
            event.setCanceled(true); // 食べる・投げる等のアクションを阻止
        }
    }

    // 2. 食料や弓などの「継続使用」の開始を阻止
    @SubscribeEvent
    public void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        if (CompressionUtils.isCompressed(event.getItem())) {
            event.setCanceled(true);
        }
    }

    // 3. ツールチップへの警告表示
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        int level = CompressionUtils.getLevel(event.getItemStack());
        if (level > 0) {
            event.getToolTip().add(1, Component.literal("§6圧縮レベル: x" + level));
            event.getToolTip().add(2, Component.literal("§c高密度のため使用や設置はできません"));
        }
    }
}
