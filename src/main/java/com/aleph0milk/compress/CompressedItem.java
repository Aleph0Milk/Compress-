package com.aleph0milk.compress;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class CompressedItem extends Item {
    public static final CompressedItem INSTANCE = new CompressedItem(new Item.Properties());

    public CompressedItem(Properties properties) {
        // スタック数は基本64に固定（元のアイテムが1個制限でも圧縮すればまとまる仕様）
        super(properties.stacksTo(64));
    }

    /**
     * 右クリック（空中）での使用を禁止（食べる、投げる、振る等の防止）
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // PASSを返すことで、右クリックしても何も起きないようにする
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    /**
     * 右クリック（ブロックに対して）の使用を禁止（設置、耕す、バケツを汲む等の防止）
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        // FAILを返すことで、設置処理などを完全に遮断する
        return InteractionResult.FAIL;
    }

    /**
     * 食料アニメーションを無効化
     */
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    /**
     * 耐久値バーを非表示にする
     */
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    /**
     * 採掘速度を「素手」レベルまで落とす（ツールとしての機能を無効化）
     */
    @Override
    public float getDestroySpeed(ItemStack stack, net.minecraft.world.level.block.state.BlockState state) {
        return 1.0F;
    }

    /**
     * エンチャントを不可能にする（オプション）
     */
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
