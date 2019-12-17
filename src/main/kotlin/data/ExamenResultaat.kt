package data

import data.ExamenResultaat.*

enum class ExamenResultaat {
    VOLDOENDE,
    ONVOLDOENDE
}

val Collection<ExamenResultaatAantal>.voldoende
    get() = filter { it.examenResultaat == VOLDOENDE }

val Collection<ExamenResultaatAantal>.onvoldoende
    get() = filter { it.examenResultaat == ONVOLDOENDE }