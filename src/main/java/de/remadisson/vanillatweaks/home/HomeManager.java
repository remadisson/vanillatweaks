package de.remadisson.vanillatweaks.home;

import de.remadisson.vanillatweaks.VanillaTweaks;
import de.remadisson.vanillatweaks.file.FileAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeManager {

    private static HomeManager homeManager;
    private HashMap<UUID, HomeUser> homesCache = new HashMap<>();
    private FileAPI homeStorage;

    private final Listener listener;
    private boolean cleanCacheRunning = false;

    private int maxHomes;

    private HomeManager(int maxHomes) {
        homeManager = this;
        homeStorage = new FileAPI("homes.yml", VanillaTweaks.getInstance().getDataFolder().getPath());
        this.maxHomes = maxHomes;

        if (homeStorage.getConfig().contains("maxHomes")) {
            this.maxHomes = homeStorage.getConfig().getInt("maxHomes");
        }

        listener = new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent e) {
                UUID uuid = e.getPlayer().getUniqueId();
                if (!homesCache.containsKey(uuid)) {
                    loadPlayer(uuid);
                }
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent e) {
                UUID uuid = e.getPlayer().getUniqueId();

                if (homesCache.containsKey(uuid)) {
                    savePlayer(uuid);
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, VanillaTweaks.getInstance());
    }

    private void loadPlayer(UUID uuid) {
        if (!cleanCacheRunning) {
            cleanCacheAuto();
        }

        if (homesCache.containsKey(uuid)) {
            return;
        }

        ConfigurationSection playersSection = homeStorage.getConfig().getConfigurationSection(uuid.toString());

        if (playersSection == null) {
            return;
        }

        HashMap<String, Location> homes = new HashMap<>();

        for (String key : playersSection.getKeys(false)) {
            homes.put(key, playersSection.getLocation(key));
        }

        homesCache.put(uuid, new HomeUser(uuid, homes));
    }

    private void savePlayer(UUID uuid) {
        ConfigurationSection playersSection = null;

        if (!homesCache.containsKey(uuid)) {
            return;
        }

        homeStorage.getConfig().set(uuid.toString(), null);
        playersSection = homeStorage.getConfig().createSection(uuid.toString());

        for (Map.Entry<String, Location> home : homesCache.get(uuid).getHomes().entrySet()) {
            playersSection.set(home.getKey(), home.getValue());
        }

        homeStorage.save();
    }

    private void cleanCacheAuto() {
        cleanCacheRunning = true;
        Bukkit.getScheduler().runTaskTimerAsynchronously(VanillaTweaks.getInstance(), (task) -> {
            if (Bukkit.getOnlinePlayers().isEmpty()) {
                task.cancel();
                cleanCacheRunning = false;
            }

            for (UUID uuid : new ArrayList<>(homesCache.keySet())) {
                if (!Bukkit.getOfflinePlayer(uuid).isOnline()) {
                    homesCache.remove(uuid);
                    continue;
                }

                if (homesCache.get(uuid).getHomes().isEmpty()) {
                    homesCache.remove(uuid);
                    homeStorage.getConfig().set(uuid.toString(), null);
                }
            }


        }, 20 * 10 * 60, 20 * 10 * 60);
    }

    public static HomeManager getInstance() {
        if (homeManager == null) {
            homeManager = new HomeManager(5);
        }

        return homeManager;
    }

    public boolean contains(UUID uuid) {
        return homesCache.containsKey(uuid);
    }

    public boolean hasThisHome(UUID uuid, String home) {
        return homesCache.get(uuid).contains(home);
    }

    public void addHome(UUID uuid, String home, Location location) {
        homesCache.get(uuid).addHome(home, location);
    }

    public void removeHome(UUID uuid, String home) {
        homesCache.get(uuid).remove(home);
    }

    public Location get(UUID uuid, String home) {
        return homesCache.get(uuid).getHome(home).clone();
    }

    public HashMap<String, Location> get(UUID uuid) {
        return homesCache.get(uuid).getHomes();
    }

    public void addUser(UUID uuid) {
        if (homesCache.containsKey(uuid)) {
            return;
        }

        homesCache.put(uuid, new HomeUser(uuid, null));
    }

    public int amount(UUID uuid) {
        if (!homesCache.containsKey(uuid)) return 0;
        return homesCache.get(uuid).getHomes().size();
    }

    public int getMaxHomes() {
        return maxHomes;
    }

    public void setMaxHomes(int maxHomes) {
        this.maxHomes = maxHomes;
        this.homeStorage.getConfig().set("maxHomes", maxHomes);
        this.homeStorage.save();
    }
}
