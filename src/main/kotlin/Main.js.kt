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