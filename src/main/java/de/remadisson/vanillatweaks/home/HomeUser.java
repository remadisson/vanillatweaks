package de.remadisson.vanillatweaks.home;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeUser {

    private final UUID uuid;
    private final HashMap<String, Location> homes = new HashMap<>();

    public HomeUser(UUID uuid, HashMap<String, Location> homes) {
        this.uuid = uuid;
        if (homes != null && !homes.isEmpty()) {
            this.homes.putAll(homes);
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public HashMap<String, Location> getHomes() {
        HashMap<String, Location> homesTemp = new HashMap<>();

        for (Map.Entry<String, Location> home : homes.entrySet()) {
            homesTemp.put(home.getKey(), home.getValue().clone());
        }

        return homesTemp;
    }

    public void addHome(String name, Location location) {
        homes.put(name.toLowerCase(), location);
    }

    public Location getHome(String name) {
        return homes.get(name.toLowerCase());
    }

    public boolean contains(String name) {
        return homes.containsKey(name.toLowerCase());
    }

    public void remove(String name) {
        homes.remove(name.toLowerCase());
    }
}