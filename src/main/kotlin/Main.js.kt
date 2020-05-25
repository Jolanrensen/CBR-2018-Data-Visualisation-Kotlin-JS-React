import data.Data
import react.dom.render
import kotlin.browser.document

external fun alert(message: Any?)

fun main() {
    println("Hello Kotlin/JS")
    val root = document.getElementById("root")
    render(root) {
        app()
    }
}

enum class Loading {
    NOT_LOADED, LOADING, LOADED
}

fun Boolean.toInt() = if (this) 1 else 0

/**
 * Performs the given [action] on each element.
 */
inline fun <T> Iterable<T>.forEachApply(action: T.() -> Unit) {
    for (element in this) action(element)
}

/**
 * Performs the given [action] on each element, providing sequential index with the element.
 * @param [action] function that takes the index of an element and the element itself
 * and performs the desired action on the element.
 */
inline fun <T> Iterable<T>.forEachIndexedApply(action: T.(index: Int) -> Unit) {
    var index = 0
    for (item in this) action(item, index++)
}






























