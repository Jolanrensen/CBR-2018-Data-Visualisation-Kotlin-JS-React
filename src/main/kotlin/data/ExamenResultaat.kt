package data

import data.ExamenResultaat.*

enum class ExamenResultaat {
    Voldoende,
    OnVoldoende
}

val Collection<ExamenResultaatAantal>.voldoende
    get() = filter { it.examenResultaat == Voldoende }

val Collection<ExamenResultaatAantal>.onvoldoende
    get() = filter { it.examenResultaat == OnVoldoende }