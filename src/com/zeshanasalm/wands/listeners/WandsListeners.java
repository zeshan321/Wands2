package com.zeshanasalm.wands.listeners;

import com.wasteofplastic.askyblock.Island;
import com.zeshanasalm.wands.Main;
import com.zeshanasalm.wands.WandHelpers;
import com.zeshanasalm.wands.config.ConfigStore;
import com.zeshanasalm.wands.config.Wand;
import com.zeshanasalm.wands.config.WandData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WandsListeners implements Listener {

    private Main main;
    private WandHelpers wandHelpers;

    public WandsListeners(Main plugin) {
        this.main = plugin;
        this.wandHelpers = new WandHelpers(plugin);
    }


    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack hand = event.getItemInHand();

        if (hand != null && wandHelpers.isWand(hand) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWand(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack wandItemStack = player.getItemInHand();
        WandData wandData = wandHelpers.isWand(wandItemStack);

        if (wandData == null) {
            return;
        }

        if (wandData.chargeLeft == 0 && wandData.wand.charges != -1) {
            player.sendMessage(main.configStore.messages.get(ConfigStore.Messages.WAND_OUT));
            return;
        }

        Wand wand = wandData.wand;
        if (wand.islandCheck) {
            Island island = main.aSkyBlockAPI.getIslandAt(player.getLocation());
            if (island != null && (!island.getOwner().equals(player.getUniqueId()) && !island.getMembers().contains(player.getUniqueId()))) {
                player.sendMessage(main.configStore.messages.get(ConfigStore.Messages.ISLAND_OWNER));
                return;
            }
        }

        if (wand.actions.contains(event.getAction().name())) {
            event.setCancelled(true);

            if (wand.cooldown != -1) {
                long seconds = main.cooldownManager.isInCooldown(player, wand.type);
                if (seconds > 0) {
                    player.sendMessage(main.configStore.messages.get(ConfigStore.Messages.COOLDOWN).replace("%secs%", String.valueOf(seconds)));
                    return;
                }
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) {
                if (wandHelpers.usesBlocks(wandData)) {
                    if (wand.materials.contains(event.getClickedBlock().getType().name())) {
                        wandHelpers.executeCommands(player, wandData, event.getClickedBlock());
                    } else {
                        player.sendMessage(main.configStore.messages.get(ConfigStore.Messages.BLOCKS_CHECK));
                    }
                } else {
                    wandHelpers.executeCommands(player, wandData, null);
                }
            } else {
                wandHelpers.executeCommands(player, wandData, null);
            }

            main.cooldownManager.addCooldown(player, wand.type, wand.cooldown);

            if (wand.charges != -1) {
                wandHelpers.updateWand(player, wandData);
            }

            if ((wandData.chargeLeft - 1) == 0 && wandData.wand.charges != -1) {
                player.sendMessage(main.configStore.messages.get(ConfigStore.Messages.WAND_OUT));
                if (main.configStore.destroyWand) {
                    player.getInventory().setItemInHand(null);
                }
            }
        }
    }
}
