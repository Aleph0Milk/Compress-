package com.aleph0milk.compress;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class DynamicCompressionRecipe extends CustomRecipe {
    public DynamicCompressionRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        // 全スロット(0-8)が埋まっているか確認
        ItemStack first = container.getItem(0);
        if (first.isEmpty()) return false;

        // 【追加】すでにint最大値なら、これ以上圧縮させない
        if (CompressionUtils.getLevel(first) >= Integer.MAX_VALUE) {
            return false;
        }
        
        // 9マスすべて埋まっているか、かつ最初のスロットと同じアイテム（NBT含む）か
        for (int i = 0; i < 9; i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty() || !ItemStack.isSameItemSameTags(stack, first)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        // 最初のスロットから情報をコピー
        ItemStack first = container.getItem(0);
        int currentLevel = CompressionUtils.getLevel(first);
        
        // 安全のためにここでも判定
        int nextLevel = (currentLevel == Integer.MAX_VALUE) ? Integer.MAX_VALUE : currentLevel + 1;
        
        // 新しいアイテムを作成
        ItemStack result = CompressionUtils.withLevel(first, nextLevel);
        
        // 【重要】出力数を必ず「1」に固定する
        // これを忘れると、素材のスタック数分だけ完成品が出てしまい、増殖バグになります
        result.setCount(1);
        
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CompressMod.COMPRESSION_SERIALIZER.get();
    }
}
