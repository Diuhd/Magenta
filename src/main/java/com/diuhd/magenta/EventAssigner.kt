package com.diuhd.magenta

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Method

class EventAssigner<T : Event>(private val eventClass: Class<T>) : Listener {
    private var handler: (T) -> Unit = {}
    private var cancelled: Boolean = false
    private var remainingTriggers: Int? = null

    companion object {
        private val onGoingEvents: MutableMap<String, Listener> = mutableMapOf()
        private val plugin: JavaPlugin = JavaPlugin.getProvidingPlugin(EventAssigner::class.java)
    }

    init {
        checkEventClassHasHandlerList(eventClass)
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

    private fun checkEventClassHasHandlerList(eventClass: Class<*>) {
        try {
            val method: Method = eventClass.getDeclaredMethod("getHandlerList")
            if (!HandlerList::class.java.isAssignableFrom(method.returnType)) {
                throw IllegalArgumentException("Event type ${eventClass.simpleName} does not have a valid getHandlerList method")
            }
        } catch (e: NoSuchMethodException) {
            throw IllegalArgumentException("Event type ${eventClass.simpleName} does not have a getHandlerList method")
        }
    }

    fun handler(func: (T) -> Unit): EventAssigner<T> {
        handler = func
        return this
    }

    fun assign(key: String? = null) {
        plugin.server.pluginManager.registerEvents(this, plugin)
        key?.let { onGoingEvents[it] = this }
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
