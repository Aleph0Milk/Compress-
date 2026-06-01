// server_scripts/compressed_safe_multiplier.js

ItemEvents.crafted(event => {
    const item = event.item;
    
    // 圧縮レベルの取得
    if (item.nbt && item.nbt.contains('CompressionLevel')) {
        let level = item.nbt.getInt('CompressionLevel');
        
        if (level > 0) {
            // 1. 基本の安全処理（破壊不可と耐久値リセット）
            item.nbt.putByte('Unbreakable', 1);
            item.nbt.remove('Damage');
            
            // 2. アイテムが「素の属性（AttributeModifiers）」をすでに持っているか、Javaの内部データから取得
            // これにより、Mod武器固有の初期ステータスも安全に吸い上げます
            let modifiersList = item.nbt.getList('AttributeModifiers', 10); // 10はCompoundTagの型ID
            
            // もしNBTにまだ属性データがない（バニラのデフォルト状態）なら、Javaから素のデータを吸い上げてNBT化する
            if (modifiersList.isEmpty()) {
                let slotTypes = [
                    global.net.minecraft.world.entity.EquipmentSlot.MAINHAND, // 武器・ツール用
                    global.net.minecraft.world.entity.EquipmentSlot.CHEST,    // 胴防具用
                    global.net.minecraft.world.entity.EquipmentSlot.LEGS,     // 脚防具用
                    global.net.minecraft.world.entity.EquipmentSlot.FEET,     // 足防具用
                    global.net.minecraft.world.entity.EquipmentSlot.HEAD      // 頭防具用
                ];
                
                // 全スロットの素の属性をチェックしてリストに突っ込む
                slotTypes.forEach(slot => {
                    let attributeMap = item.minecraftItem.getAttributeModifiers(slot);
                    attributeMap.asMap().forEach((attribute, modifiers) => {
                        modifiers.forEach(mod => {
                            let tag = Utils.newMap();
                            tag.put('AttributeName', attribute.getDescriptionId());
                            tag.put('Name', mod.getName());
                            tag.put('Amount', mod.getAmount());
                            tag.put('Operation', mod.getOperation().getValue());
                            tag.put('Slot', slot.getName());
                            
                            // 【超重要】UUIDはそのままコピー（ここを倍化させないのが回避のコツ）
                            let uuidArray = java('net.minecraft.core.UUIDUtil').uuidToIntArray(mod.getId());
                            tag.put('UUID', uuidArray);
                            
                            modifiersList.add(tag);
                        });
                    });
                });
            }
            
            // 3. 【今回の核心】安全なフィルター倍化処理
            // リスト内のすべての属性データをスキャンし、「数値（Amount）」だけを綺麗に倍化する
            for (let i = 0; i < modifiersList.size(); i++) {
                let modifierTag = modifiersList.get(i);
                
                // 元の「素の数値」を取得
                let baseAmount = modifierTag.getDouble('Amount');
                let attributeName = modifierTag.getString('AttributeName');
                
                // 計算：素の数値 * (レベル + 1)
                let newAmount = baseAmount * (level + 1);
                
                // 例外処理：攻撃速度（generic.attack_speed）はマイクラの仕様上「マイナス値」でツールごとに設定されているため、
                // 倍化すると「振るのがめちゃくちゃ遅いツール」になってしまいます。
                // なので、攻撃速度だけは倍化から除外（または少しだけおまけする）処理を入れます。
                if (attributeName.includes('attack_speed')) {
                    // 攻撃速度はそのまま（重さは変わるが、振る速度は維持されるリアリティ）
                    continue; 
                }
                
                // 安全に数値だけを上書き
                modifierTag.putDouble('Amount', newAmount);
            }
            
            // 完成した「安全に倍化された属性リスト」をアイテムのNBTにセット
            if (!modifiersList.isEmpty()) {
                item.nbt.put('AttributeModifiers', modifiersList);
            }
        }
    }
});
