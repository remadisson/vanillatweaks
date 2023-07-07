package de.remadisson.vanillatweaks.home.commands;

import de.remadisson.vanillatweaks.Messages;
import de.remadisson.vanillatweaks.home.HomeManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SetHome implements CommandExecutor, TabExecutor {

    private final HomeManager homeManager;
    private final int maxHomes;

    public SetHome() {
        homeManager = HomeManager.getInstance();
        this.maxHomes = homeManager.getMaxHomes();
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
            homeManager.addUser(uuid);
        }

        if (homeManager.hasThisHome(uuid, home)) {
            player.sendMessage(Component.text(Messages.getPrefix() + "§cDas Home §4" + home + " §cist bereits gesetzt worden. Du musst es zuerst löschen um es neu setzen zu können!"));
            return false;
        }

        if (homeManager.get(uuid).size() >= maxHomes) {
            player.sendMessage(Component.text(Messages.getPrefix() + "§cDu hast bereits die maximale Anzahl an Homes erreicht §7(§4" + maxHomes + "§7)."));
            return false;
        }

        homeManager.addHome(uuid, home, player.getLocation());
        player.sendMessage(Component.text(Messages.getPrefix() + "§aDu hast das Zuhause §2\"" + home + "\" §aerfolgreich gesetzt!"));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
