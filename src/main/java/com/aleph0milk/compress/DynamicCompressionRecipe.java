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

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack first = container.getItem(0);
        if (first.isEmpty()) return false;

        // レベル制限（int最大値まで）
        int firstLevel = CompressionUtils.getLevel(first);
        if (firstLevel >= Integer.MAX_VALUE) {
            return false;
        }
        
        for (int i = 0; i < 9; i++) {
            ItemStack stack = container.getItem(i);
            // 【厳格化】スロットが空、またはアイテムID/バニラタグが異なる場合は不可
            if (stack.isEmpty() || !ItemStack.isSameItemSameTags(stack, first)) {
                return false;
            }
            // 【厳格化】圧縮レベル（CompressionLevel）が1つでも異なるアイテムが混ざっている場合は不可
            // これにより、通常のレシピ側がNBTを無視していても、こちらの圧縮レシピ側が「同じ圧縮度同士のクラフト」以外を完全にシャットアウトします
            if (CompressionUtils.getLevel(stack) != firstLevel) {
                // ※これにより「通常のクラフト（非圧縮アイテムのレシピ）」に圧縮アイテムが誤流用されるのを間接的に防止、
                // かつレベルが異なる圧縮アイテム（例：x1とx2）を混ぜてクラフトすることを鉄壁ガードします
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        ItemStack first = container.getItem(0);
        int currentLevel = CompressionUtils.getLevel(first);
        
        int nextLevel = (currentLevel == Integer.MAX_VALUE) ? Integer.MAX_VALUE : currentLevel + 1;
        
        // 圧縮された新しいアイテムを生成
        ItemStack result = CompressionUtils.withLevel(first, nextLevel);
        
        // 【修正】出力数を1に固定（20→19→... の増殖バグ対策）
        result.setCount(1);
        
        return result;
    }

    /**
     * 【修正】バケツ等の容器アイテムを返却せず、完全に消滅させる
     */
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        return NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
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
