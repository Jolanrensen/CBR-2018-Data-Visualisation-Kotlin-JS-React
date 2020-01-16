
import react.dom.render
import kotlin.browser.document

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
    // val xmlhttp = XMLHttpRequest()
    // xmlhttp.open("GET", "pc6hnr20180801_gwb-vs2.csv", false)
    //
    // xmlhttp.send()
    // val result = (if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null)!!
    //
    // val postcodeToGemeente = result
    //     .split('\n')
    //     .asSequence()
    //     .map { it.split(';') }
    //     .map { it.getOrElse(0) { "" } to it.getOrElse(4) { "" } }
    //     .toMap() // filters out doubles
    //     .map { (postcode, gemeente) -> "$postcode;$gemeente" }
    //     .joinToString(separator = "\n")
    // println(postcodeToGemeente)
}

fun Boolean.toInt() = if (this) 1 else 0