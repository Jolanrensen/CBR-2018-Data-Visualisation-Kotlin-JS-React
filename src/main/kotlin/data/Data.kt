package data

import data.ExamenResultaat.*
import data.ExamenResultaatCategorie.*
import data.ExamenResultaatVersie.*
import io.data2viz.time.Date
import org.w3c.xhr.XMLHttpRequest

object Data {
//    val alleResultaten: ArrayList<Resultaat> = arrayListOf()
//        get() {
//            if (field.isEmpty()) buildResults()
//            return field
//        }

    val alleOpleiders = hashMapOf<String, Opleider>()
    val alleExamenLocaties = hashMapOf<String, Examenlocatie>()

    fun buildData() {
        val xmlhttp = XMLHttpRequest()
        xmlhttp.open("GET", "opleiderresultaten-01072017-tm-30062018.csv", false)

        xmlhttp.send()
        val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null

        val csv = result
            ?.split('\n')
            ?.map { it.split(';') }

        if (csv == null) {
            println("Reading data failed!!")
        } else {
//            val headers = csv.first()
            val data = csv.drop(1)
            for (line in data) {
                alleOpleiders.getOrPut(line[0]) {
                    Opleider(
                        code = line[0],
                        naam = line[1],
                        startdatum = line[2].split('-').let {
                            Date(
                                day = it[0].toInt(),
                                month = it[1].toInt(),
                                year = it[2].toInt(),
                                hour = 0,
                                minute = 0,
                                second = 0,
                                millisecond = 0
                            )
                        },
                        einddatum = line[3].split('-').let {
                            Date(
                                day = it[0].toInt(),
                                month = it[1].toInt(),
                                year = it[2].toInt(),
                                hour = 0,
                                minute = 0,
                                second = 0,
                                millisecond = 0
                            )
                        },
                        straatnaam = line[4],
                        huisnummer = line[5],
                        huisnummerToevoeging = line[6],
                        postcode = line[7],
                        plaatsnaam = line[8]
                    )
                }

                alleExamenLocaties.getOrPut(line[13]) {
                    Examenlocatie(
                        naam = line[13],
                        straatnaam = line[14],
                        huisnummer = line[15],
                        huisnummerToevoeging = line[16],
                        postcode = line[17],
                        plaatsnaam = line[18]
                    )
                }
            }
        }
    }

}

// No ram can handle this
//for ((i, line) in data.withIndex()) {
//    alleResultaten.add(
//        Resultaat(
//            id = i,
//            opleider = Data.alleOpleiders.getOrPut(line[0]) {
//                Opleider(
//                    code = line[0],
//                    naam = line[1],
//                    startdatum = line[2].split('-').let {
//                        Date(
//                            day = it[0].toInt(),
//                            month = it[1].toInt(),
//                            year = it[2].toInt(),
//                            hour = 0,
//                            minute = 0,
//                            second = 0,
//                            millisecond = 0
//                        )
//                    },
//                    einddatum = line[3].split('-').let {
//                        Date(
//                            day = it[0].toInt(),
//                            month = it[1].toInt(),
//                            year = it[2].toInt(),
//                            hour = 0,
//                            minute = 0,
//                            second = 0,
//                            millisecond = 0
//                        )
//                    },
//                    straatnaam = line[4],
//                    huisnummer = line[5],
//                    huisnummerToevoeging = line[6],
//                    postcode = line[7],
//                    plaatsnaam = line[8]
//                )
//            },
//            product = Product.valueOf(
//                line[11].replace('-', '_')
//            ),
//            examenlocatie = Data.alleExamenLocaties.getOrPut(line[13]) {
//                Examenlocatie(
//                    naam = line[13],
//                    straatnaam = line[14],
//                    huisnummer = line[15],
//                    huisnummerToevoeging = line[16],
//                    postcode = line[17],
//                    plaatsnaam = line[18]
//                )
//            },
//            examenResultaatAantallen = listOf(
//                ExamenResultaatAantal(
//                    examenResultaatVersie = EERSTE_EXAMEN_OF_TOETS,
//                    examenResultaatCategorie = AUTOMAAT,
//                    examenResultaat = VOLDOENDE,
//                    aantal = line[23].toInt()
//                ),
//                ExamenResultaatAantal(
//                    examenResultaatVersie = EERSTE_EXAMEN_OF_TOETS,
//                    examenResultaatCategorie = AUTOMAAT,
//                    examenResultaat = ONVOLDOENDE,
//                    aantal = line[24].toInt()
//                ),
//                ExamenResultaatAantal(
//                    examenResultaatVersie = EERSTE_EXAMEN_OF_TOETS,
//                    examenResultaatCategorie = COMBI,
//                    examenResultaat = VOLDOENDE,
//                    aantal = line[25].toInt()
//                ),
//                ExamenResultaatAantal(
//                    examenResultaatVersie = EERSTE_EXAMEN_OF_TOETS,
//                    examenResultaatCategorie = COMBI,
//                    examenResultaat = ONVOLDOENDE,
//                    aantal = line[26].toInt()
//                ),
//                ExamenResultaatAantal(
//                    examenResultaatVersie = EERSTE_EXAMEN_OF_TOETS,
//                    examenResultaatCategorie = HANDGESCHAKELD,
//                    examenResultaat = VOLDOENDE,
//                    aantal = line[27].toInt()
//                ),
//                ExamenResultaatAantal(
//                    examenResultaatVersie = EERSTE_EXAMEN_OF_TOETS,
//                    examenResultaatCategorie = HANDGESCHAKELD,
//                    examenResultaat = ONVOLDOENDE,
//                    aantal = line[28].toInt()
//                ),
//                ExamenResultaatAantal(
//                    examenResultaatVersie = HEREXAMEN_OF_TOETS,
//                    examenResultaatCategorie = AUTOMAAT,
//                    examenResultaat = VOLDOENDE,
//                    aantal = line[31].toInt()
//                ),
//                ExamenResultaatAantal(
//                    examenResultaatVersie = HEREXAMEN_OF_TOETS,
//                    examenResultaatCategorie = AUTOMAAT,
//                    examenResultaat = ONVOLDOENDE,
//                    aantal = line[32].toInt()
//                ),
//                ExamenResultaatAantal(
//                    examenResultaatVersie = HEREXAMEN_OF_TOETS,
//                    examenResultaatCategorie = COMBI,
//                    examenResultaat = VOLDOENDE,
//                    aantal = line[33].toInt()
//                ),
//                ExamenResultaatAantal(
//                    examenResultaatVersie = HEREXAMEN_OF_TOETS,
//                    examenResultaatCategorie = COMBI,
//                    examenResultaat = ONVOLDOENDE,
//                    aantal = line[34].toInt()
//                ),
//                ExamenResultaatAantal(
//                    examenResultaatVersie = HEREXAMEN_OF_TOETS,
//                    examenResultaatCategorie = HANDGESCHAKELD,
//                    examenResultaat = VOLDOENDE,
//                    aantal = line[35].toInt()
//                ),
//                ExamenResultaatAantal(
//                    examenResultaatVersie = HEREXAMEN_OF_TOETS,
//                    examenResultaatCategorie = HANDGESCHAKELD,
//                    examenResultaat = ONVOLDOENDE,
//                    aantal = line[36].toInt()
//                )
//            )
//        )
//    )
//}