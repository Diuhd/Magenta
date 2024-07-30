package com.diuhd.magenta.gui

abstract class PaginatedGui(title: String, size: Int) : Gui(title, size) {

    private var currentPage = 0
    private val buttons: MutableList<GuiButton> = mutableListOf()
    private val slots: MutableList<Int> = mutableListOf()

    fun setButtons(buttons: List<GuiButton>) {
        this.buttons.clear()
        this.buttons.addAll(buttons)
        updatePage()
    }

    fun setSlots(slots: List<Int>) {
        this.slots.clear()
        this.slots.addAll(slots)
        updatePage()
    }

    fun nextPage() {
        if ((currentPage + 1) * slots.size < buttons.size) {
            currentPage++
            updatePage()
        }
    }

    fun previousPage() {
        if (currentPage > 0) {
            currentPage--
            updatePage()
        }
    }

    private fun updatePage() {
        clearButtons()
        val startIndex = currentPage * slots.size
        val endIndex = (startIndex + slots.size).coerceAtMost(buttons.size)
        for (i in startIndex until endIndex) {
            addButton(slots[i - startIndex], buttons[i])
        }
    }

    private fun clearButtons() {
        for (slot in slots) {
            removeButton(slot)
        }
    }
}
