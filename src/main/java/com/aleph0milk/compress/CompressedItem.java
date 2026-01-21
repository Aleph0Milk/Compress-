package com.aleph0milk.compress;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CompressedItem extends Item {
    public CompressedItem(Properties properties) {
        super(properties);
    }

    // アイテム名に「圧縮された[アイテム名] x[レベル]」を表示するための処理
    @Override
    public Component getName(ItemStack stack) {
        int level = stack.getOrCreateTag().getInt("CompressionLevel");
        // 本来はここでもっと複雑な名前解決をしますが、まずは基本形
        return Component.translatable("item.alephs_compress.compressed_item.format", level);
    }

    // 説明文（ツールチップ）に元のアイテム情報を出す
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (stack.hasTag()) {
            String original = stack.getTag().getString("OriginalItem");
            tooltip.add(Component.literal("Original: " + original));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
