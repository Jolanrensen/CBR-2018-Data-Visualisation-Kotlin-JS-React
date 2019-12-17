import data.*
import kotlinx.coroutines.*
import org.w3c.xhr.XMLHttpRequest
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window
import kotlin.coroutines.CoroutineContext


external fun alert(message: Any?)

fun main() {
    println("Hello Kotlin/JS")
    window.onload = {
        val root = document.getElementById("root")
        render(root) {
            app()
        }
    }

    println(
        Data.alleResultaten.filter {
            it.opleider == Data.alleOpleiders.values.find {
                it.naam.contains("ANWB") && it.plaatsnaam == "BREDA"
            }
        }.sumBy {
            it.examenResultaatAantallen
                .handgeschakeld
                .eersteExamen
                .voldoende[0] // list is now size 1
                .aantal
        }
    )
}


