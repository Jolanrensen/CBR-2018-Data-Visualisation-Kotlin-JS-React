package data

import data.ExamenResultaatVersie.EERSTE_EXAMEN_OF_TOETS
import data.ExamenResultaatVersie.HEREXAMEN_OF_TOETS

enum class ExamenResultaatVersie {
    EERSTE_EXAMEN_OF_TOETS,
    HEREXAMEN_OF_TOETS
}

val Sequence<ExamenResultaatAantal>.eersteExamen
    get() = filter { it.examenResultaatVersie == EERSTE_EXAMEN_OF_TOETS }

val Sequence<ExamenResultaatAantal>.herExamen
    get() = filter { it.examenResultaatVersie == HEREXAMEN_OF_TOETS }