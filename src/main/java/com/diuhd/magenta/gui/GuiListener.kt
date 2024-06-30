package com.diuhd.magenta.gui

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class GuiListener: Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val player: Player = event.whoClicked as Player
        val slot: Int = event.slot
        if (player.hasMetadata("OpenGui")) {
            val menu: Gui = player.getMetadata("OpenGui")[0].value() as Gui
            if (menu.buttons[slot] == null) return
            val button: GuiButton = menu.buttons[slot]!!
            event.isCancelled = true
            button.onClick(event)
        }
    }
    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val player: Player = event.player
        if (player.hasMetadata("OpenGui")) {
            player.removeMetadata("OpenGui", JavaPlugin.getProvidingPlugin(JavaPlugin::class.java))
        }
    }
    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        val player: Player = event.player as Player
        if (player.hasMetadata("OpenGui")) {
            player.removeMetadata("OpenGui", JavaPlugin.getProvidingPlugin(JavaPlugin::class.java))
        }
    }
}