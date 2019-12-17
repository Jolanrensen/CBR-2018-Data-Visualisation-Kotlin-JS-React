package data

import data.ExamenResultaatVersie.*

enum class ExamenResultaatVersie {
    EERSTE_EXAMEN_OF_TOETS,
    HEREXAMEN_OF_TOETS
}

val Collection<ExamenResultaatAantal>.eersteExamen
    get() = filter { it.examenResultaatVersie == EERSTE_EXAMEN_OF_TOETS }

val Collection<ExamenResultaatAantal>.herExamen
    get() = filter { it.examenResultaatVersie == HEREXAMEN_OF_TOETS }