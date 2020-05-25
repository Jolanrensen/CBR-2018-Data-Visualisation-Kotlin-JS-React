package data

import kotlin.js.Date

inline class Opleider(val data: Array<String>) {

    constructor(
        code: String,
        naam: String,
        startdatum: String,
        einddatum: String,
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
            code,
            naam,
            startdatum,
            einddatum,
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

    inline val code: String
        get() = data[0]

    inline val naam: String
        get() = data[1]


    inline val startdatum: Date
        get() = data[2].split('-').let {
            Date(
                day = it[0].toInt(),
                month = it[1].toInt(),
                year = it[2].toInt(),
                hour = 0,
                minute = 0,
                second = 0,
                millisecond = 0
            )
        }

    inline val einddatum: Date
        get() = data[3].split('-').let {
            Date(
                day = it[0].toInt(),
                month = it[1].toInt(),
                year = it[2].toInt(),
                hour = 0,
                minute = 0,
                second = 0,
                millisecond = 0
            )
        }

    inline val straatnaam: String
        get() = data[4]

    inline val huisnummer: String
        get() = data[5]

    inline val huisnummerToevoeging: String
        get() = data[6]

    inline val postcode: String
        get() = data[7]

    inline val plaatsnaam: String
        get() = data[8]

    inline val gemeente: String
        get() = data[9]


    inline var slagingspercentageEersteKeer: Double?
        get() = if (data[10] == "null") null else data[10].toDouble()
        set(value) {
            data[10] = value?.toString() ?: "null"
        }

    inline var slagingspercentageHerkansing: Double?
        get() = if (data[11] == "null") null else data[11].toDouble()
        set(value) {
            data[11] = value?.toString() ?: "null"
        }


    inline val content: Map<String, String>
        get() = mapOf(
            "code" to code,
            "naam" to naam,
            "startdatum" to startdatum.toDateString(),
            "einddatum" to einddatum.toDateString(),
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