package com.diuhd.magenta

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class EventAssigner<T : Event>(private val eventClass: Class<T>) {
    private var handler: (T) -> Unit = { _ -> }
    private var cancelled: Boolean = false
    private var remainingTriggers: Int? = null
    private var listener: Listener? = null
    private val plugin: JavaPlugin = JavaPlugin.getProvidingPlugin(EventAssigner::class.java)

    init {
        listener = object : Listener {
            @EventHandler
            fun onEvent(event: T) {
                if (eventClass.isInstance(event)) {
                    val typedEvent = eventClass.cast(event)
                    if (typedEvent is Cancellable && cancelled) {
                        typedEvent.isCancelled = true
                    }
                    handler(typedEvent)

                    remainingTriggers?.let {
                        if (it > 1) {
                            remainingTriggers = it - 1
                        } else {
                            unregister()
                        }
                    }
                }
            }
        }
        plugin.server.pluginManager.registerEvents(listener!!, plugin)
    }

    fun handler(func: (T) -> Unit): EventAssigner<T> {
        handler = func
        return this
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

    private fun unregister() {
        listener?.let { HandlerList.unregisterAll(it) }
    }
}
