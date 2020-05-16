package data

import data.ExamenResultaatVersie.EERSTE_EXAMEN_OF_TOETS
import data.ExamenResultaatVersie.HEREXAMEN_OF_TOETS

enum class ExamenResultaatVersie(val title: String) {
    EERSTE_EXAMEN_OF_TOETS("Eerste examen of -toets"),
    HEREXAMEN_OF_TOETS("Herexamen of -toets")
}

inline val Sequence<ExamenResultaatAantal>.eersteExamen
    get() = filter { it.examenResultaatVersie == EERSTE_EXAMEN_OF_TOETS }

inline val Sequence<ExamenResultaatAantal>.herexamen
    get() = filter { it.examenResultaatVersie == HEREXAMEN_OF_TOETS }