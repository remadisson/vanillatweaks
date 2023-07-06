package de.remadisson.vanillatweaks.teleport;

import de.remadisson.vanillatweaks.Messages;
import de.remadisson.vanillatweaks.VanillaTweaks;
import de.remadisson.vanillatweaks.teleport.enums.TeleportType;
import de.remadisson.vanillatweaks.teleport.enums.TeleportingPlayerType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.UUID;

public class TeleporterManager {

    private final static ArrayList<TeleportRequest> requests = new ArrayList<>();

    private TeleporterManager() {
    }

    public static boolean contains(UUID requester, UUID target) {
        return contains(requester, target, null);
    }

    public static boolean contains(UUID requester, UUID target, TeleportType type) {
        return requests.stream().anyMatch(request -> request.getRequester().equals(requester) && request.getTarget().equals(target) && (type == null || request.getTeleportEnum() == type));
    }

    public static boolean contains(UUID uuid) {
        return containsWithType(uuid, null);
    }

    public static boolean containsWithType(UUID uuid, TeleportingPlayerType type) {
        if (type == null) {
            return requests.stream().anyMatch(request -> request.getRequester().equals(uuid) || request.getTarget().equals(uuid));
        }

        switch (type) {
            case REQUESTER -> {
                return requests.stream().anyMatch(request -> request.getRequester().equals(uuid));
            }

            case TARGET -> {
                return requests.stream().anyMatch(request -> request.getTarget().equals(uuid));
            }
        }

        return false;
    }

    public static ArrayList<TeleportRequest> get(UUID uuid) {
        return get(uuid, null);
    }

    public static ArrayList<TeleportRequest> get(UUID uuid, TeleportingPlayerType type) {
        ArrayList<TeleportRequest> requestsContainingThePlayer = new ArrayList<>();

        for (TeleportRequest request : requests) {
            if ((type == TeleportingPlayerType.TARGET || type == null) && request.getTarget().equals(uuid)) {
                requestsContainingThePlayer.add(request);
            }

            if ((type == TeleportingPlayerType.REQUESTER || type == null) && request.getRequester().equals(uuid)) {
                requestsContainingThePlayer.add(request);
            }
        }

        return requestsContainingThePlayer;
    }

    public static TeleportRequest get(UUID requester, UUID target, TeleportType type) {
        return requests.stream().filter(request -> request.getRequester().equals(requester) && request.getTarget().equals(target) && (type == null || request.getTeleportEnum() == type)).findFirst().orElse(null);
    }

