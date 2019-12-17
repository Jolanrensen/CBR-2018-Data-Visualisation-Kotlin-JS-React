package data

import data.ExamenResultaatCategorie.*

enum class ExamenResultaatCategorie {
    Handgeschakeld,
    Automaat,
    Combi
}

val Collection<ExamenResultaatAantal>.handgeschakeld
    get() = filter { it.examenResultaatCategorie == Handgeschakeld }

val Collection<ExamenResultaatAantal>.automaat
    get() = filter { it.examenResultaatCategorie == Automaat }

val Collection<ExamenResultaatAantal>.combi
    get() = filter { it.examenResultaatCategorie == Combi }