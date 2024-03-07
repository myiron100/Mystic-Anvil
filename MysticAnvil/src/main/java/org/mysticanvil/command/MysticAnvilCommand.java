package org.mysticanvil.command;



import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.mysticanvil.MysticAnvil;

public class MysticAnvilCommand implements CommandExecutor {
    private final MysticAnvil plugin;

    public MysticAnvilCommand(MysticAnvil plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;
        Inventory gui = Bukkit.createInventory(player, 27, ChatColor.DARK_PURPLE + "Mystic Anvil");

        // Remplissage du GUI avec du verre mauve pour les emplacements non utilisés
        ItemStack purpleGlass = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        for (int i = 0; i < gui.getSize(); i++) {
            if (i != 10 && i != 16) { // Les emplacements pour le livre et l'équipement
                gui.setItem(i, purpleGlass);
            }
        }

        player.openInventory(gui);
        return true;
    }
}

