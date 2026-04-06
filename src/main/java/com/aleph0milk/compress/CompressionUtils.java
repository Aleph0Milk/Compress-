package com.aleph0milk.compress;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class CompressionUtils {
    public static final String TAG_LEVEL = "CompressionLevel";

    public static int getLevel(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) return 0;
        return stack.getTag().getInt(TAG_LEVEL);
    }

    public static ItemStack withLevel(ItemStack stack, int level) {
        // 新しいアイテムは作らず、元のアイテムをコピー
        ItemStack result = stack.copy();
        CompoundTag tag = result.getOrCreateTag();
        
        // 圧縮レベルを書き込む
        tag.putInt(TAG_LEVEL, level);
        
        // 耐久値をリセット（仕様：圧縮すると新品になる）
        if (tag.contains("Damage")) {
            tag.remove("Damage");
        }
        
        // スタックサイズを1に固定（Recipe側で制御しますが、ここでも念のため）
        result.setCount(1);
        
        return result;
    }

    public static boolean isCompressed(ItemStack stack) {
        return getLevel(stack) > 0;
    }
}
