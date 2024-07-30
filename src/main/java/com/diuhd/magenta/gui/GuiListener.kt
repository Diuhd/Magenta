package com.diuhd.magenta.gui

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.java.JavaPlugin

class GuiListener : Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val inventory = event.inventory.holder
        if (inventory is Gui) {
            event.isCancelled = true
            inventory.handleInventoryClick(event)
        } else {
            println("Non GUI click event passed")
        }
    }
}
