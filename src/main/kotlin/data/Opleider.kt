package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.Date

@Serializable
data class Opleider(
    val code: String,
    val naam: String,

    @Serializable(with = DateSerializer::class)
    val startdatum: Date,

    @Serializable(with = DateSerializer::class)
    val einddatum: Date,

    val straatnaam: String,
    val huisnummer: String,

    @SerialName("huisnummer toevoeging")
    val huisnummerToevoeging: String = "",

    val postcode: String,
    val plaatsnaam: String,
    val gemeente: String,

    @Serializable(with = PercentageSerializer::class)
    @SerialName("slagingspercentage eerste keer")
    var slagingsPercentageEersteKeer: Double? = null,

    @Serializable(with = PercentageSerializer::class)
    @SerialName("slagingspercentage herkansing")
    var slagingsPercentageHerkansing: Double? = null
)