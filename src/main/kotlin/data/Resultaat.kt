package data

inline class Resultaat(val data: Array<Any>) {
    constructor(
        id: Int,
        product: Product,
        examenresultaatAantallen: Array<ExamenresultaatAantal>
    ) : this(
        arrayOf(
            id,
            product.name,
            examenresultaatAantallen
        )
    )

    inline val id: Int
        get() = data[0] as Int

    inline val product: Product
        get() = Product.valueOf(data[1] as String)

    inline val categorie: Categorie
        get() = product.categorie

    @Suppress("UNCHECKED_CAST")
    inline val examenresultaatAantallen: Array<ExamenresultaatAantal>
        get() = data[2] as Array<ExamenresultaatAantal>

    override fun toString() =
        "Resultaat(id = $id, product = $product, examenresultaatAantallen = $examenresultaatAantallen, categorie = $categorie)"




}