package com.aleph0milk.compress;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item; // 追加
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
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
    
    // アイテムレジストリを追加
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = 
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    // 【重要】圧縮アイテムの実体登録
    // これにより CompressionUtils から参照可能になります
    public static final RegistryObject<Item> COMPRESSED_ITEM = ITEMS.register("compressed_item", 
        () -> CompressedItem.INSTANCE);

    // レシピシリアライザー
    public static final RegistryObject<SimpleCraftingRecipeSerializer<DynamicCompressionRecipe>> COMPRESSION_SERIALIZER = 
        SERIALIZERS.register("dynamic_compression", 
        () -> new SimpleCraftingRecipeSerializer<>(DynamicCompressionRecipe::new));

    public static final RegistryObject<SimpleCraftingRecipeSerializer<DynamicDecompressionRecipe>> DECOMPRESSION_SERIALIZER = 
        SERIALIZERS.register("dynamic_decompression", 
        () -> new SimpleCraftingRecipeSerializer<>(DynamicDecompressionRecipe::new));


    public CompressMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // アイテムとシリアライザーを登録
        ITEMS.register(modEventBus);
        SERIALIZERS.register(modEventBus);
        
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Compress! Mod has been initialized with CompressedItem.");
    }

    // --- イベントハンドラ ---

    // ツールチップ表示（レベルの可視化）
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        int level = CompressionUtils.getLevel(stack);
        if (level > 0) {
            // ツールチップの2行目あたりに挿入
            event.getToolTip().add(1, Component.literal("§6圧縮レベル: x" + level));
            event.getToolTip().add(2, Component.literal("§c使用や設置はできません"));
        }
    }
    
    // ※設置キャンセル処理は CompressedItem.java 側の InteractionResult.FAIL で
    // 制御されるため、このクラスからは削除しても大丈夫です（二重ガードにしてもOK）。
}
