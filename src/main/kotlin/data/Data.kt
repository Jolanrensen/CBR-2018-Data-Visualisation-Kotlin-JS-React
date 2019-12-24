package data

import data.ExamenResultaat.*
import data.ExamenResultaatCategorie.*
import data.ExamenResultaatVersie.*
import io.data2viz.time.Date
import org.w3c.xhr.XMLHttpRequest

object Data {
    val alleOpleiders = hashMapOf<String, Opleider>()
    val alleExamenlocaties = hashMapOf<String, Examenlocatie>()

    val opleiderToExamenlocaties: HashMap<String, HashSet<String>> = hashMapOf()
    val examenlocatieToOpleiders: HashMap<String, HashSet<String>> = hashMapOf()

    private val csv: List<List<String>>?
        get() {
            val xmlhttp = XMLHttpRequest()
            xmlhttp.open("GET", "opleiderresultaten-01072017-tm-30062018.csv", false)

            xmlhttp.send()
            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null

            return result
                ?.split('\n')
                ?.map { it.split(';') }
        }

    fun getResults(
        opleiders: Collection<Opleider> = listOf(),
        examenlocaties: Collection<Examenlocatie> = listOf()
    ) = getResults(opleiders.map { it.code }, examenlocaties.map { it.naam })

    fun getResults(
        opleiders: Collection<String> = listOf(),
        examenlocaties: Collection<String> = listOf()
    ): List<Resultaat> {
        csv?.let {
            val data = it.drop(1).filter { it[0] in opleiders || it[13] in examenlocaties }
            val results = arrayListOf<Resultaat>()
            for ((i, line) in data.withIndex()) {
                results.add(
                    Resultaat(
                        id = i,
                        opleider = alleOpleiders[line[0]]!!,
                        product = Product.valueOf(
                            line[11].replace('-', '_')
                        ),
                        examenlocatie = alleExamenlocaties[line[13]]!!,
                        examenResultaatAantallen = listOf(
                            ExamenResultaatAantal(
                                examenResultaatVersie = EERSTE_EXAMEN_OF_TOETS,
                                examenResultaatCategorie = AUTOMAAT,
                                examenResultaat = VOLDOENDE,
                                aantal = line[23].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = EERSTE_EXAMEN_OF_TOETS,
                                examenResultaatCategorie = AUTOMAAT,
                                examenResultaat = ONVOLDOENDE,
                                aantal = line[24].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = EERSTE_EXAMEN_OF_TOETS,
                                examenResultaatCategorie = COMBI,
                                examenResultaat = VOLDOENDE,
                                aantal = line[25].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = EERSTE_EXAMEN_OF_TOETS,
                                examenResultaatCategorie = COMBI,
                                examenResultaat = ONVOLDOENDE,
                                aantal = line[26].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = EERSTE_EXAMEN_OF_TOETS,
                                examenResultaatCategorie = HANDGESCHAKELD,
                                examenResultaat = VOLDOENDE,
                                aantal = line[27].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = EERSTE_EXAMEN_OF_TOETS,
                                examenResultaatCategorie = HANDGESCHAKELD,
                                examenResultaat = ONVOLDOENDE,
                                aantal = line[28].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = HEREXAMEN_OF_TOETS,
                                examenResultaatCategorie = AUTOMAAT,
                                examenResultaat = VOLDOENDE,
                                aantal = line[31].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = HEREXAMEN_OF_TOETS,
                                examenResultaatCategorie = AUTOMAAT,
                                examenResultaat = ONVOLDOENDE,
                                aantal = line[32].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = HEREXAMEN_OF_TOETS,
                                examenResultaatCategorie = COMBI,
                                examenResultaat = VOLDOENDE,
                                aantal = line[33].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = HEREXAMEN_OF_TOETS,
                                examenResultaatCategorie = COMBI,
                                examenResultaat = ONVOLDOENDE,
                                aantal = line[34].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = HEREXAMEN_OF_TOETS,
                                examenResultaatCategorie = HANDGESCHAKELD,
                                examenResultaat = VOLDOENDE,
                                aantal = line[35].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = HEREXAMEN_OF_TOETS,
                                examenResultaatCategorie = HANDGESCHAKELD,
                                examenResultaat = ONVOLDOENDE,
                                aantal = line[36].toInt()
                            )
                        )
                    )
                )
            }
            return results
        } ?: throw IllegalArgumentException("Couldn't read data")
    }

    fun buildData() {
        csv?.let {
            val data = it.drop(1)
            for (line in data) {
                val opleider = alleOpleiders.getOrPut(line[0]) {
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

                val examenlocatie = alleExamenlocaties.getOrPut(line[13]) {
                    Examenlocatie(
                        naam = line[13],
                        straatnaam = line[14],
                        huisnummer = line[15],
                        huisnummerToevoeging = line[16],
                        postcode = line[17],
                        plaatsnaam = line[18]
                    )
                }

                opleiderToExamenlocaties.apply {
                    get(opleider.code)?.add(examenlocatie.naam) ?: set(opleider.code, hashSetOf(examenlocatie.naam))
                }
                examenlocatieToOpleiders.apply {
                    get(examenlocatie.naam)?.add(opleider.code) ?: set(examenlocatie.naam, hashSetOf(opleider.code))
                }
            }
        } ?: throw IllegalArgumentException("Couldn't read data")
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