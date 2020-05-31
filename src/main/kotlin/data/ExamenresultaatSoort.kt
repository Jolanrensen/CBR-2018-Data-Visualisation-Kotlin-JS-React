package data

import data.ExamenresultaatSoort.AUTOMAAT
import data.ExamenresultaatSoort.COMBI
import data.ExamenresultaatSoort.HANDGESCHAKELD

enum class ExamenresultaatSoort(val title: String) {
    HANDGESCHAKELD("Handgeschakeld"),
    AUTOMAAT("Automaat"),
    COMBI("Combi")
}

inline val Sequence<ExamenresultaatAantal>.handgeschakeld
    get() = filter { it.examenresultaatSoort == HANDGESCHAKELD }

inline val Sequence<ExamenresultaatAantal>.automaat
    get() = filter { it.examenresultaatSoort == AUTOMAAT }

inline val Sequence<ExamenresultaatAantal>.combi
    get() = filter { it.examenresultaatSoort == COMBI }