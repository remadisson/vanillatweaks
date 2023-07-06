package de.remadisson.vanillatweaks.teleport.commands;

import de.remadisson.vanillatweaks.Messages;
import de.remadisson.vanillatweaks.teleport.TeleportRequest;
import de.remadisson.vanillatweaks.teleport.TeleporterManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TeleportDecline implements @Nullable CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        String permission = command.getPermission() == null ? "vt.tpa.admin" : command.getPermission();
        String syntax = command.getDescription();

        if (!player.hasPermission(permission)) {
            player.sendMessage(Component.text(Messages.getNoPermissionMessage()));
            return false;
        }
        if (args.length > 1) {
            player.sendMessage(Component.text(Messages.getPrefix() + syntax));
            return false;
        }

        if (args.length == 0) {
            ArrayList<TeleportRequest> playerRequests = TeleporterManager.get(player.getUniqueId());
            if (playerRequests.size() > 1) {
                player.sendMessage(Component.text(Messages.getPrefix() + syntax));
                return false;
            }
            if (playerRequests.isEmpty()) {
                player.sendMessage(Component.text(Messages.getPrefix() + "§cDu hast derzeit keine offenen Anfragen."));
                return false;
            }

            TeleportRequest teleportRequest = playerRequests.get(0);

            if (teleportRequest.isAccepted()) {
                player.sendMessage(Component.text(Messages.getPrefix() + "§cDu hast diese Anfrage bereits angenommen."));
                return false;
            }


            player.sendMessage(Component.text(Messages.getPrefix() + "§cDu hast die teleportations Anfrage von §4" + Bukkit.getOfflinePlayer(teleportRequest.getRequester()).getName() + "§c abgelehnt."));
            Player requester = Bukkit.getPlayer(teleportRequest.getRequester());
            if (requester != null && requester.isOnline()) {
                requester.sendMessage(Component.text(Messages.getPrefix() + "§cDeine Anfrage zu §4" + player.getName() + " §cwurde abgelehnt."));
            }
            TeleporterManager.remove(teleportRequest);
            return true;
        }

        Player requester = Bukkit.getPlayer(args[0]);

        if (requester == null) {
            player.sendMessage(Component.text(Messages.getPrefix() + "§cDieser Spieler konnte nicht gefunden werden."));
            return false;
        }

        if (!TeleporterManager.contains(requester.getUniqueId(), player.getUniqueId())) {
            player.sendMessage(Component.text(Messages.getPrefix() + "§cDu hast leider keine Anfrage von §4" + requester.getName()));
            return false;
        }

        @Nullable TeleportRequest teleportRequest = TeleporterManager.get(requester.getUniqueId(), player.getUniqueId(), null);

        if (teleportRequest == null) {
            player.sendMessage(Component.text(Messages.getPrefix() + "§cEs konnte keine Anfrage mit diesem Spieler gefunden werden."));
            return false;
        }

        if (teleportRequest.isAccepted()) {
            player.sendMessage(Component.text(Messages.getPrefix() + "§cDu hast die Anfrage von §4" + requester.getName() + " §cbereits aktzeptiert!"));
            return false;
        }

        player.sendMessage(Component.text(Messages.getPrefix() + "§cDu hast die teleportations Anfrage von §4" + Bukkit.getOfflinePlayer(teleportRequest.getRequester()).getName() + "§c abgelehnt."));
        if (requester.isOnline()) {
            requester.sendMessage(Component.text(Messages.getPrefix() + "§cDeine Anfrage zu §4" + player.getName() + " §cwurde abgelehnt."));
        }

        TeleporterManager.remove(teleportRequest);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return null;
        }

        ArrayList<String> result = new ArrayList<>();

        for (TeleportRequest tp : TeleporterManager.get(player.getUniqueId())) {
            if (!tp.isAccepted() && tp.getTarget().equals(player.getUniqueId())) {
                result.add(Bukkit.getOfflinePlayer(tp.getRequester()).getName());
            }
        }

        return result;
    }
}
