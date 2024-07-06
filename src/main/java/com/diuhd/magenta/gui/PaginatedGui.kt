package com.diuhd.magenta.gui

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

abstract class PaginatedGui(rows: Int, title: String) : Gui(rows, title) {

    private val items = mutableListOf<ItemStack>()
    private var page = 0

    init {
        make()
        updateInventory()
    }

    abstract override fun make()

    fun addItem(item: ItemStack): PaginatedGui {
        items.add(item)
        updateInventory()
        return this
    }

    private fun updateInventory() {
        inventory.clear()
        val start = page * (inventory.size - 9) // Reserve last row for navigation
        val end = (start + (inventory.size - 9)).coerceAtMost(items.size)

        for (i in start until end) {
            setItem(i - start, items[i])
        }

        // Add navigation items
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
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta?.setDisplayName(name)
        item.itemMeta = meta

        return GuiButton(item, onClick)
    }

    fun handleInventoryClick(event: InventoryClickEvent) {
        val slot = event.rawSlot
        if (slot < 0 || slot >= inventory.size) return

        val button = getButton(slot)
        button?.onClick(event)
    }
}
