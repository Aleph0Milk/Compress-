package com.aleph0milk.compress;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CompressMod.MODID)
public class CompressMod {
    public static final String MODID = "alephs_compress";
    public static final Logger LOGGER = LogManager.getLogger();

    // --- 登録用レジストリ ---
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = 
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    // 圧縮レシピの登録
    public static final RegistryObject<SimpleCraftingRecipeSerializer<DynamicCompressionRecipe>> COMPRESSION_SERIALIZER = 
        SERIALIZERS.register("dynamic_compression", 
        () -> new SimpleCraftingRecipeSerializer<>(DynamicCompressionRecipe::new));

    // 解凍レシピの登録 (ここにまとめました)
    public static final RegistryObject<SimpleCraftingRecipeSerializer<DynamicDecompressionRecipe>> DECOMPRESSION_SERIALIZER = 
        SERIALIZERS.register("dynamic_decompression", 
        () -> new SimpleCraftingRecipeSerializer<>(DynamicDecompressionRecipe::new));


    public CompressMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        SERIALIZERS.register(modEventBus);
        
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Compress! Mod has been initialized safely.");
    }

    // --- イベントハンドラ ---

    @SubscribeEvent
    public void onBlockPlace(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (CompressionUtils.getLevel(stack) >= 1) {
            event.setCanceled(true);
            event.getEntity().displayClientMessage(
                Component.literal("§c圧縮された物質(x1以上)は実体を持たないため設置できません"), true
            );
        }
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        int level = CompressionUtils.getLevel(stack);
        if (level > 0) {
            event.getToolTip().add(1, Component.literal("§6圧縮レベル: x" + level));
        }
    }
}
