package data

import data.ExamenResultaatCategorie.AUTOMAAT
import data.ExamenResultaatCategorie.COMBI
import data.ExamenResultaatCategorie.HANDGESCHAKELD

enum class ExamenResultaatCategorie(val title: String) {
    HANDGESCHAKELD("Handgeschakeld"),
    AUTOMAAT("Automaat"),
    COMBI("Combi")
}

inline val Sequence<ExamenResultaatAantal>.handgeschakeld
    get() = filter { it.examenResultaatCategorie == HANDGESCHAKELD }

inline val Sequence<ExamenResultaatAantal>.automaat
    get() = filter { it.examenResultaatCategorie == AUTOMAAT }

inline val Sequence<ExamenResultaatAantal>.combi
    get() = filter { it.examenResultaatCategorie == COMBI }