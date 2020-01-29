import data.Categorie
import data.Data
import data.Product
import kotlinext.js.jsObject
import org.w3c.dom.MessageEvent
import org.w3c.dom.Worker
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.workers.ServiceWorkerGlobalScope
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

//external val self: ServiceWorkerGlobalScope
//fun runOnWorker(run: (evt: MessageEvent) -> Unit) {
//    var worker: Worker? = null
//    fun terminate() = worker!!.terminate()
//
//    worker = Worker(
//        URL.createObjectURL(
//            Blob(
//                arrayOf(
//                    {
//                        self.onmessage = {
//                            run(it)
//                            terminate()
//                        }
//                    }()
//                ),
//                jsObject { type = "text/javascript" }
//            )
//        )
//    ).apply {
//        postMessage(null)
//    }
//}





























