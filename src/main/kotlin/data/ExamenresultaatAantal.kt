package data

inline class ExamenresultaatAantal(val data: Array<Int>) {

    constructor(
        examenresultaatVersie: ExamenresultaatVersie,
        examenresultaatCategorie: ExamenresultaatCategorie,
        examenresultaat: Examenresultaat,
        aantal: Int
    ) : this(
        arrayOf(
            ExamenresultaatVersie.values().indexOf(examenresultaatVersie),
            ExamenresultaatCategorie.values().indexOf(examenresultaatCategorie),
            Examenresultaat.values().indexOf(examenresultaat),
            aantal
        )
    )

    inline val examenresultaatVersie: ExamenresultaatVersie
        get() = ExamenresultaatVersie.values()[data[0]]

    inline val examenresultaatCategorie: ExamenresultaatCategorie
        get() = ExamenresultaatCategorie.values()[data[1]]

    inline val examenresultaat: Examenresultaat
        get() = Examenresultaat.values()[data[2]]

    inline val aantal: Int
        get() = data[3]

    override fun toString() =
        "ExamenresultaatAantal(examenResultaatVersie = $examenresultaatVersie, examenResultaatCategorie = $examenresultaatCategorie, examenResultaat = $examenresultaat, aantal = $aantal)"
}