package com.aleph0milk.compress;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class CompressionUtils {
    public static final String TAG_LEVEL = "CompressionLevel";

    // 現在の圧縮レベルを取得（NBTがない場合は0）
    public static int getLevel(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) return 0;
        return stack.getTag().getInt(TAG_LEVEL);
    }

    // 指定したレベルを付与したItemStackを返す
    public static ItemStack withLevel(ItemStack stack, int level) {
        ItemStack result = stack.copy();
        CompoundTag tag = result.getOrCreateTag();
        tag.putInt(TAG_LEVEL, level);
        return result;
    }

    // 圧縮可能かどうかの判定（ルール：x0は解凍不可などのロジックをここに集約できる）
    public static boolean isCompressed(ItemStack stack) {
        return getLevel(stack) > 0;
    }
}
