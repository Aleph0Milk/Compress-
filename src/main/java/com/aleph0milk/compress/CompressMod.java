package com.aleph0milk.compress;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CompressMod.MODID)
public class CompressMod {
    public static final String MODID = "alephs_compress";
    public static final Logger LOGGER = LogManager.getLogger();

    public CompressMod() {
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Compress! Mod Initialized.");
    }

    @SubscribeEvent
    public void onBlockPlace(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        // ユーティリティを使用して判定
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
        } else if (stack.hasTag() && stack.getTag().contains(CompressionUtils.TAG_LEVEL)) {
            // x0状態の表示
            event.getToolTip().add(1, Component.literal("§7圧縮レベル: x0 (解凍不可)"));
        }
    }
}
