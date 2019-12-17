import data.*
import data.ExamenResultaat.ONVOLDOENDE
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

//    val opleider = Data.alleOpleiders.values.find {
//        it.naam.contains("ANWB") && it.plaatsnaam == "BREDA"
//    }
//
//    println(
//        Data.alleResultaten.filter {
//            it.opleider == opleider
//        }.sumBy {
//            it.examenResultaatAantallen.filter {
//                //it.examenResultaatCategorie == HANDGESCHAKELD
//                         it.examenResultaatVersie == EERSTE_EXAMEN_OF_TOETS
//                        && it.examenResultaat == VOLDOENDE
//            }.sumBy { it.aantal }
//        }
//    )
//    println(
//        Data.alleResultaten.filter {
//            it.opleider == opleider
//        }.sumBy {
//            it.examenResultaatAantallen.filter {
//                //it.examenResultaatCategorie == HANDGESCHAKELD
//                        it.examenResultaatVersie == EERSTE_EXAMEN_OF_TOETS
//                        && it.examenResultaat == ONVOLDOENDE
//            }.sumBy { it.aantal }
//        }
//    )
}


