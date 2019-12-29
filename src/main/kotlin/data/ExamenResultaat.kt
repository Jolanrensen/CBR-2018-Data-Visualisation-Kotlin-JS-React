package data

import data.ExamenResultaat.ONVOLDOENDE
import data.ExamenResultaat.VOLDOENDE

enum class ExamenResultaat {
    VOLDOENDE,
    ONVOLDOENDE
}

val Sequence<ExamenResultaatAantal>.voldoende
    get() = filter { it.examenResultaat == VOLDOENDE }

val Sequence<ExamenResultaatAantal>.onvoldoende
    get() = filter { it.examenResultaat == ONVOLDOENDE }