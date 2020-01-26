package jsObject

import kotlinext.js.Object
import kotlinext.js.clone
import kotlinext.js.jsObject
import kotlinx.serialization.internal.MapEntry


external interface JsObject<T : Any?> {
    fun toLocaleString(): String
    fun valueOf(): dynamic
    fun hasOwnProperty(v: String): Boolean
    fun isPrototypeOf(v: Any): Boolean
    fun propertyIsEnumerable(v: String): Boolean
}

fun <T : Any?> JsObject<T>.iterator() = entries.iterator()

fun <T : Any?> JsObject<T>.asSequence() = entries.asSequence()

fun <T : Any?> JsObject<T>.toMap() = mapOf(*entries.toTypedArray())

val <T : Any?> JsObject<T>.size: Int
    get() = keys.size

//val <T : Any?> JsObject<T>.entries: MutableSet<MutableMap.MutableEntry<String, T>>
//    get() = keys.map {
//        object : MutableMap.MutableEntry<String, T> {
//            override val key: String = it
//            override val value: T = this@entries.asDynamic()[it]
//            override fun setValue(newValue: T): T {
//                val prev = value
//                this@entries.asDynamic()[key] = newValue
//                return prev
//            }
//        }
//    }.toMutableSet()

val <T : Any?> JsObject<T>.entries: Set<Pair<String, T>> get() = keys.map { it to get(it)!! }.toSet()

val <T : Any?> JsObject<T>.keys: Set<String>
    get() = setOf(*Object.keys(this@keys))

val <T : Any?> JsObject<T>.values: Set<T>
    get() = keys.map { get(it)!! }.toSet()


operator fun <T : Any?> JsObject<T>.contains(key: String) = containsKey(key)

fun <T : Any?> JsObject<T>.containsKey(key: String) = keys.contains(key)

fun <T : Any?> JsObject<T>.containsValue(value: T) = values.contains(value)

operator fun <T : Any?> JsObject<T>.get(key: String?): T? = key?.let { asDynamic()[it] }

inline fun <T : Any?> JsObject<T>.getOrElse(key: String, defaultValue: () -> T): T = get(key) ?: defaultValue()

internal inline fun <T : Any?> JsObject<T>.getOrElseNullable(key: String, defaultValue: () -> T): T {
    val value = get(key)
    if (value == null && !containsKey(key)) {
        return defaultValue()
    } else {
        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}

inline fun <T : Any?> JsObject<T>.getOrPut(key: String, defaultValue: () -> T): T {
    val value = get(key)
    return if (value == null) {
        val answer = defaultValue()
        put(key, answer)
        answer
    } else {
        value
    }
}

fun <T : Any?> JsObject<T>.isEmpty() = keys.isEmpty()
fun <T : Any?> JsObject<T>.isNotEmpty() = !isEmpty()

fun <T : Any?> JsObject<T>.clear() {
    keys.forEach {
        remove(it)
    }
}

operator fun <T : Any?> JsObject<T>.set(key: String, value: T) = put(key, value)

fun <T : Any?> JsObject<T>.put(key: String, value: T): T? {
    val prev = get(key)
    asDynamic()[key] = value
    return prev
}

fun <T : Any?> JsObject<T>.putAll(from: Map<out String, T>) = from.forEach { (key, value) ->
    put(key, value)
}

fun <T : Any?> JsObject<T>.putAll(from: Collection<MutableMap.MutableEntry<out String, out T>>) =
    from.forEach { (key, value) ->
        put(key, value)
    }

fun <T : Any?> JsObject<T>.putAll(from: Collection<MapEntry<out String, out T>>) = from.forEach { (key, value) ->
    put(key, value)
}

fun <T : Any?> JsObject<T>.putAll(from: Collection<Pair<String, T>>) = from.forEach { (key, value) ->
    put(key, value)
}

fun <T : Any?> JsObject<T>.remove(key: String): T? {
    val prev = get(key)
    js("delete this[key]")
    return prev
}

operator fun <T : Any?> JsObject<T>.plusAssign(other: JsObject<out T>) {
    putAll(other.entries)
}

operator fun <T : Any?> JsObject<T>.plus(other: JsObject<out T>) = clone(this).apply {
    this += other
}

operator fun <T : Any?> JsObject<T>.plus(other: JsObject<out Any?>) = clone(this as JsObject<Any?>).apply {
    this += other
}

fun <T : Any?> Any.asJsObject(): JsObject<T> = this as JsObject<T>

//fun Any.asJsObject() = this as JsObject<Any?>
//
//fun javaScriptObjectOf(vararg pairs: Pair<String, Any?>) = jsObject<JsObject<Any?>> {
//    pairs.forEach { (key, value) ->
//        this[key] = value
//    }
//}
fun <T : Any?> Array<Pair<String, T>>.toJsObject() = jsObjectOf(*this)
fun <T : Any?> Collection<Pair<String, T>>.toJsObject() = jsObjectOf(*toTypedArray())
fun <T : Any?> jsObjectOf(vararg pairs: Pair<String, T>) = jsObject<JsObject<T>> {
    pairs.forEach { (key, value) ->
        this[key] = value
    }
}


//fun javaScriptObjectOf(map: Map<String, Any?>) = jsObject<JsObject<Any?>> {
//    putAll(map)
//}

fun <T : Any?> jsObjectOf(map: Map<String, T>) = jsObject<JsObject<T>> {
    putAll(map)
}

fun <T> Array<T>.push(item: T) {
    asDynamic().push(item)
}

fun <T> Array<T>.pop(): T = asDynamic().pop() as T