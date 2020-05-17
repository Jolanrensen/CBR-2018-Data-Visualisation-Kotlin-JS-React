package data

class Resultaat(
    val id: Int,
    val opleider: Opleider,
    val product: Product,
    val examenlocatie: Examenlocatie,
    val examenResultaatAantallen: List<ExamenResultaatAantal>
) {
    val categorie: Categorie
        get() = product.categorie

    override fun toString() =
        "Resultaat(id = $id, opleider = ${opleider.content}, product = $product, examenlocatie = ${examenlocatie.content}, examenResultaatAantallen = $examenResultaatAantallen, categorie = $categorie)"
}
