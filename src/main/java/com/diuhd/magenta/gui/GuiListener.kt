package com.diuhd.magenta.gui

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GuiListener : Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val inventoryHolder = event.inventory.holder

        if (inventoryHolder is Gui) {
            event.isCancelled = true
            val slot = event.rawSlot

            if (slot < 0 || slot >= event.inventory.size) return
            if (inventoryHolder.checkIfItemIsBorder(slot)) return

            val button = inventoryHolder.getButton(slot)
            button?.onClick(event)
        }
    }
}
