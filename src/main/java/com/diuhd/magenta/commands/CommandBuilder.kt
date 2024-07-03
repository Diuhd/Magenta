package com.diuhd.magenta.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.*
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Field

class CommandBuilder {
    data class FilterCodec(val filter: (CommandArgs) -> Boolean, val warningMessage: String)

    companion object {
        fun create(plugin: JavaPlugin): InitialState {
            return InitialState(plugin)
        }
    }

    class InitialState(private val plugin: JavaPlugin) {
        private lateinit var commandHandler: (CommandArgs) -> Boolean
        private val filterNodes: MutableList<FilterCodec> = mutableListOf()

        fun checkPlayer(warning: String): InitialState {
            filter(warning) { c ->
                c is CommandArgs.PlayerCommandArgs
            }
            return this
        }

        fun checkOp(warning: String): InitialState {
            filter(warning) { c ->
                c.sender.isOp
            }
            return this
        }

        fun handler(handlerFunc: (CommandArgs) -> Boolean): FinalState {
            commandHandler = handlerFunc
            return FinalState(plugin, filterNodes, commandHandler)
        }

        fun filter(filterNode: (CommandArgs) -> Boolean): InitialState {
            filterNodes.add(FilterCodec(filterNode, "${ChatColor.RED}You cannot use this command!"))
            return this
        }

        fun filter(warning: String, filterNode: (CommandArgs) -> Boolean): InitialState {
            filterNodes.add(FilterCodec(filterNode, warning))
            return this
        }
    }

    class FinalState(
        private val plugin: JavaPlugin,
        private val filterNodes: MutableList<FilterCodec>,
        private val commandHandler: (CommandArgs) -> Boolean
    ) {
        fun register(name: String) {
            val bukkitCommandMap: Field = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            bukkitCommandMap.setAccessible(true)
            val commandMap: CommandMap = bukkitCommandMap.get(Bukkit.getServer()) as CommandMap
            val command: Command = plugin.getCommand(name) ?: object: BukkitCommand(name) {
                override fun execute(p0: CommandSender, p1: String, p2: Array<out String>): Boolean {
                    val args: CommandArgs = CommandArgs.create(p0, this, p1, p2.toList())
                    for (node in filterNodes) {
                        if (!node.filter(args)) {
                            p0.sendMessage("${ChatColor.RED}You cannot use this command!")
                            return true
                        }
                    }
                    commandHandler(args)
                    return true
                }
            }
            commandMap.register(name, command)
        }
    }
}
