package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Examenlocatie(
    val naam: String,
    val straatnaam: String,
    val huisnummer: String,

    @SerialName("huisnummer toevoeging")
    val huisnummerToevoeging: String = "",

    val postcode: String,
    val plaatsnaam: String,
    val gemeente: String,

    @Serializable(with = PercentageSerializer::class)
    @SerialName("slagingspercentage eerste keer")
    var slagingspercentageEersteKeer: Double? = null,

    @Serializable(with = PercentageSerializer::class)
    @SerialName("slagingspercentage herkansing")
    var slagingspercentageHerkansing: Double? = null
)