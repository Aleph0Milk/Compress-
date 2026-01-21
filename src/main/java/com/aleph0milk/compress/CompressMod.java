package com.aleph0milk.compress;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CompressMod.MODID)
public class CompressMod {
    public static final String MODID = "alephs_compress";
    public static final Logger LOGGER = LogManager.getLogger();

    public CompressMod() {
        // 全アイテムを対象にするため、レジストリ登録(ITEMS)は削除しました
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Compress! Mod has been initialized.");
    }

    // ルール1: 設置の禁止
    @SubscribeEvent
    public void onBlockPlace(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.hasTag() && stack.getTag().getInt("CompressionLevel") >= 1) {
            event.setCanceled(true);
            // 設置しようとした時に警告を出す（オプション）
            event.getEntity().displayClientMessage(Component.literal("§c圧縮された物質は実体を持たないため設置できません"), true);
        }
    }

    // ルール2: 名前の書き換え (アイテムを追加しない代わりに表示をフックする)
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.hasTag() && stack.getTag().contains("CompressionLevel")) {
            int level = stack.getTag().getInt("CompressionLevel");
            // ツールチップの先頭（アイテム名の下）に「圧縮レベル」を表示
            event.getToolTip().add(1, Component.literal("§6圧縮レベル: x" + level));
        }
    }
}
