package org.mysticanvil.gui;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.mysticanvil.MysticAnvil;

import java.util.Map;

public class GListener implements Listener {
    private final MysticAnvil plugin;



    public GListener(MysticAnvil plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().contains("Mystic Anvil")) return;

        int slot = event.getRawSlot();
        ItemStack clickedItem = event.getCurrentItem();

        // Si l'item cliqué est du verre mauve, empêcher sa récupération.
        if (clickedItem != null && clickedItem.getType() == Material.PURPLE_STAINED_GLASS_PANE) {
            event.setCancelled(true);
            return;
        }

        // Si l'action se produit dans l'inventaire du GUI personnalisé (et non dans l'inventaire du joueur)
        if (event.getClickedInventory() != event.getWhoClicked().getInventory()) {
            // Autoriser le placement et le retrait d'items uniquement pour les slots 10 et 16
            if (!(slot == 10 || slot == 16)) {
                // Pour tous les autres slots dans le GUI, empêcher l'interaction
                event.setCancelled(true);
            }
        }
        // Les interactions dans l'inventaire du joueur restent non affectées et ne sont pas annulées
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().contains("Mystic Anvil")) return;
        Inventory inv = event.getInventory();
        ItemStack enchantedBook = inv.getItem(10);
        ItemStack equipment = inv.getItem(16);

        Player player = (Player) event.getPlayer();

        if (enchantedBook == null || equipment == null) {
            if (enchantedBook != null) player.getInventory().addItem(enchantedBook);
            if (equipment != null) player.getInventory().addItem(equipment);
            player.sendMessage(ChatColor.RED + "Please place your items correctly in the slots.");
            return;
        }

        boolean isBookInCorrectSlot = enchantedBook.getType() == Material.ENCHANTED_BOOK;
        boolean isEquipmentInCorrectSlot = equipment.getType() != Material.ENCHANTED_BOOK;

        if (!isBookInCorrectSlot || !isEquipmentInCorrectSlot) {
            player.getInventory().addItem(enchantedBook);
            player.getInventory().addItem(equipment);
            player.sendMessage(ChatColor.DARK_RED + "Bad placement of items. Please put the book in the first slot and the equipment in the second.");
            return;
        }

        if (equipment.getEnchantments().size() >= 20) {
            player.getInventory().addItem(enchantedBook);
            player.getInventory().addItem(equipment);
            player.sendMessage(ChatColor.RED + "This equipment already has the maximum number of enchantments. No additional enchantments can be added. (20 max)");
            return;
        }

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();
        if (meta != null && meta.hasStoredEnchants()) {
            boolean enchantmentsApplied = false;
            boolean incompatibleEnchantments = false;

            // Ici, vérifiez si le joueur a suffisamment d'argent avant d'appliquer les enchantements
            if (!MysticAnvil.getEconomy().has(player, 1000)) {
                player.sendMessage(ChatColor.RED + "You do not have enough money to complete this enchantment.");
                player.getInventory().addItem(enchantedBook);
                player.getInventory().addItem(equipment);
                return; // Arrêtez le processus si le joueur n'a pas assez d'argent
            }

            for (Map.Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();

                if (enchantment.canEnchantItem(equipment) && equipment.getEnchantments().size() < 20) {
                    equipment.addUnsafeEnchantment(enchantment, level);
                    enchantmentsApplied = true;
                } else {
                    incompatibleEnchantments = true;
                    break;
                }
            }

            if (enchantmentsApplied && !incompatibleEnchantments) {
                MysticAnvil.getEconomy().withdrawPlayer(player, 1000); // Déduire l'argent ici après avoir vérifié que les enchantements sont possibles
                player.sendMessage(ChatColor.GREEN + "1000$ have been deducted from your account for the enchantment.");
                player.getInventory().addItem(equipment); // Ajouter l'équipement enchanté à l'inventaire du joueur
                player.sendMessage(ChatColor.GREEN + "Compatible enchantments have been successfully applied!");
            } else {
                player.getInventory().addItem(enchantedBook);
                player.getInventory().addItem(equipment);
                if (incompatibleEnchantments) {
                    player.sendMessage(ChatColor.RED + "Some enchantments are not compatible with this item. The book and equipment have been returned to you.");
                } else {
                    player.sendMessage(ChatColor.RED + "Enchantment failed for an unknown reason.");
                }
            }
        } else {
            player.getInventory().addItem(enchantedBook);
            player.getInventory().addItem(equipment);
            player.sendMessage(ChatColor.RED + "The book does not contain any enchantments.");
        }

        inv.setItem(10, null); // Retirer le livre enchanté
        inv.setItem(16, null); // Retirer l'équipement
    }
}
