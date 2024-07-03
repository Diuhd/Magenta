package com.diuhd.magenta

import com.diuhd.magenta.commands.CommandBuilder
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class Example: JavaPlugin() {
    override fun onEnable() {
        // Commands Example
        CommandBuilder.create(this)
            .checkPlayer("Non players cannot use this command.")
            .handler { c ->
                val player: Player = c.sender as Player
                player.sendMessage("Hello Sir!")
                true
            }
            .register("hello")

        // Events Example
        EventAssigner.create(PlayerJoinEvent::class.java)
            .expiresIn(5)
            .handler { c ->
                c.player.sendMessage("Hello Sir!")
            }
            .assign()
    }
}