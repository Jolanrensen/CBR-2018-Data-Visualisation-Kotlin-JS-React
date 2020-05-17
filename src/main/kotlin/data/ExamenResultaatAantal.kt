package data

inline class ExamenResultaatAantal(val data: Array<Int>) {

    constructor(
        examenResultaatVersie: ExamenResultaatVersie,
        examenResultaatCategorie: ExamenResultaatCategorie,
        examenResultaat: ExamenResultaat,
        aantal: Int
    ) : this(
        arrayOf(
            ExamenResultaatVersie.values().indexOf(examenResultaatVersie),
            ExamenResultaatCategorie.values().indexOf(examenResultaatCategorie),
            ExamenResultaat.values().indexOf(examenResultaat),
            aantal
        )
    )

    inline val examenResultaatVersie: ExamenResultaatVersie
        get() = ExamenResultaatVersie.values()[data[0]]

    inline val examenResultaatCategorie: ExamenResultaatCategorie
        get() = ExamenResultaatCategorie.values()[data[1]]

    inline val examenResultaat: ExamenResultaat
        get() = ExamenResultaat.values()[data[2]]

    inline val aantal: Int
        get() = data[3]

    override fun toString() =
        "ExamenResultaatAantal(examenResultaatVersie = $examenResultaatVersie, examenResultaatCategorie = $examenResultaatCategorie, examenResultaat = $examenResultaat, aantal = $aantal)"
}