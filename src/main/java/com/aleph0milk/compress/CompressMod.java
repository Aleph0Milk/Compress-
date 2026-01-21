package com.aleph0milk.compress;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
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

    // アイテム登録のためのレジストリ
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // 圧縮アイテム本体の登録
    public static final RegistryObject<Item> COMPRESSED_ITEM = ITEMS.register("compressed_item", 
        () -> new CompressedItem(new Item.Properties()));

    public CompressMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // レジストリをイベントバスに登録
        ITEMS.register(modEventBus);
        
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Compress! Mod has been initialized.");
    }
}
