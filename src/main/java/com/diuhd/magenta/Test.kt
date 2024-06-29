package com.diuhd.magenta

import com.diuhd.magenta.CommandDesigner
import org.bukkit.plugin.java.JavaPlugin

class Test: JavaPlugin() {
    override fun onEnable() {
        CommandDesigner.create(this)
            .checkPlayer(true)
    }
}