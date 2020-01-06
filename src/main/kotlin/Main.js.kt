
import react.dom.render
import kotlin.browser.document
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

external fun alert(message: Any?)

fun main() {
    println("Hello Kotlin/JS")
    // window.onload = {
        val root = document.getElementById("root")
        render(root) {
            app()
        }
        // Data.buildData()
        // val opleider = Data.alleOpleiders.values.find {
        //     it.naam.contains("ANWB") && it.plaatsnaam == "BREDA"
        // }!!
        //
        // println("${
        // Data.getResults(listOf(opleider)).asSequence()
        //     .filter { it.product == Product.A || it.product == Product.A_NO }
        //     .sumBy {
        //         it.examenResultaatAantallen.asSequence()
        //             .voldoende
        //             .eersteExamen
        //             .sumBy { it.aantal }
        //     }
        // }/${
        // Data.getResults(listOf(opleider)).asSequence()
        //     .filter { it.product == Product.A || it.product == Product.A_NO }
        //     .sumBy {
        //         it.examenResultaatAantallen.asSequence()
        //             .eersteExamen
        //             .sumBy { it.aantal }
        //     }
        // }")
    // }
}

fun Boolean.toInt() = if (this) 1 else 0


val <T> KMutableProperty0<T>.delegate
    get() = delegateOf(::get, ::set)

val <T> KProperty0<T>.delegate
    get() = delegateOf(::get)

