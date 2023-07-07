package de.remadisson.vanillatweaks.listener;

import de.remadisson.vanillatweaks.VanillaTweaks;
import de.remadisson.vanillatweaks.home.HomeManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HeaderAndFooterList implements Listener {

    private final HomeManager homeManager;
    private boolean schedulerRunning = false;

    public HeaderAndFooterList() {
        homeManager = HomeManager.getInstance();
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e) {
        e.joinMessage(Component.text("§8[§a+§8] " + e.getPlayer().getName()));
        e.getPlayer().sendPlayerListHeaderAndFooter(Component.text("\n§b1§7.§b20§7.§b1 §7\"§bVanilla§7\"\n"), Component.text("\n §7Hosted by §bremadisson \n"));

        scheduleUpdateHeaderAndFooter();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.quitMessage(Component.text("§8[§c-§8] " + e.getPlayer().getName()));
    }


    public void updateFooterAndHeader(Player player) {
        Component header = Component.text("\n§7Hallo §b" + player.getName() + "\n\n§b1§7.§b20§7.§b1 §7\"§bVanilla§7\"\n");
        Component footer = Component.text("\n §b" + homeManager.amount(player.getUniqueId()) + "§8/§9" + homeManager.getMaxHomes() + " §bHomes \n\n §7Hosted by §bremadisson\n");
        player.sendPlayerListHeaderAndFooter(header, footer);
    }

    public void scheduleUpdateHeaderAndFooter() {
        if (schedulerRunning) {
            return;
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(VanillaTweaks.getInstance(), (task) -> {
            schedulerRunning = true;

            if (Bukkit.getOnlinePlayers().size() == 0) {
                schedulerRunning = false;
                task.cancel();
            }

            for (Player online : Bukkit.getOnlinePlayers()) {
                Bukkit.getScheduler().runTask(VanillaTweaks.getInstance(), () -> {
                    updateFooterAndHeader(online);
                });
            }
        }, 0, 20 * 2);
    }

}
