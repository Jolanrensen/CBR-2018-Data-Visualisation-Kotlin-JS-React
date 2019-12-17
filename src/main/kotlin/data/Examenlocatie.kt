package data

data class Examenlocatie(
    val naam: String,
    val straatnaam: String,
    val huisnummer: String,
    val huisnummerToevoeging: String = "",
    val postcode: String,
    val plaatsnaam: String
)