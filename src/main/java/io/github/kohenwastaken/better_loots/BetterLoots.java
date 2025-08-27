package io.github.kohenwastaken.better_loots;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterLoots implements ModInitializer {
	public static final String MOD_ID = "better-loots";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		
		// yerleşik resource pack override
	    var container = net.fabricmc.loader.api.FabricLoader.getInstance()
	            .getModContainer(MOD_ID).orElseThrow();
	    net.fabricmc.fabric.api.resource.ResourceManagerHelper.registerBuiltinResourcePack(
	            new net.minecraft.util.Identifier(MOD_ID, "overrides"),
	            container,
	            net.fabricmc.fabric.api.resource.ResourcePackActivationType.ALWAYS_ENABLED);
	}
}