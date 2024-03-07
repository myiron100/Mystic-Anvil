package org.mysticanvil.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.mysticanvil.MysticAnvil;

public class AnvilClickBedrock implements Listener {


    private final MysticAnvil plugin;

    public AnvilClickBedrock(MysticAnvil plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Vérifie si le clic est sur un bloc et que le bloc est une enclume
        if (event.hasBlock() && event.getClickedBlock().getType() == Material.ANVIL) {
            // Vérifie si le nom du joueur commence par un "."
            if (event.getPlayer().getName().startsWith(".")) {
                // Envoie un message au joueur
                event.getPlayer().sendMessage(ChatColor.GRAY + "⚠ Warning, using a anvil on bedrock could make your items unstable. To enchant something use the mystic machine at spawn instead");
            }
        }
    }
}