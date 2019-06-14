package com.zeshanasalm.wands.commands;


import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import com.zeshanasalm.wands.Main;
import com.zeshanasalm.wands.WandHelpers;
import com.zeshanasalm.wands.config.ConfigStore;
import com.zeshanasalm.wands.config.Wand;
import com.zeshanasalm.wands.utils.EnchantGlow;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;

public class WandCommands implements CommandExecutor {
    private final Main main;
    private final WandHelpers wandHelpers;

    public WandCommands(Main main) {
        this.main = main;
        this.wandHelpers = new WandHelpers(main);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(main.configStore.messages.get(ConfigStore.Messages.MAIN_USAGE_MESSAGE));
            return false;
        }

        String type = args[0];
        if (type.equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("Wands.reload")) {
                sender.sendMessage(main.configStore.messages.get(ConfigStore.Messages.NO_PERMISSION));
                return false;
            }

            main.reloadConfig();
            main.configStore = new ConfigStore(main);
            sender.sendMessage(main.configStore.messages.get(ConfigStore.Messages.RELOAD));
        }

        if (type.equalsIgnoreCase("list")) {
            if (!sender.hasPermission("Wands.list")) {
                sender.sendMessage(main.configStore.messages.get(ConfigStore.Messages.NO_PERMISSION));
                return false;
            }

            String list = main.configStore.messages.get(ConfigStore.Messages.WANDS_LIST).replace("%wands%", String.join(", ",  new ArrayList<>(main.configStore.wands.keySet())));
            sender.sendMessage(list);
        }

        if (type.equalsIgnoreCase("give")) {
            if (!sender.hasPermission("Wands.give")) {
                sender.sendMessage(main.configStore.messages.get(ConfigStore.Messages.NO_PERMISSION));
                return false;
            }

            if (args.length < 2) {
                sender.sendMessage(main.configStore.messages.get(ConfigStore.Messages.GIVE_USAGE_MESSAGE));
                return false;
            }

            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage(main.configStore.messages.get(ConfigStore.Messages.INVALID_PLAYER));
                return false;
            }

            String wandType = args[2];
            if (!main.configStore.wands.containsKey(wandType)) {
                sender.sendMessage(main.configStore.messages.get(ConfigStore.Messages.WAND_NOT_FOUND));
                return false;
            }

            Wand wand = main.configStore.wands.get(wandType);
            player.getInventory().addItem(wand.getWand((args[3] == null) ? wand.charges : Integer.parseInt(args[3])));
        }

        if (type.equalsIgnoreCase("helpers")) {
            if (!sender.hasPermission("Wands.helpers")) {
                sender.sendMessage(main.configStore.messages.get(ConfigStore.Messages.NO_PERMISSION));
                return false;
            }

            if (args[1].equals("msg")) {
                Player player = Bukkit.getPlayer(args[2]);

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    stringBuilder.append(args[i]).append(" ");
                }

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', stringBuilder.toString()));
            }

            if (args[1].equals("sell")) {
                Player player = Bukkit.getPlayer(args[2]);
                World world = Bukkit.getWorld(args[3]);
                int x = Integer.parseInt(args[4]);
                int y = Integer.parseInt(args[5]);
                int z = Integer.parseInt(args[6]);

                Block block = world.getBlockAt(x, y, z);
                BlockState blockState = block.getState();
                if (blockState instanceof Chest) {
                    Chest chest = (Chest) blockState;
                    InventoryHolder holder = chest.getInventory().getHolder();

                    BigDecimal worth = new BigDecimal(0);
                    int count = 0;
                    for (int i = 0; i < holder.getInventory().getSize(); i++) {
                        ItemStack itemStack = holder.getInventory().getItem(i);
                        if (itemStack == null)
                            continue;

                        if (wandHelpers.isWand(itemStack) != null)
                            continue;

                        BigDecimal itemWorth = main.essentials.getWorth().getPrice(main.essentials, itemStack);
                        worth = worth.add(itemWorth == null ? new BigDecimal(0) : itemWorth);
                        holder.getInventory().setItem(i, null);
                        count = count + itemStack.getAmount();
                    }

                    main.economy.depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), worth.doubleValue());
                    player.sendMessage(main.configStore.messages.get(ConfigStore.Messages.SOLD).replace("%item%", String.valueOf(count))
                            .replace("%amount%", String.valueOf(worth)));
                }
            }
        }

        return true;
    }
}
