package de.remadisson.vanillatweaks;

import de.remadisson.vanillatweaks.teleport.commands.TeleportAccept;
import de.remadisson.vanillatweaks.teleport.commands.TeleportDecline;
import de.remadisson.vanillatweaks.teleport.commands.TeleportHereRequest;
import de.remadisson.vanillatweaks.teleport.commands.TeleportRequester;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class VanillaTweaks extends JavaPlugin {

    private static VanillaTweaks plugin;

    @Override
    public void onEnable() {
        plugin = this;
        registerCommands();
        getLogger().info("VanillaTweaks started!");
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("tpa")).setExecutor(new TeleportRequester());
        Objects.requireNonNull(getCommand("tpaaccept")).setExecutor(new TeleportAccept());
        Objects.requireNonNull(getCommand("tpadecline")).setExecutor(new TeleportDecline());
        Objects.requireNonNull(getCommand("tpahere")).setExecutor(new TeleportHereRequest());
    }

    @Override
    public void onDisable() {
        getLogger().info("VanillaTweaks shutdown!");
    }

    public static VanillaTweaks getInstance() {
        return plugin;
    }
}
