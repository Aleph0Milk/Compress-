package com.aleph0milk.compress;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class DynamicDecompressionRecipe extends CustomRecipe {
    public DynamicDecompressionRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    // クラフト盤に「1つだけ」圧縮アイテムが置かれているかチェック
    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack target = ItemStack.EMPTY;
        int count = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                target = stack;
                count++;
            }
        }

        // 1マスだけにアイテムがあり、かつそのレベルが1以上なら解凍可能
        return count == 1 && CompressionUtils.getLevel(target) >= 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                int currentLevel = CompressionUtils.getLevel(stack);
                // レベルを-1して、個数を9個にして返す
                ItemStack result = CompressionUtils.withLevel(stack, currentLevel - 1);
                result.setCount(9);
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CompressMod.DECOMPRESSION_SERIALIZER.get();
    }
}
