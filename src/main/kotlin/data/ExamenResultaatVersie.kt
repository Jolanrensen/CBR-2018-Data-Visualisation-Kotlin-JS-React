package data

import data.ExamenResultaatVersie.*

enum class ExamenResultaatVersie {
    EersteExamenOfToets,
    HerexamenOfToets
}

val Collection<ExamenResultaatAantal>.eersteExamen
    get() = filter { it.examenResultaatVersie == EersteExamenOfToets }

val Collection<ExamenResultaatAantal>.herExamen
    get() = filter { it.examenResultaatVersie == HerexamenOfToets }