package data

data class Resultaat(
    val id: String,
    val opleider: Opleider,
    val product: Product,
    val examenlocatie: Examenlocatie,
    val examenResultaatAantallen: List<ExamenResultaatAantal>
) {
    val categorie: Categorie
        get() = product.categorie

}