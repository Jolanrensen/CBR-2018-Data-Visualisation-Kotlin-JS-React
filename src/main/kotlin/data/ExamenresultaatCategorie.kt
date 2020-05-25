package data

import data.ExamenresultaatCategorie.AUTOMAAT
import data.ExamenresultaatCategorie.COMBI
import data.ExamenresultaatCategorie.HANDGESCHAKELD

enum class ExamenresultaatCategorie(val title: String) {
    HANDGESCHAKELD("Handgeschakeld"),
    AUTOMAAT("Automaat"),
    COMBI("Combi")
}

inline val Sequence<ExamenresultaatAantal>.handgeschakeld
    get() = filter { it.examenresultaatCategorie == HANDGESCHAKELD }

inline val Sequence<ExamenresultaatAantal>.automaat
    get() = filter { it.examenresultaatCategorie == AUTOMAAT }

inline val Sequence<ExamenresultaatAantal>.combi
    get() = filter { it.examenresultaatCategorie == COMBI }