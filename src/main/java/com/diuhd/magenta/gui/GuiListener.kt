package com.diuhd.magenta.gui

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GuiListener : Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val inventoryHolder = event.inventory.holder as? Gui ?: return

        val slot = event.rawSlot
        if (!inventoryHolder.checkOpenSlot(slot)) {
            event.isCancelled = true
            return
        }
        if (slot < 0 || slot >= event.inventory.size) return

        inventoryHolder.getButton(slot)?.onClick(event)
    }
}
