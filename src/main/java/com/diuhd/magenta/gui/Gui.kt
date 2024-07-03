package com.diuhd.magenta.gui

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.java.JavaPlugin

class Gui(private val rows: Int, title: String) {
    private val INVENTORY_WIDTH = 9
    private val inventory: Inventory = Bukkit.createInventory(null, rows * INVENTORY_WIDTH, title)
    val buttons: MutableList<GuiButton?> = MutableList(INVENTORY_WIDTH * rows) { null }
    val borderItems: BooleanArray = BooleanArray(INVENTORY_WIDTH * rows) { false }

    companion object {
        private var initialized: Boolean = false
        fun initialize(plugin: JavaPlugin) {
            plugin.server.pluginManager.registerEvents(GuiListener(), plugin)
            initialized = true
        }
        private val BORDER_ITEM = ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build()
    }

    init {
        require(rows in 1..6) { "Rows must be between 1 and 6!" }
        require(initialized) { "Gui must be initialized before creating one! (Gui#initialize)" }
    }

    fun open(player: Player) {
        player.setMetadata("OpenGui", FixedMetadataValue(JavaPlugin.getProvidingPlugin(Gui::class.java), this))
        player.openInventory(inventory)
    }

    fun setBorder(thickness: Int): Gui {
        require(thickness * 2 < rows) { "Border is too thick for the number of rows!" }
        require(thickness * 2 < INVENTORY_WIDTH) { "Border is too thick for the number of columns!" }

        val schem = Schematic()
        repeat(thickness) {
            schem.map("1".repeat(INVENTORY_WIDTH))
        }
        repeat(rows - thickness * 2) {
            schem.map("1".repeat(thickness) + "0".repeat(INVENTORY_WIDTH - thickness * 2) + "1".repeat(thickness))
        }
        repeat(thickness) {
            schem.map("1".repeat(INVENTORY_WIDTH))
        }
        schem.getBooleanArray().copyInto(borderItems)
        schem.apply(this, BORDER_ITEM)
        return this
    }

    fun setItem(slot: Int, item: ItemStack): Gui {
        inventory.setItem(slot, item)
        return this
    }

    fun setItem(row: Int, column: Int, item: ItemStack): Gui {
        require(row in 0 until rows) { "Row must be between 0 and ${rows - 1}" }
        require(column in 0 until INVENTORY_WIDTH) { "Column must be between 0 and 8" }
        setItem((row - 1) * INVENTORY_WIDTH + (column - 1), item)
        return this
    }

    fun setButton(slot: Int, button: GuiButton): Gui {
        setItem(slot, button.getItemStack())
        buttons[slot] = button
        return this
    }

    fun setButton(row: Int, column: Int, button: GuiButton): Gui {
        require(row in 0 until rows) { "Row must be between 0 and ${rows - 1}" }
        require(column in 0 until INVENTORY_WIDTH) { "Column must be between 0 and 8" }
        setItem(row, column, button.getItemStack())
        buttons[row * INVENTORY_WIDTH + column] = button
        return this
    }
}
