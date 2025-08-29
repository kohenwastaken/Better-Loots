package io.github.kohenwastaken.better_loots.mixin;

import net.minecraft.item.Item;
import net.minecraft.loot.entry.ItemEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemEntry.class)
public interface ItemEntryAccessor {
    @Accessor("item")
    Item betterloots$getItem();
}
