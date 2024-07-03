package com.diuhd.magenta.gui

import org.bukkit.Material
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

    private fun convertToBooleanArray(stringArray: List<String>): BooleanArray =
        BooleanArray(scheme.size * LINE_LENGTH).apply {
            scheme.forEachIndexed { rowIndex, line ->
                line.forEachIndexed { columnIndex, char ->
                    this[rowIndex * LINE_LENGTH + columnIndex] = char == '1'
                }
            }
        }

    fun apply(gui: Gui, material: Material) {
        val boolArray = convertToBooleanArray(scheme)
        boolArray.forEachIndexed { index, isBorder ->
            if (isBorder) {
                gui.setItem(index, ItemBuilder(material).setName(" ").setLore(" ").build())
            }
        }
    }

    fun getBooleanArray(): BooleanArray {
        return convertToBooleanArray(scheme)
    }

    fun apply(gui: Gui, itemStack: ItemStack) {
        val boolArray = convertToBooleanArray(scheme)
        boolArray.forEachIndexed { index, isBorder ->
            if (isBorder) {
                gui.setItem(index, itemStack)
            }
        }
    }
}
