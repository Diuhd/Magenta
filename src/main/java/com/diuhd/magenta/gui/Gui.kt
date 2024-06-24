package com.diuhd.magenta.gui

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class Gui(private val rows: Int, title: String, plugin: JavaPlugin) : Listener {
    private val inventory: Inventory
    private val buttons = mutableMapOf<Int, GuiButton>()

    init {
        require(rows in 1..6) { "Rows must be between 1 and 6!" }
        inventory = Bukkit.createInventory(null, rows * 9, title)
        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getProvidingPlugin(Gui::class.java))
    }

    fun open(player: Player) {
        player.openInventory(inventory)
    }

    fun setBorder(thickness: Int) {
        require(thickness * 2 < rows) { "Border is too thick for the number of rows!" }
        require(thickness * 2 < 9) { "Border is too thick for the number of columns!" }

        val schem = Schematic()
        for (i in 0 until thickness) {
            schem.map("1".repeat(9))
        }
        for (i in 0 until rows - thickness * 2) {
            schem.map("1".repeat(thickness) + "0".repeat(9 - thickness * 2) + "1".repeat(thickness))
        }
        for (i in 0 until thickness) {
            schem.map("1".repeat(9))
        }

        schem.apply(this)
    }

    fun setItem(slot: Int, item: ItemStack) {
        inventory.setItem(slot, item)
    }

    fun setItem(row: Int, column: Int, item: ItemStack) {
        require(row in 0 until rows) { "Row must be between 0 and ${rows - 1}" }
        require(column in 0 until 9) { "Column must be between 0 and 8" }
        inventory.setItem(row * 9 + column, item)
    }

    fun setButton(slot: Int, button: GuiButton) {
        inventory.setItem(slot, button.getItemStack())
        buttons[slot] = button
    }

    fun getRows(): Int {
        return rows
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.clickedInventory != inventory) return
        val button = buttons[event.slot] ?: return
        button.onClick(event)
        event.isCancelled = true
    }
}
