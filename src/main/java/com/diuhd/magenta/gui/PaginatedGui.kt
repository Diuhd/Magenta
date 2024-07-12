package com.diuhd.magenta.gui

import org.bukkit.entity.Player
import kotlin.math.ceil

abstract class PaginatedGui(title: String, lines: Int) : Gui(title, lines) {
    private val content: MutableList<GuiButton> = mutableListOf()
    private val slots: MutableList<Int> = mutableListOf()
    protected var currentPage: Int = 0

    fun setContent(newContent: List<GuiButton>) {
        content.clear()
        content.addAll(newContent)
        populatePage(currentPage)
    }

    fun addContent(newContent: GuiButton) {
        content.add(newContent)
        populatePage(currentPage)
    }

    fun setPagedSlots(vararg pagedSlots: Int) {
        slots.clear()
        pagedSlots.forEach { slot ->
            slots.add(slot)
        }
        populatePage(currentPage)
    }

    fun nextPage() {
        val maxPages: Int = ceil(content.size.toDouble() / slots.size.toDouble()).toInt()
        if (currentPage < maxPages - 1) {
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

    private fun populatePage(page: Int) {
        val startIndex = page * slots.size
        val endIndex = (startIndex + slots.size).coerceAtMost(content.size)
        for (i in startIndex until endIndex) {
            setButton(slots[i - startIndex], content[i])
        }
    }

    override fun open(entity: Player) {
        populatePage(currentPage)
        onOpen()
        require(slots.isNotEmpty()) { "Slot list must not be empty" }
        super.open(entity)
    }
}
