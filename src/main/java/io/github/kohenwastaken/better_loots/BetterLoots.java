package io.github.kohenwastaken.better_loots;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.entry.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import io.github.kohenwastaken.better_loots.mixin.LootPoolBuilderAccessor;
import io.github.kohenwastaken.better_loots.mixin.ItemEntryAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class BetterLoots implements ModInitializer {

public static final String MOD_ID = "better-loots";
// get mod name as logger name
public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

public static final Identifier WEAPONSMITH = new Identifier("minecraft", "chests/village/village_weaponsmith");
public static final Identifier TOOLSMITH   = new Identifier("minecraft", "chests/village/village_toolsmith");
	
@Override
public void onInitialize() {
	// This code runs as soon as Minecraft is in a mod-load-ready state.
	// However, some things (like resources) may still be uninitialized.
	
	// built-in pack register
	var container = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
	ResourceManagerHelper.registerBuiltinResourcePack
	(
	    new Identifier(MOD_ID, "overrides"),
	    container,
	    ResourcePackActivationType.ALWAYS_ENABLED
	);
	    
	final Item copperPick	 = findFirstByPath("copper_pickaxe");
	final Item copperAxe	 = findFirstByPath("copper_axe");
	final Item copperShovel	 = findFirstByPath("copper_shovel");
	final Item copperSword	 = findFirstByPath("copper_sword");
	final Item copperHelmet	 = findFirstByPath("copper_helmet");
	final Item copperCplate	 = findFirstByPath("copper_chestplate");
	final Item copperLegs	 = findFirstByPath("copper_leggings");
	final Item copperBoots	 = findFirstByPath("copper_boots");
	    
LOGGER.info("[Better-Loots] detected copper items -> pickaxe{}, axe{}, shovel{}, sword{}, helmet{}, chestplate{}, leggings{}, boots{}",
	    	idOf(copperPick), idOf(copperAxe), idOf(copperShovel), idOf(copperSword),
	   		idOf(copperHelmet), idOf(copperCplate), idOf(copperLegs), idOf(copperBoots)
);


	if (copperPick == null && copperAxe == null && copperShovel == null && copperSword == null &&
		copperHelmet == null && copperCplate == null && copperLegs == null && copperBoots == null) return;

	LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
		((FabricLootTableBuilder)(Object) tableBuilder).modifyPools(poolBuilder -> {
		    var entries = ((LootPoolBuilderAccessor)(Object) poolBuilder).betterloots$getEntries();

			boolean hasStonePick   = containsItem(entries, Items.STONE_PICKAXE);
			boolean hasStoneAxe    = containsItem(entries, Items.STONE_AXE);
			boolean hasStoneShovel = containsItem(entries, Items.STONE_SHOVEL);
			boolean hasStoneSword  = containsItem(entries, Items.STONE_SWORD);

			boolean hasChainHelm   = containsItem(entries, Items.CHAINMAIL_HELMET);
			boolean hasChainChest  = containsItem(entries, Items.CHAINMAIL_CHESTPLATE);
			boolean hasChainLegs   = containsItem(entries, Items.CHAINMAIL_LEGGINGS);
			boolean hasChainBoots  = containsItem(entries, Items.CHAINMAIL_BOOTS);

			if (id.equals(TOOLSMITH)) 
			{
				if (hasStonePick)    addIfNotNull(poolBuilder, copperPick, 30);
				if (hasStoneAxe)   	 addIfNotNull(poolBuilder, copperAxe, 30);
				if (hasStoneShovel)  addIfNotNull(poolBuilder, copperShovel, 30);
			}
			if (id.equals(WEAPONSMITH))
			{
				if (hasStonePick) addIfNotNull(poolBuilder, copperPick, 30);
				if ((hasStoneAxe || hasStoneSword)) 
				{
					addIfNotNull(poolBuilder, copperAxe, 30);
					addIfNotNull(poolBuilder, copperSword, 30);
				}
				if (hasChainHelm)  addIfNotNull(poolBuilder, copperHelmet, 30);
				if (hasChainChest) addIfNotNull(poolBuilder, copperCplate, 30);
				if (hasChainLegs)  addIfNotNull(poolBuilder, copperLegs, 30);
				if (hasChainBoots) addIfNotNull(poolBuilder, copperBoots, 30);
			}
            });
        });
	}
	
	private static Item findFirstByPath(String wantedPath) {
		
		for (Item it : Registries.ITEM) {
			var id = Registries.ITEM.getId(it);
            if (id != null && wantedPath.equals(id.getPath())) return it;
		}
		return null;
	}
	
	private static void addIfNotNull(LootPool.Builder pool, Item item, int weight) {
	    if (item != null) {
	        pool.with(ItemEntry.builder(item).weight(weight).build());
	    }
	}

	private static String idOf(Item item) {
	    return item == null ? "none" : Registries.ITEM.getId(item).toString();
	}
	
	private static boolean containsItem(List<LootPoolEntry> entries, Item wanted) {
	    for (LootPoolEntry e : entries) {
	        if (e instanceof ItemEntry ie) {
	            Item entryItem = ((ItemEntryAccessor)(Object) ie).betterloots$getItem();
	            if (entryItem == wanted) return true;
	        }
	    }
	    return false;
	}

	
}