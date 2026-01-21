package com.aleph0milk.compress;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
// ...既存のインポート...

@Mod(CompressMod.MODID)
public class CompressMod {
    public static final String MODID = "alephs_compress";
    public static final Logger LOGGER = LogManager.getLogger();

    // レシピシリアライザーのレジストリ
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = 
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    // 圧縮レシピの登録
    public static final RegistryObject<SimpleCraftingRecipeSerializer<DynamicCompressionRecipe>> COMPRESSION_SERIALIZER = 
        SERIALIZERS.register("dynamic_compression", 
        () -> new SimpleCraftingRecipeSerializer<>(DynamicCompressionRecipe::new));

    public CompressMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        SERIALIZERS.register(modEventBus); // 忘れずに登録

        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Compress! Mod Initialized.");
    }
    
    // ...onBlockPlace や onItemTooltip はそのまま...
}
