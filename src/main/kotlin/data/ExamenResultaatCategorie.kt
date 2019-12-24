package data

import data.ExamenResultaatCategorie.*

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