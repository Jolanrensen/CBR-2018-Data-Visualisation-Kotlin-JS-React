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

    val csv = result
        ?.split('\n')
        ?.map { it.split(';') }
        ?: run {
            println("Reading data failed!!")
            return
        }
    val headers = csv.first()
    val data = csv.drop(1)

    val rijscholen = hashSetOf<Pair<String, String>>()
    for (line in data) {
        rijscholen.add(line[0] to line[1])
    }

    println("no rijscholen = ${rijscholen.size}")

    val categorien = hashSetOf<Pair<String, String>>()
    data.forEach {
        categorien.add(it[9] to it[10])
    }
    println("no categorien = ${categorien.size}")
    println(categorien.map { "${it.first.replace('-', '_')}(\"${it.second}\")" })

//    for (item in data.first()) {
//        println(item)
//    }
}


//}