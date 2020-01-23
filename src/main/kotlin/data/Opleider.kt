package data

import kotlin.js.Date


data class Opleider(
    val code: String,
    val naam: String,
    private val _startdatum: String, // DateString
    private val _einddatum: String, // DateString
    val straatnaam: String,
    val huisnummer: String,
    val huisnummerToevoeging: String = "",
    val postcode: String,
    val plaatsnaam: String,
    val gemeente: String
) {

    val startdatum get() = Date(_startdatum)

    val einddatum get() = Date(_einddatum)


}