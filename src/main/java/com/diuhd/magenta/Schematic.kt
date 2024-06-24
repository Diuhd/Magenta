package com.diuhd.magenta

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Schematic {
    private val scheme: ArrayList<String> = ArrayList()

    fun map(line: String): Schematic {
        require(line.length == 9) { "Map length must be 9" }
        scheme.add(line)
        return this
    }

    private fun convertToBooleanArray(): BooleanArray {
        val boolArray = BooleanArray(scheme.size * 9)
        for (i in scheme.indices) {
            for (j in scheme[i].indices) {
                boolArray[i * 9 + j] = scheme[i][j] == '1'
            }
        }
        return boolArray
    }

    fun apply(gui: Gui) {
        val boolArray: BooleanArray = convertToBooleanArray()
        boolArray.forEachIndexed { index, bool ->
            if (bool) gui.setItem(index, ItemStack(Material.GRAY_STAINED_GLASS_PANE))
        }
    }
}
