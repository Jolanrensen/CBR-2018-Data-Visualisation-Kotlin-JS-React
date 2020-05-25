package data

import data.ExamenresultaatVersie.EERSTE_EXAMEN_OF_TOETS
import data.ExamenresultaatVersie.HEREXAMEN_OF_TOETS

enum class ExamenresultaatVersie(val title: String) {
    EERSTE_EXAMEN_OF_TOETS("Eerste examen of -toets"),
    HEREXAMEN_OF_TOETS("Herexamen of -toets")
}

inline val Sequence<ExamenresultaatAantal>.eersteExamen
    get() = filter { it.examenresultaatVersie == EERSTE_EXAMEN_OF_TOETS }

inline val Sequence<ExamenresultaatAantal>.herexamen
    get() = filter { it.examenresultaatVersie == HEREXAMEN_OF_TOETS }