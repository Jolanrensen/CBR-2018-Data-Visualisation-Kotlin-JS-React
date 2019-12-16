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

    // TODO TEST
//    CoroutineScope(Dispatchers.Default).launch {
    val xmlhttp = XMLHttpRequest()
    xmlhttp.open("GET", "opleiderresultaten-01072017-tm-30062018.csv", false)

    xmlhttp.send()
    val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null

    val data = result
        ?.split('\n')
        ?.map { it.split(';') }
        ?: run {
            println("Reading data failed!!")
            return
        }

    for (item in data.first()) {
        println(item)
    }
}


//}