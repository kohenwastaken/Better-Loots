package io.github.kohenwastaken.better_loots;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import io.github.kohenwastaken.better_loots.mixin.LootPoolBuilderAccessor;
import io.github.kohenwastaken.better_loots.mixin.ItemEntryAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class BetterLoots implements ModInitializer {

public static final String MOD_ID = "better-loots";
public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

public static final Identifier WEAPONSMITH = new Identifier("minecraft", "chests/village/village_weaponsmith");
public static final Identifier TOOLSMITH   = new Identifier("minecraft", "chests/village/village_toolsmith");
	
@Override
public void onInitialize() {
	
	var container = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
	ResourceManagerHelper.registerBuiltinResourcePack(
	    new Identifier(MOD_ID, "overrides"),
	    container,
	    ResourcePackActivationType.ALWAYS_ENABLED
	);
	    
	final Item copperPick   = findFirstGear("copper", "pickaxe");
    final Item copperAxe    = findFirstGear("copper", "axe");
    final Item copperShovel = findFirstGear("copper", "shovel");
    final Item copperSword  = findFirstGear("copper", "sword");
    final Item copperHelm   = findFirstGear("copper", "helmet");
    final Item copperChest  = findFirstGear("copper", "chestplate");
    final Item copperLegs   = findFirstGear("copper", "leggings");
    final Item copperBoots  = findFirstGear("copper", "boots");
	
	final Item rosePick   = findFirstGear("rosegold", "pickaxe");
	final Item roseAxe    = findFirstGear("rosegold", "axe");
	final Item roseShovel = findFirstGear("rosegold", "shovel");
	final Item roseSword  = findFirstGear("rosegold", "sword");
	final Item roseHelm   = findFirstGear("rosegold", "helmet");
	final Item roseChest  = findFirstGear("rosegold", "chestplate");
	final Item roseLegs   = findFirstGear("rosegold", "leggings");
	final Item roseBoots  = findFirstGear("rosegold", "boots");
	    
LOGGER.info("[Better-Loots] detected copper items -> pickaxe{}, axe{}, shovel{}, sword{}, helmet{}, chestplate{}, leggings{}, boots{}",
	    	idOf(copperPick), idOf(copperAxe), idOf(copperShovel), idOf(copperSword),
	   		idOf(copperHelm), idOf(copperChest), idOf(copperLegs), idOf(copperBoots)
);
LOGGER.info("[Better-Loots] detected rosegold items -> pickaxe={}, axe={}, shovel={}, sword={}, helmet={}, chest={}, legs={}, boots={}",
			idOf(rosePick), idOf(roseAxe), idOf(roseShovel), idOf(roseSword),
			idOf(roseHelm), idOf(roseChest), idOf(roseLegs), idOf(roseBoots)
);


	if (!hasAnyGear("copper") && !hasAnyGear("rosegold")) return;

	LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
		((FabricLootTableBuilder)(Object) tableBuilder).modifyPools(poolBuilder -> {
			List<LootPoolEntry> entries = ((LootPoolBuilderAccessor)(Object) poolBuilder).betterloots$getEntries();

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
				if (hasChainHelm)  addIfNotNull(poolBuilder, copperHelm, 30);
				if (hasChainChest) addIfNotNull(poolBuilder, copperChest, 30);
				if (hasChainLegs)  addIfNotNull(poolBuilder, copperLegs, 30);
				if (hasChainBoots) addIfNotNull(poolBuilder, copperBoots, 30);
			}
            });
        });
	}
	
	private static Item findFirstByPredicate(Predicate<Identifier> test) 
	{
		for (Item it : Registries.ITEM) 
		{
			Identifier id = Registries.ITEM.getId(it);
			if (id != null && test.test(id)) return it;
		}
		return null;
	}
	
    private static Item findFirstGear(String metal, String suffix) {
        final String wantSuffix = "_" + suffix; // "_pickaxe"
        return findFirstByPredicate(id -> {
            String path = id.getPath(); // "rose_gold_pickaxe"
            if (!path.endsWith(wantSuffix)) return false;

            String base = path.substring(0, path.length() - wantSuffix.length()); // "rose_gold" / "rose_golden"
            String norm = base.toLowerCase(Locale.ROOT).replace("_", "");         // "rosegold" / "rosegolden"

            if ("rosegold".equals(metal)) {
                if (norm.endsWith("golden")) norm = norm.substring(0, norm.length() - 2);
            }
            return norm.endsWith(metal);// "rosegold"
        });
    }
	
    private static boolean hasAnyGear(String metal) {
        String[] suffixes = {
                "pickaxe","axe","shovel","sword",
                "helmet","chestplate","leggings","boots"
        };
        for (String s : suffixes) {
            if (findFirstGear(metal, s) != null) return true;
        }
        return false;
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

    private static void addIfNotNull(LootPool.Builder pool, Item item, int weight) {
        if (item != null) {
            pool.with(ItemEntry.builder(item).weight(weight).build());
        }
    }
	
    private static String idOf(Item item) {
        return item == null ? "none" : Registries.ITEM.getId(item).toString();
    }
	
	



	
}