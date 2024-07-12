package com.diuhd.magenta.gui

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

abstract class Gui(title: String, lines: Int): InventoryHolder {
    private val _inventory: Inventory = Bukkit.createInventory(null, lines * 9, title)
    protected val buttons: MutableMap<Int, GuiButton> = mutableMapOf()
    protected val openSlots: MutableList<Int> = mutableListOf()
    private var registeredEvents: Boolean = false

    init {
        if (!registeredEvents) {
            val plugin: JavaPlugin = JavaPlugin.getProvidingPlugin(Gui::class.java)
            plugin.server.pluginManager.registerEvents(GuiListener(), plugin)
            registeredEvents = true
        }
    }

    abstract fun onOpen()
    abstract fun onClose()

    fun setButton(slot: Int, button: GuiButton) {
        buttons[slot] = button
        _inventory.setItem(slot, button.getItemStack())
    }

    fun setOpen(slot: Int) {
        openSlots.add(slot)
    }

    fun checkIfOpen(slot: Int): Boolean {
        return openSlots.contains(slot)
    }

    override fun getInventory(): Inventory = _inventory

    fun fill(item: ItemStack) {
        for (i in 0 until inventory.size) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, item)
            }
        }
    }

    fun getButton(slot: Int): GuiButton? {
        return buttons[slot]
    }

    fun setBorder(thickness: Int): Gui {
        val size = inventory.size
        val rows = size / 9

        for (row in 0 until rows) {
            for (col in 0 until 9) {
                if (row < thickness || row >= rows - thickness || col < thickness || col >= 9 - thickness) {
                    val slot = row * 9 + col
                    if (inventory.getItem(slot) == null) {
                        inventory.setItem(slot, ItemStack(Material.GRAY_STAINED_GLASS_PANE))
                    }
                }
            }
        }
        return this
    }

    open fun open(entity: Player) {
        onOpen()
        entity.openInventory(inventory)
    }
}
