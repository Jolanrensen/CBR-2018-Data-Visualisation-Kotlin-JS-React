package data

import data.ExamenResultaatCategorie.*

enum class ExamenResultaatCategorie {
    HANDGESCHAKELD,
    AUTOMAAT,
    COMBI
}

val Collection<ExamenResultaatAantal>.handgeschakeld
    get() = filter { it.examenResultaatCategorie == HANDGESCHAKELD }

val Collection<ExamenResultaatAantal>.automaat
    get() = filter { it.examenResultaatCategorie == AUTOMAAT }

val Collection<ExamenResultaatAantal>.combi
    get() = filter { it.examenResultaatCategorie == COMBI }