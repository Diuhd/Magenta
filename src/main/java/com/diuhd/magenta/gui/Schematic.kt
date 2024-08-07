package com.diuhd.magenta.gui

import org.bukkit.inventory.ItemStack

class Schematic {
    companion object {
        private const val LINE_LENGTH = 9
    }

    private val scheme: MutableList<String> = mutableListOf()

    fun map(line: String): Schematic {
        require(line.length == LINE_LENGTH) { "Map length must be $LINE_LENGTH" }
        scheme.add(line)
        return this
    }

    private fun convertToBooleanArray(): BooleanArray =
        BooleanArray(scheme.size * LINE_LENGTH).apply {
            scheme.forEachIndexed { rowIndex, line ->
                line.forEachIndexed { columnIndex, char ->
                    this[rowIndex * LINE_LENGTH + columnIndex] = char == '1'
                }
            }
        }

    fun getBooleanArray(): BooleanArray {
        return convertToBooleanArray()
    }

    fun apply(inventory: Gui, itemStack: ItemStack) {
        val boolArray = convertToBooleanArray()
        boolArray.forEachIndexed { index, bool ->
            if (bool) {
                inventory.addButton(index, GuiButton(itemStack))
            }
        }
    }
}
