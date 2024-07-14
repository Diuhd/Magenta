package com.diuhd.magenta.gui

import org.bukkit.entity.Player
import kotlin.math.ceil
import kotlin.math.min

abstract class PaginatedGui(title: String, lines: Int) : Gui(title, lines) {
    private val content: MutableList<GuiButton> = mutableListOf()
    private val slots: MutableList<Int> = mutableListOf()
    protected var currentPage: Int = 0

    fun setContent(newContent: List<GuiButton>, refresh: Boolean = false) {
        content.clear()
        content.addAll(newContent)
        if (refresh) displayPage()
    }

    fun addContent(newContent: GuiButton, refresh: Boolean = false) {
        content.add(newContent)
        if (refresh) displayPage()
    }

    fun setPagedSlots(vararg pagedSlots: Int, refresh: Boolean = false) {
        slots.clear()
        pagedSlots.forEach { slot ->
            slots.add(slot)
        }
        if (refresh) displayPage()
    }

    fun nextPage() {
        val maxPages: Int = ceil(content.size.toDouble() / slots.size.toDouble()).toInt()
        if (currentPage < maxPages - 1) {
            currentPage++
        }
        displayPage()
    }

    fun previousPage() {
        if (currentPage > 0) {
            currentPage--
        }
        displayPage()
    }

    fun displayPage() {
        val page: Int = currentPage
        val startIndex: Int = (page - 1) * slots.size
        val endIndex: Int = min(slots.size * page - 1, content.size - 1)
        content.slice(startIndex..endIndex).forEachIndexed { index, guiButton ->
            setButton(slots[index], guiButton)
        }
    }

    override fun open(entity: Player) {
        displayPage()
        super.open(entity)
    }
}
