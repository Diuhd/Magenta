package com.diuhd.magenta

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHandler(private val isPlayer: Boolean, private val isOp: Boolean, private val filters: MutableList<Boolean>, private val func: (CommandDesigner.CommandArgs) -> Unit): CommandExecutor {
    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if (isPlayer) {
            if (p0 !is Player) {
                p0.sendMessage("${ChatColor.RED}Only players can use this command!")
                return true
            }
        }
        if (isOp) {
            if (!p0.isOp) {
                p0.sendMessage("${ChatColor.RED}Only server operators can use this command!")
                return true
            }
        }
        filters.forEach { filter ->
            if (!filter) return true
        }
        func(CommandDesigner.CommandArgs(p0, p1, p2, p3.toList()))
        return true
    }
}