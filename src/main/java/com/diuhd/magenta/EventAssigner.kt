package com.diuhd.magenta

import org.bukkit.Bukkit
import org.bukkit.event.*
import org.bukkit.plugin.java.JavaPlugin

class EventAssigner<T : Event>(private val eventClass: Class<T>) : Listener {
    private var handler: (T) -> Unit = {}
    private var cancelled: Boolean = false
    private var remainingTriggers: Int? = null

    companion object {
        private val plugin: JavaPlugin = JavaPlugin.getProvidingPlugin(EventAssigner::class.java)
    }

    @EventHandler
    private fun handleEvent(event: T) {
        if (eventClass.isAssignableFrom(event.javaClass)) {
            if (event is Cancellable && cancelled) {
                event.isCancelled = true
            }
            handler(event)
            remainingTriggers?.let {
                if (it > 1) {
                    remainingTriggers = it - 1
                } else {
                    unregister()
                }
            }
        }
    }

    fun handler(func: (T) -> Unit): EventAssigner<T> {
        handler = func
        return this
    }

    fun assign() {
        Bukkit.getPluginManager().registerEvent(eventClass, this, EventPriority.NORMAL, { _, e -> handleEvent(eventClass.cast(e)) }, plugin)
    }

    fun cancelled(bool: Boolean): EventAssigner<T> {
        if (Cancellable::class.java.isAssignableFrom(eventClass)) {
            cancelled = bool
        } else {
            throw IllegalArgumentException("Event type ${eventClass.simpleName} cannot be cancelled")
        }
        return this
    }

    fun expiresIn(runtime: Int): EventAssigner<T> {
        remainingTriggers = runtime
        return this
    }

    fun unregister() {
        HandlerList.unregisterAll(this)
    }
}
