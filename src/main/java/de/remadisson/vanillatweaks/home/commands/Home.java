package de.remadisson.vanillatweaks.home.commands;

import de.remadisson.vanillatweaks.Messages;
import de.remadisson.vanillatweaks.home.HomeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Location;
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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Home implements CommandExecutor, TabExecutor {

    private final HomeManager homeManager;

    public Home() {
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

        if (args.length > 2) {
            player.sendMessage(Component.text(Messages.getPrefix() + usage));
            return false;
        }

        if (!homeManager.contains(uuid)) {
            player.sendMessage(Component.text(Messages.getPrefix() + "§cDu hast derzeit kein Zuhause!"));
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text(Messages.getPrefix() + "§aHier erhälst du eine Liste deiner gesetzen homes:"));
            TextComponent list = Component.text(Messages.getPrefix());
            for (Map.Entry<String, Location> home : homeManager.get(uuid).entrySet()) {
                if (!list.equals(Component.text(Messages.getPrefix()))) {
                    list = list.append(Component.text("§7, "));
                }
                list = list.append(Component.text("§e" + StringUtils.capitalise(home.getKey())).hoverEvent(HoverEvent.showText(Component.text("§bKlick zum Teleportieren"))).clickEvent(ClickEvent.runCommand("/home " + home.getKey())));
            }
            player.sendMessage(list);
            return true;
        }

        if (args.length == 1) {
            String home = args[0];

            if (!homeManager.hasThisHome(uuid, home)) {
                player.sendMessage(Component.text(Messages.getPrefix() + "§cDu hast kein Zuhause mit dem Namen \"§4" + home.toLowerCase() + "\""));
                return false;
            }

            Location loc = homeManager.get(uuid, home);
            player.sendMessage(Component.text(Messages.getPrefix() + "§aDu wirst zu §2" + home.toLowerCase() + " §ateleportiert."));
            player.teleport(loc);
            return false;
        }

        if (!player.hasPermission("vt.admin")) {
            player.sendMessage(Component.text(Messages.getPrefix() + usage));
            return false;
        }

        String subcommand = args[0];
        String data = args[1];

        if (!subcommand.equalsIgnoreCase("maxHomes")) {
            player.sendMessage(Component.text(Messages.getPrefix() + "§cAktuell das einzige Sonderargument ist: §4maxHomes"));
            return false;
        }
        int newMaxAmount;
        try {
            newMaxAmount = Integer.parseInt(data);
        } catch (NumberFormatException ex) {
            player.sendMessage(Component.text(Messages.getPrefix() + "§cDein zweites Argument sollte in diesem Fall besser eine Zahl sein."));
            return false;
        }

        homeManager.setMaxHomes(newMaxAmount);
        player.sendMessage(Component.text(Messages.getPrefix() + "§dSettings §7> §bDu hast maxHomes erfolgreich auf §9" + newMaxAmount + "§b gesetzt!"));
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