    public static boolean prepare(TeleportRequest teleportRequest) {
        if (!teleportRequest.isAccepted()) {
            return false;
        }

        if (teleportRequest.getListener() != null) {
            return false;
        }

        Player requester = Bukkit.getPlayer(teleportRequest.getRequester());
        Player target = Bukkit.getPlayer(teleportRequest.getTarget());

        if (requester == null || !requester.isOnline() || target == null || !target.isOnline()) {
            remove(teleportRequest);
            return false;
        }

        String requesterMessage = "";
        String targetMessage = "";

        if (teleportRequest.getTeleportEnum() == TeleportType.HERE) {
            targetMessage = Messages.getPrefix() + "§eTeleportier Vorgang wird gestartet, bitte bewege dich 5 Sekunden nicht.";
            teleportRequest.setListener(new Listener() {
                @EventHandler
                public void onMove(PlayerMoveEvent e) {
                    if (!e.getPlayer().getUniqueId().equals(target.getUniqueId())) {
                        return;
                    }

                    if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
                        target.sendMessage(Component.text(Messages.getPrefix() + "§cDu hast dich bewegt! §8- §4Die Anfrage wurde abgebrochen."));
                        requester.sendMessage(Component.text(Messages.getPrefix() + "§4" + target.getName() + "§c hat sich bewegt! §8 - §4Deine Anfrage wurde abgebrochen."));
                        if (teleportRequest.getTaskID() != -1) {
                            Bukkit.getScheduler().cancelTask(teleportRequest.getTaskID());
                        }
                        HandlerList.unregisterAll(this);
                        requests.remove(teleportRequest);
                    }
                }
            }, VanillaTweaks.getInstance());

            teleportRequest.setTaskID(Bukkit.getScheduler().runTaskLaterAsynchronously(VanillaTweaks.getInstance(), () -> {
                HandlerList.unregisterAll(teleportRequest.getListener());
                execute(teleportRequest);
            }, 5 * 20).getTaskId());

        } else if (teleportRequest.getTeleportEnum() == TeleportType.THERE) {
            requesterMessage = Messages.getPrefix() + "§eTeleportier Vorgang wird gestartet, bitte bewege dich 5 Sekunden nicht.";
            teleportRequest.setListener(new Listener() {
                @EventHandler
                public void onMove(PlayerMoveEvent e) {
                    if (!e.getPlayer().getUniqueId().equals(requester.getUniqueId())) {
                        return;
                    }

                    if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
                        requester.sendMessage(Component.text(Messages.getPrefix() + "§cDu hast dich bewegt! §8- §4Deine Anfrage wurde abgebrochen."));
                        target.sendMessage(Component.text(Messages.getPrefix() + "§4" + target.getName() + "§c hat sich bewegt! §8 - §4Die Anfrage wurde abgebrochen."));
                        if (teleportRequest.getTaskID() != -1) {
                            Bukkit.getScheduler().cancelTask(teleportRequest.getTaskID());
                        }
                        HandlerList.unregisterAll(this);
                        requests.remove(teleportRequest);
                    }
                }
            }, VanillaTweaks.getInstance());

            teleportRequest.setTaskID(Bukkit.getScheduler().runTaskLaterAsynchronously(VanillaTweaks.getInstance(), () -> {
                HandlerList.unregisterAll(teleportRequest.getListener());
                Bukkit.getScheduler().runTask(VanillaTweaks.getInstance(), () -> {
                    execute(teleportRequest);
                });
            }, 5 * 20).getTaskId());
        }

        if (!requesterMessage.isBlank()) {
            requester.sendMessage(Component.text(requesterMessage));
        }
        if (!targetMessage.isBlank()) {
            target.sendMessage(Component.text(targetMessage));
        }

        return true;
    }

    private static boolean execute(TeleportRequest teleportRequest) {
        Player requester = Bukkit.getPlayer(teleportRequest.getRequester());
        Player target = Bukkit.getPlayer(teleportRequest.getTarget());

        if (requester == null || !requester.isOnline() || target == null || !target.isOnline()) {
            return false;
        }

        String requesterMessage = "";
        String targetMessage = "";

        if (teleportRequest.getTeleportEnum() == TeleportType.HERE) {
            targetMessage = Messages.getPrefix() + "§eDu wirst nun zu §6" + requester.getName() + " §eteleportiert.";
            requesterMessage = Messages.getPrefix() + "§6" + target.getName() + " §ewird zu dir teleportiert.";
        } else if (teleportRequest.getTeleportEnum() == TeleportType.THERE) {
            requesterMessage = Messages.getPrefix() + "§eDu wirst nun zu §6" + target.getName() + " §eteleportiert.";
            targetMessage = Messages.getPrefix() + "§6" + requester.getName() + " §ewird zu dir teleportiert.";
        }

        requester.sendMessage(Component.text(requesterMessage));
        target.sendMessage(Component.text(targetMessage));

        if (teleportRequest.getTeleportEnum() == TeleportType.THERE) {
            requester.teleport(target);
            Bukkit.getLogger().info("VanillaTweaks - " + requester.getName() + " was teleported to " + target.getName());
        } else {
            target.teleport(requester);
            Bukkit.getLogger().info("VanillaTweaks - " + target.getName() + " was teleported to " + requester.getName());
        }

        requests.remove(teleportRequest);
        return true;
    }

    public static void put(UUID requester, UUID target, TeleportType type) {
        requests.add(new TeleportRequest(requester, target, type));
    }

    public static boolean remove(TeleportRequest teleportRequest) {
        return requests.remove(teleportRequest);
    }

}
