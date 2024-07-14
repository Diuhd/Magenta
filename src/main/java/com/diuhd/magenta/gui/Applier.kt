package com.diuhd.magenta.gui

class Applier(private val schematic: Schematic, private val gui: Gui) {
    private val filledSlots: MutableList<Int> = mutableListOf(gui.inventory.size)
    fun getSchematic(): Schematic = schematic
    fun applyButton(button: GuiButton): Applier {
        val boolArray: BooleanArray = schematic.getBooleanArray()
        val slot: Int = getFirstTrueIndex(boolArray)
        gui.addButton(slot, button)
        filledSlots.add(slot)
        return this
    }
    private fun getFirstTrueIndex(boolArray: BooleanArray): Int {
        for (i in boolArray.indices) {
            if (boolArray[i] && i !in filledSlots) {
                return i
            }
        }
        return -1
    }
}