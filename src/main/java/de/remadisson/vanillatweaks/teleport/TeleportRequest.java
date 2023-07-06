package de.remadisson.vanillatweaks.teleport;

import de.remadisson.vanillatweaks.teleport.enums.TeleportType;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class TeleportRequest {

    private final UUID requester;
    private final UUID target;
    private boolean targetAccepted = false;
    private final TeleportType teleportEnum;

    private Listener listener;
    private int taskID = -1;

    public TeleportRequest(UUID requester, UUID target, TeleportType teleportEnum) {
        this.requester = requester;
        this.target = target;
        this.teleportEnum = teleportEnum;
    }

    public UUID getTarget() {
        return target;
    }

    public UUID getRequester() {
        return requester;
    }

    public TeleportType getTeleportEnum() {
        return teleportEnum;
    }

    public boolean isAccepted() {
        return targetAccepted;
    }

    public Listener getListener() {
        return listener;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTargetAccept(boolean targetAccepted) {
        this.targetAccepted = targetAccepted;
    }

    public void setListener(Listener listener, Plugin plugin) {
        this.listener = listener;
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }
}
