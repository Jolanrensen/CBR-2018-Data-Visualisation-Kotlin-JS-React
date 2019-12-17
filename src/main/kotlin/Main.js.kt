import data.*
import data.ExamenResultaat.VOLDOENDE
import data.ExamenResultaatCategorie.HANDGESCHAKELD
import data.ExamenResultaatVersie.EERSTE_EXAMEN_OF_TOETS
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

    println(
        Data.alleResultaten.filter {
            it.opleider == Data.alleOpleiders.values.find {
                it.naam.contains("ANWB") && it.plaatsnaam == "BREDA"
            }
        }.sumBy {
            it.examenResultaatAantallen.find {
                it.examenResultaatCategorie == HANDGESCHAKELD
                        && it.examenResultaatVersie == EERSTE_EXAMEN_OF_TOETS
                        && it.examenResultaat == VOLDOENDE
            }!!.aantal
        }
    )
}


