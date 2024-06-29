package com.diuhd.magenta.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

sealed class CommandArgs(open val command: Command, open val label: String, open val args: List<String>) {
    abstract val sender: CommandSender

    data class PlayerCommandArgs(override val sender: Player, override val command: Command, override val label: String, override val args: List<String>) : CommandArgs(command, label, args)
    data class DefaultCommandArgs(override val sender: CommandSender, override val command: Command, override val label: String, override val args: List<String>) : CommandArgs(command, label, args)

    companion object {
        fun create(sender: CommandSender, command: Command, label: String, args: List<String>): CommandArgs {
            return if (sender is Player) {
                PlayerCommandArgs(sender, command, label, args)
            } else {
                DefaultCommandArgs(sender, command, label, args)
            }
        }
    }
}