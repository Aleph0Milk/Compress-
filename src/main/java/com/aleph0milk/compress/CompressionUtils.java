package com.aleph0milk.compress;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class CompressionUtils {
    public static final String TAG_LEVEL = "CompressionLevel";

    /**
     * 現在の圧縮レベルを取得（NBTがない場合は0）
     */
    public static int getLevel(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) return 0;
        return stack.getTag().getInt(TAG_LEVEL);
    }

    /**
     * 指定したレベルを付与したItemStackを返す
     * 修正：圧縮時にアイテムを「CompressedItem」に入れ替え、耐久値などの不要なNBTを削除する
     */
    public static ItemStack withLevel(ItemStack stack, int level) {
        // 1. 新しい「圧縮専用アイテム」のスタックを作成
        // これにより、元のアイテム（ツール、食料、バケツ等）の挙動を封殺する
        ItemStack result = new ItemStack(CompressMod.COMPRESSED_ITEM.get());
        
        // 2. 元のアイテムのNBTをコピーして持ってくる（名前やカスタム情報を維持するため）
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag().copy();
            
            // 3. 【重要】元のアイテムの「機能」に関わるNBTを削除
            // 耐久値(Damage)や、バケツの中身(Fluid)など、
            // 「使用」できてしまう原因となるデータを掃除する
            tag.remove("Damage");
            tag.remove("Enchantments"); // 圧縮したらエンチャントも消える仕様（高密度化の代償）
            
            result.setTag(tag);
        }
        
        // 4. 圧縮レベルを書き込む
        result.getOrCreateTag().putInt(TAG_LEVEL, level);
        
        return result;
    }

    /**
     * 圧縮可能かどうかの判定
     */
    public static boolean isCompressed(ItemStack stack) {
        return getLevel(stack) > 0;
    }
}
