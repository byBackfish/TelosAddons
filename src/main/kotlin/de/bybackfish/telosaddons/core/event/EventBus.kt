package de.bybackfish.telosaddons.core.event

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions
import kotlin.reflect.full.isSuperclassOf

class EventBus {

    private val subscribers = hashMapOf<KClass<*>, ArrayList<Subscriber>>()

    fun register(instance: Any, condition: () -> Boolean = { true }) {
        val clazz = instance::class
        clazz.functions.forEach {
            if (it.annotations.none { annotation -> annotation is Subscribe }) return@forEach
            val parameters = it.parameters
            val eventClass = parameters[1].type.classifier as KClass<*>
            val annotation = it.annotations.first { annotation -> annotation is Subscribe } as Subscribe

            if(!Event::class.isSuperclassOf(eventClass))
                throw IllegalArgumentException("Event class must be a subclass of Event! (Event: ${eventClass.simpleName})")

            val subscriber = Subscriber(instance, it, condition, annotation)
            println("[Telos] Added subscriber for event ${eventClass.simpleName} (Method: ${it.name}")

            subscribers.getOrPut(eventClass) { arrayListOf() }.add(subscriber)
        }
    }

    fun post(event: Event): Boolean {
        val eventClass = event::class
        subscribers[eventClass]?.sortedBy { -it.annotation.priority }?.forEach { subscriber ->
            if (event.isCancelled && !subscriber.annotation.ignoreCancelled) return@forEach
            if (subscriber.condition() && !subscriber.annotation.ignoreCondition)
                subscriber.method.call(subscriber.instance, event)
        }
        return event.isCancelled
    }

    class Subscriber(
        val instance: Any,
        val method: KFunction<*>,
        val condition: () -> Boolean,
        val annotation: Subscribe
    ) {}

}