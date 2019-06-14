package com.zeshanasalm.wands.config;

import com.zeshanasalm.wands.utils.EnchantGlow;
import com.zeshanasalm.wands.utils.HiddenString;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Wand {

    public String type;
    public int charges;
    public List<String> commands;
    public String display;
    public List<String> lore;
    public Material material;
    public int cooldown;
    public boolean glow;
    public List<String> actions;
    public List<String> materials;
    public boolean islandCheck;

    public Wand(String type, int charges, List<String> commands, String display, List<String> lore, Material material, int cooldown, boolean glow, List<String> actions, List<String> materials, boolean islandCheck) {
        this.type = type;
        this.charges = charges;
        this.commands = commands;
        this.display = display;
        this.lore = lore;
        this.material = material;
        this.cooldown = cooldown;
        this.glow = glow;
        this.actions = actions;
        this.materials = materials;
        this.islandCheck = islandCheck;
    }

    public ItemStack getWand(int chargeLeft) {
        ItemStack itemStack = new ItemStack(material);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(display);

        itemMeta.setLore(lore);
        List<String> newLore = new ArrayList<>();
        for (String oldLore: itemMeta.getLore()) {
            oldLore = oldLore.replace("%charge%", chargeLeft == -1 ? "Unlimited" : String.valueOf(chargeLeft)).replace("%cooldown%", String.valueOf(cooldown));
            newLore.add(oldLore);
        }

        newLore.add(HiddenString.encodeString("WANDITEM|" + chargeLeft + "|" + type));
        itemMeta.setLore(newLore);

        if (glow) {
            EnchantGlow glow = new EnchantGlow(255);
            itemMeta.addEnchant(glow, 1, true);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
