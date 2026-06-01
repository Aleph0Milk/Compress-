// server_scripts/compressed_equipment.js

ItemEvents.crafted(event => {
    const item = event.item;
    
    // 圧縮レベルの取得
    if (item.nbt && item.nbt.contains('CompressionLevel')) {
        let level = item.nbt.getInt('CompressionLevel');
        
        if (level > 0) {
            // 1. 基本の安全処理（破壊不可と耐久値リセット）
            item.nbt.putByte('Unbreakable', 1);
            item.nbt.remove('Damage');
            
            // アイテムの既存の属性リストを取得
            let modifiersList = item.nbt.getList('AttributeModifiers', 10); // 10: CompoundTag型ID
            
            // NBTにまだ属性データがない（バニラや他Modのデフォルト状態）なら、初期性能を吸い上げる
            if (modifiersList.isEmpty()) {
                // バニラの全防具スロットとメインハンド
                let slotTypes = [
                    global.net.minecraft.world.entity.EquipmentSlot.MAINHAND,
                    global.net.minecraft.world.entity.EquipmentSlot.CHEST,
                    global.net.minecraft.world.entity.EquipmentSlot.LEGS,
                    global.net.minecraft.world.entity.EquipmentSlot.FEET,
                    global.net.minecraft.world.entity.EquipmentSlot.HEAD
                ];
                
                // バニラ形式の属性を抽出
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
                            
                            // UUIDはそのまま維持（バグ回避の要）
                            let uuidArray = java('net.minecraft.core.UUIDUtil').uuidToIntArray(mod.getId());
                            tag.put('UUID', uuidArray);
                            
                            modifiersList.add(tag);
                        });
                    });
                });
                
                // 【Curios 連携】CuriosModがロードされている場合のみ、アクセサリー属性を吸い上げる
                if (Platform.isModLoaded('curios')) {
                    try {
                        let curiosHelper = java('top.the_one_who_blocks.curios.api.CuriosApi').getCuriosHelper();
                        let curiosModifiers = curiosHelper.getAttributeModifiers(item.minecraftItem);
                        
                        curiosModifiers.asMap().forEach((attribute, modifiers) => {
                            modifiers.forEach(mod => {
                                let tag = Utils.newMap();
                                tag.put('AttributeName', attribute.getDescriptionId());
                                tag.put('Name', mod.getName());
                                tag.put('Amount', mod.getAmount());
                                tag.put('Operation', mod.getOperation().getValue());
                                // アクセサリーはスロット制限（Slotキー）を記述しない、あるいはCuriosのデフォルトに従う
                                
                                let uuidArray = java('net.minecraft.core.UUIDUtil').uuidToIntArray(mod.getId());
                                tag.put('UUID', uuidArray);
                                
                                modifiersList.add(tag);
                            });
                        });
                    } catch (e) {
                        // 万が一の例外エラー時のセーフティ
                        console.error("Failed to load Curios attributes for compressed item: " + e);
                    }
                }
            }
            
            // 2. 属性数値（Amount）の安全フィルター倍化処理
            for (let i = 0; i < modifiersList.size(); i++) {
                let modifierTag = modifiersList.get(i);
                let baseAmount = modifierTag.getDouble('Amount');
                let attributeName = modifierTag.getString('AttributeName');
                
                // 攻撃速度（attack_speed）は倍化すると逆にめちゃくちゃ遅くなるため除外
                if (attributeName.includes('attack_speed')) {
                    continue;
                }
                
                // 性能を「素の数値 * (レベル + 1)」に書き換え
                let newAmount = baseAmount * (level + 1);
                modifierTag.putDouble('Amount', newAmount);
            }
            
            // 3. 完成した属性データをNBTへ反映
            if (!modifiersList.isEmpty()) {
                item.nbt.put('AttributeModifiers', modifiersList);
            }
        }
    }
});
