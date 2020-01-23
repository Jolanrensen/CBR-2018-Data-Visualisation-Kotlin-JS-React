import kotlinext.js.JsObject
import kotlinext.js.Object
import kotlinext.js.getOwnPropertyNames

fun <T: Any?> Map<String, T>.toJsObject(): Any {
    val obj = js("{}")
    forEach { (key, value) ->
        obj[key] = when (value) {
            is Collection<*> -> (value as Collection<Any?>).toTypedArray()
            is Map<*, *> -> try {
                (value as Map<String, Any?>).toJsObject()
            } catch (e: Exception) {
                value
            }
            else -> value
        }
    }
    return obj as Any
}

inline fun <reified T : Any?> Map<String, T>.toJsonString() = JSON.stringify(
   toJsObject()
)

//inline fun <reified T: Any?> String.fromJsonToMap(): HashMap<String, T> = JSON.parse<Any>(this).let {
//    val m = hashMapOf<String, T>().asDynamic()
//    m.map = it
//    val keys = js("Object.keys")
//    m.`$size` = keys(it).length
//    m
//}

inline fun <reified T : Any?> String.fromJsonToMap(): Map<String, T> = JSON.parse<Any>(this).run {
    Object.keys(this).map {
        it to asDynamic()[it]
    }.toMap()
}

inline fun <reified T : Any?> Collection<T>.toJsonString() = JSON.stringify(
    toTypedArray()
)

inline fun <reified T : Any?> String.fromJsonToArray() = JSON.parse<Array<T>>(this)

