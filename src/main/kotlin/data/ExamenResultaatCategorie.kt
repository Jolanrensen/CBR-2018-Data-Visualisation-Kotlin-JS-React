package data

import data.ExamenResultaatCategorie.AUTOMAAT
import data.ExamenResultaatCategorie.COMBI
import data.ExamenResultaatCategorie.HANDGESCHAKELD

enum class ExamenResultaatCategorie {
    HANDGESCHAKELD,
    AUTOMAAT,
    COMBI
}

val Sequence<ExamenResultaatAantal>.handgeschakeld
    get() = filter { it.examenResultaatCategorie == HANDGESCHAKELD }

val Sequence<ExamenResultaatAantal>.automaat
    get() = filter { it.examenResultaatCategorie == AUTOMAAT }

val Sequence<ExamenResultaatAantal>.combi
    get() = filter { it.examenResultaatCategorie == COMBI }