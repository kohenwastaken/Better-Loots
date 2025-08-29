package io.github.kohenwastaken.better_loots;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.item.Item;
import net.minecraft.loot.*;
import net.minecraft.loot.entry.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

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
	    
	    final Item copperPickaxe	= findFirstByPath("copper_pickaxe");
	    final Item copperAxe		= findFirstByPath("copper_axe");
	    final Item copperShovel		= findFirstByPath("copper_shovel");
	    final Item copperSword		= findFirstByPath("copper_sword");
	    final Item copperHelmet		= findFirstByPath("copper_helmet");
	    final Item copperChestplate	= findFirstByPath("copper_chestplate");
	    final Item copperLeggings	= findFirstByPath("copper_leggings");
	    final Item copperBoots		= findFirstByPath("copper_boots");
	    
LOGGER.info("[Better-Loots] detected copper items -> pickaxe{}, axe{}, shovel{}, sword{}, helmet{}, chestplate{}, leggings{}, boots{}",
	    	idOf(copperPickaxe), idOf(copperAxe), idOf(copperShovel), idOf(copperSword),
	   		idOf(copperHelmet), idOf(copperChestplate), idOf(copperLeggings), idOf(copperBoots)
);
	    
	    
	    if (copperPickaxe == null && 
	    	copperAxe == null && 
	    	copperShovel == null && 
	    	copperSword == null &&
	    	copperHelmet == null && 
	    	copperChestplate == null && 
	    	copperLeggings == null && 
	    	copperBoots == null) return;
	    
	    LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
	    	
	    	// weaponsmith: 0[emerald], 1[ingots], 2[pickaxe], 3[axe+sword]
	    	// weaponsmith: 4[helmet], 5[chestplate], 6[leggings], 7[boots]
	    	if (id.equals(WEAPONSMITH)) {
                indexModify(tableBuilder, 2, pool -> addIfNotNull(pool, copperPickaxe, 30));
                indexModify(tableBuilder, 3, pool -> 
                {
                    addIfNotNull(pool, copperAxe,   30);
                    addIfNotNull(pool, copperSword, 30);
                });
                indexModify(tableBuilder, 4, pool -> addIfNotNull(pool, copperHelmet, 30));
                indexModify(tableBuilder, 5, pool -> addIfNotNull(pool, copperChestplate, 30));
                indexModify(tableBuilder, 6, pool -> addIfNotNull(pool, copperLeggings, 30));
                indexModify(tableBuilder, 7, pool -> addIfNotNull(pool, copperBoots, 30));
	    	}
	    	
	    	// toolsmith: 0[emerald], 1[ingots], 2[pickaxe], 3[axe], 4[shovel]
	    	if (id.equals(TOOLSMITH)) {
                indexModify(tableBuilder, 2, pool -> addIfNotNull(pool, copperPickaxe, 30));
                indexModify(tableBuilder, 3, pool -> addIfNotNull(pool, copperAxe, 30));
                indexModify(tableBuilder, 4, pool -> addIfNotNull(pool, copperShovel, 30));
	    	}
	    });
	}
	
	
	private static Item findFirstByPath(String wantedPath) {
		
		for (Item item : Registries.ITEM) {
			var id = Registries.ITEM.getId(item);
            if (id != null && wantedPath.equals(id.getPath())) return item;
		}
		return null;
	}
	
	private static void addIfNotNull(LootPool.Builder pool, Item item, int weight) {
        if (item != null) {
            pool.with(ItemEntry.builder(item).weight(weight).build());
        }
	}
	
	 private static void indexModify(LootTable.Builder tableBuilder, int targetIndex,
			 Consumer<LootPool.Builder> edit) {
		 final int[] i = {0};
	        ((FabricLootTableBuilder)(Object) tableBuilder).modifyPools(poolBuilder -> {
	            if (i[0]++ == targetIndex) edit.accept(poolBuilder);
	        });
	 }
	
	 private static String idOf(Item item) {
	        return item == null ? "none" : Registries.ITEM.getId(item).toString();
	 }
}