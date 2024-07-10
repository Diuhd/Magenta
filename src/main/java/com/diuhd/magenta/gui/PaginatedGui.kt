package com.diuhd.magenta.gui

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

abstract class PaginatedGui(rows: Int, title: String) : Gui(rows, title) {
    private val slots: MutableList<Int> = mutableListOf()
    private val buttons: MutableList<GuiButton> = mutableListOf()
    private var page = 0

    init {
        initialize()
        make()
        updateInventory()
    }

    abstract override fun make()

    private fun initialize() {
        slots.clear()
        buttons.clear()
    }

    fun addButton(button: GuiButton): PaginatedGui {
        buttons.add(button)
        updateInventory()
        return this
    }

    fun pageSlots(vararg slots: Int): PaginatedGui {
        require(slots.all { it in 0 until inventory.size }) {
            "Slots must be within the range of the inventory size"
        }
        this.slots.clear()
        this.slots.addAll(slots.asList())
        updateInventory()  // Update inventory whenever slots change
        return this
    }

    fun setContents(list: List<GuiButton>): PaginatedGui {
        buttons.clear()
        buttons.addAll(list.map { it })
        updateInventory()
        return this
    }

    fun addContent(guiButton: GuiButton): PaginatedGui {
        buttons.add(guiButton)
        updateInventory()
        return this
    }

    private fun updateInventory() {
        inventory.clear()

        if (slots.isEmpty()) return

        val start = page * slots.size
        val end = (start + slots.size).coerceAtMost(buttons.size)

        for (i in start until end) {
            val slot = slots[i - start]
            setButton(slot, buttons[i])
        }
        
        if (page > 0) {
            setButton(inventory.size - 9, createNavigationButton("Previous Page", Material.ARROW) {
                page--
                updateInventory()
            })
        }

        if (end < buttons.size) {
            setButton(inventory.size - 1, createNavigationButton("Next Page", Material.ARROW) {
                page++
                updateInventory()
            })
        }
    }

    private fun createNavigationButton(name: String, material: Material, onClick: (InventoryClickEvent) -> Unit): GuiButton {
        val item = ItemStack(material).apply {
            itemMeta = itemMeta?.apply { setDisplayName(name) }
        }
        return GuiButton(item, onClick)
    }
}
