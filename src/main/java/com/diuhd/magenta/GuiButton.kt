package com.diuhd.magenta

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class GuiButton(private val stack: ItemStack, private val func: (InventoryClickEvent) -> Unit): Listener {
    @EventHandler
    fun onClick(e: InventoryClickEvent) {
        func(e)
    }
    fun getItemStack(): ItemStack {
        return stack
    }
    fun getClickEvent(): (InventoryClickEvent) -> Unit {
        return func
    }
}