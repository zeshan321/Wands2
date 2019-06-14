package com.zeshanasalm.wands;

import com.zeshanasalm.wands.config.WandData;
import com.zeshanasalm.wands.utils.HiddenString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WandHelpers {

    public Main main;

    public WandHelpers(Main main) {
        this.main = main;
    }

    public WandData isWand(ItemStack itemStack) {
        if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
            String last = itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size() - 1);

            if (HiddenString.hasHiddenString(last)) {
                last = HiddenString.extractHiddenString(last);

                if (last.contains("|")) {
                    String[] data = last.split("\\|");

                    if (data[0].equals("WANDITEM")) {
                        int chargeLeft = Integer.parseInt(data[1]);
                        String type = data[2];

                        if (!main.configStore.wands.containsKey(type)) {
                            return null;
                        }

                        return new WandData(chargeLeft, main.configStore.wands.get(type));
                    }
                }
            }
        }

        return null;
    }

    public boolean usesBlocks(WandData wandData) {
        for (String command: wandData.wand.commands) {
            if (command.contains("%block%")) {
                return true;
            }
        }

        return false;
    }

    public void updateWand(Player player, WandData wandData) {
        ItemStack wand = wandData.wand.getWand(wandData.chargeLeft - 1);

        player.getInventory().setItemInHand(wand);
    }

    public void executeCommands(Player player, WandData wandData, Block block) {
        for (String command: wandData.wand.commands) {
            command = command.replace("%player%", player.getName());
            if (!command.contains("%block%")) {
                main.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                continue;
            }

            Location location = block.getLocation();
            command = command.replace("%block%", location.getWorld().getName() + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
            player.sendMessage(command);
            main.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}
