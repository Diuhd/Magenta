package com.diuhd.magenta.gui

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
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

    private val _inventory: Inventory by lazy { Bukkit.createInventory(this, rows * 9, title) }
    private val buttons = MutableList<GuiButton?>(rows * 9) { null }
    private val openSlots = mutableSetOf<Int>()

    init {
        make()
    }

    protected abstract fun make()

    // Override property accessor instead of method
    override fun getInventory(): Inventory = _inventory

    fun open(entity: Player) {
        entity.openInventory(_inventory)
    }

    fun setItem(slot: Int, item: ItemStack): Gui {
        _inventory.setItem(slot, item)
        return this
    }

    fun setItem(row: Int, column: Int, item: ItemStack): Gui {
        val slot = validateAndGetSlot(row, column)
        return setItem(slot, item)
    }

    fun checkOpenSlot(slot: Int): Boolean = openSlots.contains(slot)

    fun setButton(slot: Int, button: GuiButton): Gui {
        require(button.getItemStack().type != Material.AIR) { "Button item type cannot be AIR" }
        setItem(slot, button.getItemStack())
        buttons[slot] = button
        return this
    }

    fun setButton(row: Int, column: Int, button: GuiButton): Gui {
        val slot = validateAndGetSlot(row, column)
        return setButton(slot, button)
    }

    fun fill(item: ItemStack): Gui {
        for (slot in 0 until _inventory.size) {
            if (_inventory.getItem(slot) == null) {
                setItem(slot, item)
            }
        }
        return this
    }

    fun openSlot(row: Int, column: Int): Gui {
        val slot = validateAndGetSlot(row, column)
        return openSlot(slot)
    }

    fun openSlot(slot: Int): Gui {
        openSlots.add(slot)
        return this
    }

    fun getButton(slot: Int): GuiButton? = buttons[slot]

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
        schem.apply(this, ItemStack(Material.GRAY_STAINED_GLASS_PANE))
        return this
    }

    private fun validateAndGetSlot(row: Int, column: Int): Int {
        require(row in 0 until rows) { "Row must be between 0 and ${rows - 1}" }
        require(column in 0 until 9) { "Column must be between 0 and 8" }
        return row * 9 + column
    }
}
