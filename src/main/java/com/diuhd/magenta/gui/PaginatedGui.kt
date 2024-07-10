package com.diuhd.magenta.gui

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

abstract class PaginatedGui(rows: Int, title: String) : Gui(rows, title) {
    sealed class InventoryItem {
        data class Item(val itemStack: ItemStack) : InventoryItem()
        data class Button(val guiButton: GuiButton) : InventoryItem()
    }

    private val slots: MutableList<Int> = mutableListOf()
    private val items: MutableList<InventoryItem> = mutableListOf()
    private var page = 0

    init {
        initialize()
        make()
        updateInventory()
    }

    abstract override fun make()

    private fun initialize() {
        slots.clear()
        items.clear()
    }

    fun addItem(item: ItemStack): PaginatedGui {
        items.add(InventoryItem.Item(item))
        updateInventory()
        return this
    }

    fun addButton(button: GuiButton): PaginatedGui {
        items.add(InventoryItem.Button(button))
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

    fun setItemStackContents(list: List<ItemStack>): PaginatedGui {
        items.clear()
        items.addAll(list.map { InventoryItem.Item(it) })
        updateInventory()
        return this
    }

    fun setButtonContents(list: List<GuiButton>): PaginatedGui {
        items.clear()
        items.addAll(list.map { InventoryItem.Button(it) })
        updateInventory()
        return this
    }

    fun addItemStackContent(item: ItemStack): PaginatedGui {
        items.add(InventoryItem.Item(item))
        updateInventory()
        return this
    }

    fun addButtonContent(guiButton: GuiButton): PaginatedGui {
        items.add(InventoryItem.Button(guiButton))
        updateInventory()
        return this
    }

    private fun updateInventory() {
        inventory.clear()

        if (slots.isEmpty()) return

        val start = page * slots.size
        val end = (start + slots.size).coerceAtMost(items.size)

        for (i in start until end) {
            val slot = slots[i - start]
            when (val inventoryItem = items[i]) {
                is InventoryItem.Item -> setItem(slot, inventoryItem.itemStack)
                is InventoryItem.Button -> setButton(slot, inventoryItem.guiButton)
            }
        }

        // Navigation buttons
        if (page > 0) {
            setButton(inventory.size - 9, createNavigationButton("Previous Page", Material.ARROW) {
                page--
                updateInventory()
            })
        }

        if (end < items.size) {
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
