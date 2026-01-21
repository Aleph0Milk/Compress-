package com.aleph0milk.compress;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// gradle.propertiesで設定したmod_idと一致させる必要があります
@Mod("alephs_compress")
public class CompressMod {
    public static final String MODID = "alephs_compress";
    public static final Logger LOGGER = LogManager.getLogger();

    public CompressMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 今後、アイテムやブロックを登録する際にここを使用します
        
        // システムイベント（設置不可の処理など）を登録するための準備
        MinecraftForge.EVENT_BUS.register(this);
        
        LOGGER.info("Compress! Mod has been initialized.");
    }
}
