package com.diuhd.magenta

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.java.JavaPlugin

class CommandDesigner private constructor(private val plugin: JavaPlugin) {

    data class CommandArgs(val sender: CommandSender, val command: org.bukkit.command.Command, val label: String, val args: List<String>)

    companion object {
        fun create(plugin: JavaPlugin): Builder {
            return Builder(plugin)
        }
    }

    class Builder(private val plugin: JavaPlugin) {
        private var checkPlayer: Boolean = false
        private var checkOp: Boolean = false
        private val filters: MutableList<(CommandArgs) -> Boolean> = mutableListOf()
        private lateinit var executor: (CommandArgs) -> Boolean

        fun checkPlayer(enabled: Boolean) = apply { checkPlayer = enabled }

        fun checkOp(enabled: Boolean) = apply { checkOp = enabled }

        fun filter(filterFunction: (CommandArgs) -> Boolean) = apply { filters.add(filterFunction) }

        fun handler(executorFunction: (CommandArgs) -> Boolean) = apply { executor = executorFunction }

        fun register(name: String) {
            val command: PluginCommand = plugin.getCommand(name) ?: createCommand(name)
            command.setExecutor { sender, cmd, label, args ->
                val commandArgs = CommandArgs(sender, cmd, label, args.toList())
                if (!isValidCommand(sender, commandArgs)) {
                    return@setExecutor true
                }
                executor(commandArgs)
            }
            getCommandMap().register(plugin.description.name, command)
        }

        private fun createCommand(name: String): PluginCommand {
            return try {
                val constructor = PluginCommand::class.java.getDeclaredConstructor(String::class.java, JavaPlugin::class.java)
                constructor.isAccessible = true
                constructor.newInstance(name, plugin)
            } catch (e: Exception) {
                throw RuntimeException("Failed to create command: $name", e)
            }
        }

        private fun isValidCommand(sender: CommandSender, commandArgs: CommandArgs): Boolean {
            if (checkPlayer && sender !is org.bukkit.entity.Player) {
                sender.sendMessage("This command can only be used by players.")
                return false
            }
            if (checkOp && !sender.isOp) {
                sender.sendMessage("You must be an operator to use this command.")
                return false
            }
            if (filters.any { !it(commandArgs) }) {
                sender.sendMessage("You do not meet the requirements to use this command.")
                return false
            }
            return true
        }

        private fun getCommandMap(): org.bukkit.command.CommandMap {
            val commandMapField = Bukkit.getServer()::class.java.getDeclaredField("commandMap")
            commandMapField.isAccessible = true
            return commandMapField.get(Bukkit.getServer()) as org.bukkit.command.CommandMap
        }
    }
}
