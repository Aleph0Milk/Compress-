package com.aleph0milk.compress;

import net.minecraft.core.NonNullList;
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

    // クラフト盤の9マスがすべて同じアイテム、かつ同じレベルかチェック
    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack first = container.getItem(0);
        if (first.isEmpty()) return false;
        
        // 9マスすべて埋まっているか、かつ最初のスロットと同じアイテムか
        for (int i = 0; i < 9; i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty() || !ItemStack.isSameItemSameTags(stack, first)) {
                return false;
            }
        }
        return true;
    }

    // 実際にクラフトした結果を返す
    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        ItemStack first = container.getItem(0);
        int currentLevel = CompressionUtils.getLevel(first);
        
        // レベルを+1して返す
        return CompressionUtils.withLevel(first, currentLevel + 1);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        // 後ほど登録するシリアライザーを返します
        return null; 
    }
}
