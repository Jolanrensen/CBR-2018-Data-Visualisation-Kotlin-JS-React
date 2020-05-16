package data

import data.ExamenResultaat.ONVOLDOENDE
import data.ExamenResultaat.VOLDOENDE

enum class ExamenResultaat(val title: String) {
    VOLDOENDE("Voldoende"),
    ONVOLDOENDE("Onvoldoende")
}

inline val Sequence<ExamenResultaatAantal>.voldoende
    get() = filter { it.examenResultaat == VOLDOENDE }

inline val Sequence<ExamenResultaatAantal>.onvoldoende
    get() = filter { it.examenResultaat == ONVOLDOENDE }