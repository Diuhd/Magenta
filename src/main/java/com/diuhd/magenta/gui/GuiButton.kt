package com.diuhd.magenta.gui

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class GuiButton(private val stack: ItemStack, private val func: (InventoryClickEvent) -> Unit) {
    fun onClick(event: InventoryClickEvent) {
        func(event)
    }

    fun getItemStack(): ItemStack {
        return stack
    }
}
