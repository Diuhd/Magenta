package com.diuhd.magenta

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

object Cereal {
    fun serializeItem(item: ItemStack): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        BukkitObjectOutputStream(byteArrayOutputStream).use { bukkitObjectOutputStream ->
            bukkitObjectOutputStream.writeObject(item.serialize())
        }
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
    }

    fun deserializeItem(data: String): ItemStack {
        val byteArray = Base64.getDecoder().decode(data)
        ByteArrayInputStream(byteArray).use { byteArrayInputStream ->
            BukkitObjectInputStream(byteArrayInputStream).use { bukkitObjectInputStream ->
                val map = bukkitObjectInputStream.readObject() as Map<String, Any>
                return ItemStack.deserialize(map)
            }
        }
    }
}
