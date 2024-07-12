package com.diuhd.magenta.gui

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class PaginatedGui(title: String, lines: Int) : Gui(title, lines) {
    private val content: MutableList<GuiButton> = mutableListOf()
    private val slots: MutableList<Int> = mutableListOf()
    protected var currentPage: Int = 0
    protected var totalPages: Int = 1

    fun setContent(newContent: List<GuiButton>) {
        content.clear()
        content.addAll(newContent)
        updateTotalPages()
        populatePage(currentPage)
    }

    fun addContent(newContent: GuiButton) {
        content.add(newContent)
        updateTotalPages()
        populatePage(currentPage)
    }

    fun setPagedSlots(vararg pagedSlots: Int) {
        slots.clear()
        pagedSlots.forEach { slot ->
            slots.add(slot)
        }
        updateTotalPages()
        populatePage(currentPage)
    }

    fun nextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++
            populatePage(currentPage)
        }
    }

    fun previousPage() {
        if (currentPage > 0) {
            currentPage--
            populatePage(currentPage)
        }
    }

    private fun updateTotalPages() {
        totalPages = Math.ceil(content.size.toDouble() / slots.size).toInt()
    }

    private fun populatePage(page: Int) {
        inventory.clear()
        buttons.clear()
        val startIndex = page * slots.size
        val endIndex = Math.min(startIndex + slots.size, content.size)
        for (i in startIndex until endIndex) {
            setButton(slots[i - startIndex], content[i])
        }
        onOpen()
    }

    override fun open(entity: Player) {
        require(slots.isNotEmpty()) { "Slot list must not be empty" }
        populatePage(currentPage)
        super.open(entity)
    }
}
