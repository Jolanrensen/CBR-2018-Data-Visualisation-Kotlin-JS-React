
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

external fun alert(message: Any?)

fun main() {
    println("Hello Kotlin/JS")
    window.onload = {
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
    }
}

fun Boolean.toInt() = if (this) 1 else 0

fun <T> readOnlyPropertyOf(
    get: () -> T
) = object : ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = get()
}

fun <T> readWritePropertyOf(
    get: () -> T,
    set: (T) -> Unit
) = object : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = get()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)
}

val <T> KMutableProperty0<T>.delegate
    get() = readWritePropertyOf(::get, ::set)

val <T> KProperty0<T>.delegate
    get() = readOnlyPropertyOf(::get)

