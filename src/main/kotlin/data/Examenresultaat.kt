package data

import data.Examenresultaat.ONVOLDOENDE
import data.Examenresultaat.VOLDOENDE

enum class Examenresultaat(val title: String) {
    VOLDOENDE("Voldoende"),
    ONVOLDOENDE("Onvoldoende")
}

inline val Sequence<ExamenresultaatAantal>.voldoende
    get() = filter { it.examenresultaat == VOLDOENDE }

inline val Sequence<ExamenresultaatAantal>.onvoldoende
    get() = filter { it.examenresultaat == ONVOLDOENDE }