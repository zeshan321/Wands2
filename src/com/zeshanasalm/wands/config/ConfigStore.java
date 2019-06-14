package com.zeshanasalm.wands.config;

import com.zeshanasalm.wands.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigStore {

    public HashMap<String, Wand> wands;
    public HashMap<Messages, String> messages;
    public boolean destroyWand;

    public ConfigStore(Main main) {
        destroyWand = main.getConfig().getBoolean("DestroyWand");

        messages = new HashMap<>();
        for (String key: main.getConfig().getConfigurationSection("Messages").getKeys(false)) {
            messages.put(Messages.valueOf(key), ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("Messages." + key)));
        }

        wands = new HashMap<>();
        for (String values: main.getConfig().getStringList("Wands")) {
            String display = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString(values + ".DisplayName"));

            List<String> lore = new ArrayList<>();
            for (String line:  main.getConfig().getStringList(values + ".Lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }

            Material material = Material.valueOf(main.getConfig().getString(values + ".Material"));
            int charges = main.getConfig().getInt(values + ".Charges");
            List<String> commands = main.getConfig().getStringList(values + ".AttachedCommands");
            int cooldown = main.getConfig().getInt(values + ".Cooldown");
            boolean glow = main.getConfig().getBoolean(values + ".Glow");
            List<String> actions = main.getConfig().getStringList(values + ".Actions");

            List<String> materials = new ArrayList<>();
            if (main.getConfig().contains(values + ".BlockCheck")) {
                materials = main.getConfig().getStringList(values + ".BlockCheck");
            }

            boolean islandCheck = main.getConfig().getBoolean(values + ".IslandCheck");
            Wand wand = new Wand(values, charges, commands, display, lore, material, cooldown, glow, actions, materials, islandCheck);
            wands.put(values, wand);
        }
    }

    public boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

    public enum Messages {
        NO_PERMISSION,
        MAIN_USAGE_MESSAGE,
        RELOAD,
        GIVE_USAGE_MESSAGE,
        INVALID_PLAYER,
        GIVEN_MESSAGE,
        WANDS_LIST,
        WAND_NOT_FOUND,
        COOLDOWN,
        WAND_OUT,
        BLOCKS_CHECK,
        SOLD,
        ISLAND_OWNER
    }
}
