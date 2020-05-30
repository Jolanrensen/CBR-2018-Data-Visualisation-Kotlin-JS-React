package data

import kotlinx.css.Display
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

inline class Examenlocatie(val data: Array<String>) {

    constructor(
        naam: String,
        straatnaam: String,
        huisnummer: String,
        huisnummerToevoeging: String,
        postcode: String,
        plaatsnaam: String,
        gemeente: String,
        slagingspercentageEersteKeer: Double? = null,
        slagingspercentageHerkansing: Double? = null
    ) : this(
        arrayOf(
            naam,
            straatnaam,
            huisnummer,
            huisnummerToevoeging,
            postcode,
            plaatsnaam,
            gemeente,
            slagingspercentageEersteKeer?.toString() ?: "null",
            slagingspercentageHerkansing?.toString() ?: "null"
        )
    )

    inline val naam: String
        get() = data[0]

    inline val straatnaam: String
        get() = data[1]

    inline val huisnummer: String
        get() = data[2]

    @SerialName("huisnummer toevoeging")
    inline val huisnummerToevoeging: String
        get() = data[3]

    inline val postcode: String
        get() = data[4]

    inline val plaatsnaam: String
        get() = data[5]

    inline val gemeente: String
        get() = data[6]

    @SerialName("slagingspercentage eerste keer")
    inline var slagingspercentageEersteKeer: Double?
        get() = if (data[7] == "null") null else data[7].toDouble()
        set(value) {
            data[7] = value?.toString() ?: "null"
        }

    @SerialName("slagingspercentage herkansing")
    inline var slagingspercentageHerkansing: Double?
        get() = if (data[8] == "null") null else data[8].toDouble()
        set(value) {
            data[8] = value?.toString() ?: "null"
        }

    inline val content: Map<String, String>
        get() = mapOf(
            "naam" to naam,
            "straatnaam" to straatnaam,
            "huisnummer" to huisnummer,
            "huisnummer toevoeging" to huisnummerToevoeging,
            "postcode" to postcode,
            "plaatsnaam" to plaatsnaam,
            "gemeente" to gemeente,
            "slagingspercentage eerste keer" to slagingspercentageEersteKeer.asPercentage(),
            "slagingspercentage herkansing" to slagingspercentageHerkansing.asPercentage()
        )

}