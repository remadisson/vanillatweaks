package de.remadisson.vanillatweaks.home.commands;

import de.remadisson.vanillatweaks.Messages;
import de.remadisson.vanillatweaks.home.HomeManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DelHome implements CommandExecutor, TabExecutor {
    private final HomeManager homeManager;

    public DelHome() {
        homeManager = HomeManager.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        UUID uuid = player.getUniqueId();
        String permission = command.getPermission() == null ? "vt.tpa.admin" : command.getPermission();
        String usage = command.getDescription();

        if (!player.hasPermission(permission)) {
            player.sendMessage(Component.text(Messages.getPrefix() + Messages.getNoPermissionMessage()));
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(Component.text(Messages.getPrefix() + usage));
            return false;
        }

        String home = args[0];

        if (!homeManager.contains(uuid)) {
            player.sendMessage(Component.text(Messages.getPrefix() + "§cDu hast derzeit kein Zuhause!"));
            return false;
        }

        if (!homeManager.hasThisHome(uuid, home)) {
            player.sendMessage(Component.text(Messages.getPrefix() + "§cDu hast kein Zuhause mit dem Namen \"§4" + home + "\""));
            return false;
        }

        homeManager.removeHome(uuid, home);
        player.sendMessage(Component.text(Messages.getPrefix() + "§aDu hast das Zuhause §2\"" + home + "\" §a erfolgreich gelöscht!"));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return null;
        }

        if (args.length == 1) {
            UUID uuid = player.getUniqueId();
            return new ArrayList<>(homeManager.get(uuid).keySet().stream().map(StringUtils::capitalise).collect(Collectors.toList()));
        }

        return null;
    }
}
