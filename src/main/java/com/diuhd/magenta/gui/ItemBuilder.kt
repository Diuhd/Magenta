package com.diuhd.magenta.gui

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class ItemBuilder(mat: Material) {
    val stack = ItemStack(mat)
    val itemMeta = stack.itemMeta!!
    fun setName(name: String): ItemBuilder {
        itemMeta.setDisplayName(name)
        return this
    }
    fun setAmount(amount: Int): ItemBuilder {
        stack.amount = amount
        return this
    }
    fun build(): ItemStack {
        stack.itemMeta = itemMeta
        return stack
    }
    fun setEnchant(ench: Enchantment, level: Int): ItemBuilder {
        itemMeta.addEnchant(ench, level, true)
        return this
    }
    fun setLore(vararg lore: String): ItemBuilder {
        itemMeta.lore = lore.toMutableList()
        return this
    }
    fun addLore(lore: String): ItemBuilder {
        val existentLore = itemMeta.lore
        if (existentLore != null) {
            itemMeta.lore = existentLore.toMutableList() + lore
        }
        return this
    }
    fun setTag(key: String): ItemBuilder {
        val namespacedKey = NamespacedKey(JavaPlugin.getProvidingPlugin(ItemBuilder::class.java), key)
        itemMeta.persistentDataContainer.set(namespacedKey, PersistentDataType.BYTE, 0.toByte())
        return this
    }
}