package com.diuhd.magenta

import org.bukkit.Bukkit
import org.bukkit.event.*
import org.bukkit.plugin.java.JavaPlugin

/**
 * EventAssigner is responsible for assigning and handling Bukkit events.
 */
class EventAssigner : Listener {

    companion object {
        private val plugin: JavaPlugin = JavaPlugin.getProvidingPlugin(EventAssigner::class.java)

        /**
         * Creates an event assignment for the specified event type.
         */
        fun <T : Event> create(eventType: Class<T>): InitialState<T> {
            return EventAssigner().InitialState(eventType)
        }
    }

    /**
     * InitialState manages the initial configuration of an event assignment.
     */
    inner class InitialState<T : Event>(private val eventClass: Class<T>) {
        private var handler: (T) -> Unit = {}
        private var cancelled: Boolean = false
        private var triggers: Int? = null

        /**
         * Sets the event handler function.
         */
        fun handler(func: (T) -> Unit): FinalState<T> {
            handler = func
            return FinalState(eventClass, cancelled, handler, triggers)
        }

        /**
         * Specifies whether the event should be cancelled.
         */
        fun cancelled(bool: Boolean): InitialState<T> {
            if (Cancellable::class.java.isAssignableFrom(eventClass)) {
                cancelled = bool
            } else {
                throw IllegalArgumentException("Event type ${eventClass.simpleName} cannot be cancelled")
            }
            return this
        }

        /**
         * Sets the number of times the event should trigger before being unregistered.
         */
        fun expiresIn(runtime: Int): InitialState<T> {
            triggers = runtime
            return this
        }
    }

    /**
     * FinalState manages the final configuration and assignment of an event.
     */
    inner class FinalState<T : Event>(
        private val eventClass: Class<T>,
        private val cancelled: Boolean,
        private val handler: (T) -> Unit,
        private var triggers: Int?
    ) {

        /**
         * Assigns the event with the configured properties.
         */
        fun assign() {
            Bukkit.getPluginManager().registerEvent(eventClass, this@EventAssigner, EventPriority.NORMAL, { _, e -> handleEvent(eventClass.cast(e)) }, plugin)
        }

        private fun handleEvent(event: T) {
            if (eventClass.isAssignableFrom(event.javaClass)) {
                if (event is Cancellable && cancelled) {
                    event.isCancelled = true
                }
                handler(event)
                triggers?.let {
                    if (it > 1) {
                        triggers = it - 1
                    } else {
                        unregister()
                    }
                }
            }
        }

        /**
         * Unregisters the event handler.
         */
        private fun unregister() {
            HandlerList.unregisterAll(this@EventAssigner)
        }
    }
}
