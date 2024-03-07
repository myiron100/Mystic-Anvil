package org.mysticanvil;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mysticanvil.command.MysticAnvilCommand;
import org.mysticanvil.gui.GListener;
import org.mysticanvil.listener.AnvilClickBedrock;

import java.util.Objects;

public final class MysticAnvil extends JavaPlugin {

    private static Economy econ = null;

    @Override
    public void onEnable() {
        // Enregistrement de la commande et du gestionnaire d'événements
        Objects.requireNonNull(this.getCommand("mysticanvil")).setExecutor(new MysticAnvilCommand(this));
        getServer().getPluginManager().registerEvents(new GListener(this), this);
        getServer().getPluginManager().registerEvents(new AnvilClickBedrock(this), (this));
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
