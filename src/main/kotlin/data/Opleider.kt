package data

import io.data2viz.time.Date

data class Opleider(
    val code: String,
    val naam: String,
    val startdatum: Date,
    val einddatum: Date,
    val straatnaam: String,
    val huisnummer: String,
    val huisnummerToevoeging: String = "",
    val postcode: String,
    val plaatsnaam: String,
    val gemeente: String
)