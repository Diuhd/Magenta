package com.diuhd.magenta.gui

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

abstract class Gui(title: String, private val size: Int) : InventoryHolder {
    private val inventory: Inventory = Bukkit.createInventory(this, size, title)
    private val buttons: MutableMap<Int, GuiButton> = mutableMapOf()
    private val openSlots: MutableSet<Int> = mutableSetOf()

    companion object {
        private var eventInit: Boolean = false
        init {
            if (!eventInit) {
                val plugin: JavaPlugin = JavaPlugin.getProvidingPlugin(Gui::class.java)
                plugin.logger.info("Magenta Gui Listeners are initialized!")
                plugin.server.pluginManager.registerEvents(GuiListener(), plugin)
                eventInit = true
            }
        }
    }

    override fun getInventory(): Inventory {
        return inventory
    }

    private fun toSlot(line: Int, column: Int): Int {
        return (line - 1) * 9 + (column - 1)
    }

    fun open(player: Player) {
        player.openInventory(inventory)
    }

    fun close() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.openInventory.topInventory.holder == this) {
                player.closeInventory()
            }
        }
    }

    fun addButton(slot: Int, button: GuiButton) {
        inventory.setItem(slot, button.getItemStack())
        buttons[slot] = button
    }

    fun addButton(line: Int, column: Int, button: GuiButton) {
        addButton(toSlot(line, column), button)
    }

    fun removeButton(slot: Int) {
        inventory.setItem(slot, null)
        buttons.remove(slot)
    }

    fun removeButton(line: Int, column: Int) {
        removeButton(toSlot(line, column))
    }

    fun getButton(slot: Int): GuiButton? {
        return buttons[slot]
    }

    fun getButton(line: Int, column: Int): GuiButton? {
        return getButton(toSlot(line, column))
    }

    fun setBorder(thickness: Int): Gui {
        for (i in 0 until thickness) {
            for (j in 0 until size / 9) {
                addItemToSlot(i + j * 9, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
                addItemToSlot(8 - i + j * 9, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
            }
        }
        for (i in 1 until (size / 9) - 1) {
            for (j in 0 until thickness) {
                addItemToSlot(j + i * 9, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
                addItemToSlot(8 - j + i * 9, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
            }
        }
        return this
    }

    fun fill(item: ItemStack) {
        for (i in 0 until inventory.size) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, item)
            }
        }
    }

    fun setOpen(slot: Int) {
        openSlots.add(slot)
    }

    fun setOpen(line: Int, column: Int) {
        setOpen(toSlot(line, column))
    }

    private fun addItemToSlot(slot: Int, item: ItemStack) {
        if (inventory.getItem(slot) == null) {
            inventory.setItem(slot, item)
        }
    }

    fun handleInventoryClick(event: InventoryClickEvent) {
        val slot = event.slot
        if (slot in openSlots) return
        val button = buttons[slot]
        button?.onClick(event)
        event.isCancelled = true
    }
}
