package de.remadisson.vanillatweaks;

import de.remadisson.vanillatweaks.home.commands.DelHome;
import de.remadisson.vanillatweaks.home.commands.Home;
import de.remadisson.vanillatweaks.home.commands.SetHome;
import de.remadisson.vanillatweaks.listener.HeaderAndFooterList;
import de.remadisson.vanillatweaks.teleport.commands.TeleportAccept;
import de.remadisson.vanillatweaks.teleport.commands.TeleportDecline;
import de.remadisson.vanillatweaks.teleport.commands.TeleportHereRequest;
import de.remadisson.vanillatweaks.teleport.commands.TeleportRequester;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class VanillaTweaks extends JavaPlugin {

    private static VanillaTweaks plugin;

    @Override
    public void onEnable() {
        plugin = this;
        registerCommands();
        registerListener();
        getLogger().info("VanillaTweaks started!");
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new HeaderAndFooterList(), this);
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("tpa")).setExecutor(new TeleportRequester());
        Objects.requireNonNull(getCommand("tpaaccept")).setExecutor(new TeleportAccept());
        Objects.requireNonNull(getCommand("tpadecline")).setExecutor(new TeleportDecline());
        Objects.requireNonNull(getCommand("tpahere")).setExecutor(new TeleportHereRequest());
        Objects.requireNonNull(getCommand("home")).setExecutor(new Home());
        Objects.requireNonNull(getCommand("sethome")).setExecutor(new SetHome());
        Objects.requireNonNull(getCommand("delhome")).setExecutor(new DelHome());
    }

    @Override
    public void onDisable() {
        getLogger().info("VanillaTweaks shutdown!");
    }

    public static VanillaTweaks getInstance() {
        return plugin;
    }
}
