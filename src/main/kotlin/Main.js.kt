import kotlinx.css.RuleSet
import kotlinx.serialization.internal.MapEntry
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window


external fun alert(message: Any?)

fun main() {
    println("Hello Kotlin/JS")
    window.onload = {
        val root = document.getElementById("root")
        render(root) {
            app()
        }
    }

}

//var Tag.style: RuleSet
//    get() = error("style cannot be read from props")
//    set(value) = jsStyle {
//        CSSBuilder().apply(value).declarations.forEach { it: MapEntry<*, *> ->
//            this[it.key] = when (it.value) {
//                !is String, !is Number -> it.value.toString()
//                else -> it.value
//            }
//        }
//    }
//
//fun Tag.style(handler: RuleSet) {
//    style = handler
//}