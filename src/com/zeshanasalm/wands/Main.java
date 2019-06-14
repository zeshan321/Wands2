package com.zeshanasalm.wands;

import com.earth2me.essentials.Essentials;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.zeshanasalm.wands.commands.WandCommands;
import com.zeshanasalm.wands.config.ConfigStore;
import com.zeshanasalm.wands.cooldowns.CooldownManager;
import com.zeshanasalm.wands.listeners.WandsListeners;
import com.zeshanasalm.wands.utils.EnchantGlow;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public CooldownManager cooldownManager;
    public ConfigStore configStore;
    public Essentials essentials;
    public Economy economy;
    public ASkyBlockAPI aSkyBlockAPI;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Commands
        getCommand("Wands").setExecutor(new WandCommands(this));

        // Listener
        getServer().getPluginManager().registerEvents(new WandsListeners(this), this);

        // Config
        configStore = new ConfigStore(this);

        // Cooldowns
        cooldownManager = new CooldownManager(this);

        // Essentials
        essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");

        // Hook into vault
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        }

        // ASkyBlock
        if (ASkyBlockAPI.getInstance() != null)
            aSkyBlockAPI = ASkyBlockAPI.getInstance();

        // Glow
        EnchantGlow.getGlow();
    }

    @Override
    public void onDisable() {

    }
}
