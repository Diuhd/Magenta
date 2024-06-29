package com.diuhd.magenta.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandMap
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.java.JavaPlugin

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
            val command: PluginCommand = plugin.getCommand(name) ?: createCommand(name)
            command.setExecutor { sender, _, _, args ->
                val commandArgs = CommandArgs.create(sender, command, name, args.toList())
                for (node in filterNodes) {
                    if (!node.filter(commandArgs)) {
                        sender.sendMessage(node.warningMessage)
                        return@setExecutor true
                    }
                }
                commandHandler(commandArgs)
                sender.sendMessage("Command executed successfully!")
                true
            }
            getCommandMap().register(plugin.description.name, command)
        }

        private fun createCommand(name: String): PluginCommand {
            return try {
                val constructor =
                    PluginCommand::class.java.getDeclaredConstructor(String::class.java, JavaPlugin::class.java)
                constructor.isAccessible = true
                constructor.newInstance(name, plugin)
            } catch (e: NoSuchMethodException) {
                throw RuntimeException("Failed to find constructor for PluginCommand", e)
            } catch (e: Exception) {
                throw RuntimeException("Failed to create command: $name", e)
            }
        }

        private fun getCommandMap(): CommandMap {
            val commandMapField = Bukkit.getServer()::class.java.getDeclaredField("commandMap")
            commandMapField.isAccessible = true
            return commandMapField.get(Bukkit.getServer()) as CommandMap
        }
    }
}