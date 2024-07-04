package com.diuhd.magenta.gui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class GuiListener : Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val player: Player = event.whoClicked as Player
        val slot: Int = event.slot
        if (player.hasMetadata("OpenGui")) {
            val menu: Gui = player.getMetadata("OpenGui")[0].value() as Gui
            if (slot >= 0 && slot < menu.buttons.size) {
                if (menu.buttons[slot] != null) {
                    val button: GuiButton = menu.buttons[slot]!!
                    if (event.currentItem == null && event.currentItem?.type == Material.AIR) throw IllegalArgumentException("The button should NOT be null.")
                    event.isCancelled = true
                    button.onClick(event)
                } else if (menu.borderItems[slot]) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val player: Player = event.player
        if (player.hasMetadata("OpenGui")) {
            player.removeMetadata("OpenGui", JavaPlugin.getProvidingPlugin(GuiListener::class.java))
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        val player: Player = event.player as Player
        if (player.hasMetadata("OpenGui")) {
            player.removeMetadata("OpenGui", JavaPlugin.getProvidingPlugin(GuiListener::class.java))
        }
    }
}
