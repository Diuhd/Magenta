package com.diuhd.magenta.gui

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class GuiListener : Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val inventoryHolder = event.inventory.holder as? Gui ?: return
        val slot = event.rawSlot
        if (slot < 0 || slot >= event.inventory.size) return
        if (inventoryHolder.checkIfOpen(slot)) return

        event.isCancelled = true
        inventoryHolder.getButton(slot)?.onClick(event)
    }
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventoryHolder = event.inventory.holder as? Gui ?: return
        inventoryHolder.onClose()
    }
}
