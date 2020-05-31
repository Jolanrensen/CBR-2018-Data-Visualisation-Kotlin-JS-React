package data

inline class ExamenresultaatAantal(val data: Array<Int>) {

    constructor(
        examenresultaatVersie: ExamenresultaatVersie,
        examenresultaatSoort: ExamenresultaatSoort,
        examenresultaat: Examenresultaat,
        aantal: Int
    ) : this(
        arrayOf(
            ExamenresultaatVersie.values().indexOf(examenresultaatVersie),
            ExamenresultaatSoort.values().indexOf(examenresultaatSoort),
            Examenresultaat.values().indexOf(examenresultaat),
            aantal
        )
    )

    inline val examenresultaatVersie: ExamenresultaatVersie
        get() = ExamenresultaatVersie.values()[data[0]]

    inline val examenresultaatSoort: ExamenresultaatSoort
        get() = ExamenresultaatSoort.values()[data[1]]

    inline val examenresultaat: Examenresultaat
        get() = Examenresultaat.values()[data[2]]

    inline val aantal: Int
        get() = data[3]

    override fun toString() =
        "ExamenresultaatAantal(examenResultaatVersie = $examenresultaatVersie, examenResultaatCategorie = $examenresultaatSoort, examenResultaat = $examenresultaat, aantal = $aantal)"
}