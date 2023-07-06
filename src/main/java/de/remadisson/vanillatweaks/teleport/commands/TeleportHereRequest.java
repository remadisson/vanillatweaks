package de.remadisson.vanillatweaks.teleport.commands;

import de.remadisson.vanillatweaks.Messages;
import de.remadisson.vanillatweaks.teleport.TeleporterManager;
import de.remadisson.vanillatweaks.teleport.enums.TeleportType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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

public class TeleportHereRequest implements @Nullable CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        String permission = command.getPermission() == null ? "vt.tpa.admin" : command.getPermission();
        String syntax = command.getDescription();

        TeleportType type = TeleportType.HERE;

        if (!player.hasPermission(permission)) {
            player.sendMessage(Component.text(Messages.getNoPermissionMessage()));
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(Component.text(Messages.getPrefix() + syntax));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            player.sendMessage(Component.text(Messages.getPrefix() + "§4" + args[0] + " §cist nicht online."));
            return false;
        }

        if (target == player) {
            player.sendMessage(Component.text(Messages.getPrefix() + "§cDu kannst dir selbst keine Anfrage senden."));
            return false;
        }

        if (TeleporterManager.contains(player.getUniqueId(), target.getUniqueId(), null)) {
            player.sendMessage(Component.text(Messages.getPrefix() + "§cDu kannst eine Anfrage nur einmal senden."));
            return false;
        }

        TextComponent targetComponent = Component.text(Messages.getPrefix() + "§2" + player.getName() + "§a will dich zu sich teleportieren. ");
        TextComponent targetAccept = Component.text("§8[§a✔§8]").clickEvent(ClickEvent.runCommand("/tpaaccept " + player.getName()));
        TextComponent targetDecline = Component.text("§8[§4❌§8]").clickEvent(ClickEvent.runCommand("/tpadecline " + player.getName()));

        targetComponent = targetComponent.append(targetAccept).append(Component.text(" oder ", NamedTextColor.YELLOW)).append(targetDecline);

        target.sendMessage(targetComponent);
        player.sendMessage(Component.text(Messages.getPrefix() + "§2Du §ahast §aeine \"Hier\"-Teleportations-Anfrage an §2" + target.getName() + " §agesendet."));

        TeleporterManager.put(player.getUniqueId(), target.getUniqueId(), type);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return null;
        }

        ArrayList<String> result = new ArrayList<>();

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!TeleporterManager.contains(player.getUniqueId(), online.getUniqueId(), TeleportType.HERE)) {
                result.add(online.getName());
            }
        }

        return result;
    }
}
