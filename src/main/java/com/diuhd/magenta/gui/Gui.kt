package com.diuhd.magenta.gui

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

abstract class Gui(private val rows: Int, title: String) : InventoryHolder {

    companion object {
        fun register(plugin: JavaPlugin) {
            plugin.server.pluginManager.registerEvents(GuiListener(), plugin)
        }
    }

    private val inventory: Inventory = Bukkit.createInventory(this, rows * 9, title)
    private val buttons = MutableList<GuiButton?>(rows * 9) { null }
    private val borderItems: BooleanArray = BooleanArray(rows * 9) { false }

    init {
        make()
    }

    protected abstract fun make()

    override fun getInventory(): Inventory {
        return inventory
    }

    fun setItem(slot: Int, item: ItemStack): Gui {
        inventory.setItem(slot, item)
        return this
    }

    fun setItem(row: Int, column: Int, item: ItemStack): Gui {
        val slot = validateAndGetSlot(row, column)
        return setItem(slot, item)
    }

    fun checkIfItemIsBorder(slot: Int): Boolean {
        return borderItems[slot]
    }

    fun setButton(slot: Int, button: GuiButton): Gui {
        require(button.getItemStack().type != Material.AIR)
        setItem(slot, button.getItemStack())
        buttons[slot] = button
        return this
    }

    fun setButton(row: Int, column: Int, button: GuiButton): Gui {
        val slot = validateAndGetSlot(row, column)
        return setButton(slot, button)
    }

    fun getButton(slot: Int): GuiButton? {
        return buttons[slot]
    }

    fun setBorder(thickness: Int): Gui {
        require(thickness * 2 < rows) { "Border is too thick for the number of rows!" }
        require(thickness * 2 < 9) { "Border is too thick for the number of columns!" }

        val schem = Schematic()
        repeat(thickness) {
            schem.map("1".repeat(9))
        }
        repeat(rows - thickness * 2) {
            schem.map("1".repeat(thickness) + "0".repeat(9 - thickness * 2) + "1".repeat(thickness))
        }
        repeat(thickness) {
            schem.map("1".repeat(9))
        }
        schem.getBooleanArray().copyInto(borderItems)
        schem.apply(this, ItemStack(Material.GRAY_STAINED_GLASS_PANE))
        return this
    }

    private fun validateAndGetSlot(row: Int, column: Int): Int {
        require(row in 0 until rows) { "Row must be between 0 and ${rows - 1}" }
        require(column in 0 until 9) { "Column must be between 0 and 8" }
        return row * 9 + column
    }
}
