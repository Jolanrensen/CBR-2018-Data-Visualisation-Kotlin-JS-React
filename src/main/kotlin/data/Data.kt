package data

import data.ExamenResultaat.ONVOLDOENDE
import data.ExamenResultaat.VOLDOENDE
import data.ExamenResultaatCategorie.*
import data.ExamenResultaatVersie.EERSTE_EXAMEN_OF_TOETS
import data.ExamenResultaatVersie.HEREXAMEN_OF_TOETS
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Date

object Data {

    var hasStartedLoading = false

    val alleOpleiders = hashMapOf<String, Opleider>()
    val alleExamenlocaties = hashMapOf<String, Examenlocatie>()

    val opleiderToExamenlocaties: HashMap<String, HashSet<String>> = hashMapOf()
    val examenlocatieToOpleiders: HashMap<String, HashSet<String>> = hashMapOf()

    val opleiderToResultaten: HashMap<String, HashSet<Resultaat>> = hashMapOf()
    val examenlocatieToResultaten: HashMap<String, HashSet<Resultaat>> = hashMapOf()

    private var alleResultaten: HashSet<Resultaat> = hashSetOf()

    interface GemeentesProperties {
        val statcode: String
        val jrstatcode: String
        val statnaam: String
        val rubriek: String
        val FID: String
    }

    /** geometry type is Polygon or MultiPolygon */
    var geoJson: FeatureCollection<GemeentesProperties>? = null
        get() {
            if (field != null) return field
            val xmlhttp = XMLHttpRequest()
            // data from https://cartomap.github.io/nl/
            xmlhttp.open("GET", "gemeente_2018.geojson", false)

            xmlhttp.send()
            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null

            field = result?.toFeatureCollection()
            return field
        }

    val csv: List<List<String>>?
        get() {
            val xmlhttp = XMLHttpRequest()
            // data from overheid cbr, gemeentes toegevoegd met https://www.cbs.nl/nl-nl/maatwerk/2018/36/buurt-wijk-en-gemeente-2018-voor-postcode-huisnummer
            xmlhttp.open("GET", "opleiderresultaten-met-gemeentes.csv", false)

            xmlhttp.send()
            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null

            return result
                ?.split('\n')
                ?.map { it.split(';') }
        }

    fun buildAllData() {
        hasStartedLoading = true
        csv?.let {
            val data = it.drop(1)

            for ((i, line) in data.withIndex()) {
                try {
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
                            plaatsnaam = line[8],
                            gemeente = line[37]
                        )
                    }

                    val examenlocatie = alleExamenlocaties.getOrPut(line[13]) {
                        Examenlocatie(
                            naam = line[13],
                            straatnaam = line[14],
                            huisnummer = line[15],
                            huisnummerToevoeging = line[16],
                            postcode = line[17],
                            plaatsnaam = line[18],
                            gemeente = line[38]
                        )
                    }

                    val resultaat = getResult(i, line)

                    alleResultaten.add(resultaat)

                    opleiderToExamenlocaties.getOrPut(opleider.code, { hashSetOf() })
                        .add(examenlocatie.naam)
                    examenlocatieToOpleiders.getOrPut(examenlocatie.naam, { hashSetOf() })
                        .add(opleider.code)

                    opleiderToResultaten.getOrPut(opleider.code, { hashSetOf() })
                        .add(resultaat)
                    examenlocatieToResultaten.getOrPut(examenlocatie.naam, { hashSetOf() })
                        .add(resultaat)

                } catch (e: Exception) {
                }
            }

//            alleOpleiders.forEach { (opleiderCode, opeider) ->
//                val resultaten = opleiderToResultaten[opleiderCode]!!
//
//            }

            opleiderToResultaten.forEach { (opleiderCode, resultaten) ->
                alleOpleiders[opleiderCode]?.apply {
                    slagingsPercentageEersteKeer = (
                            resultaten.sumBy {
                                it.examenResultaatAantallen
                                    .asSequence()
                                    .voldoende
                                    .eersteExamen
                                    .sumBy { it.aantal }
                            }.toDouble() / resultaten.sumBy {
                                it.examenResultaatAantallen
                                    .asSequence()
                                    .eersteExamen
                                    .sumBy { it.aantal }
                            }.toDouble()
                            ).let { if (it.isNaN()) null else it }

                    slagingsPercentageHerkansing = (
                            resultaten.sumBy {
                                it.examenResultaatAantallen
                                    .asSequence()
                                    .voldoende
                                    .herExamen
                                    .sumBy { it.aantal }
                            }.toDouble() / resultaten.sumBy {
                                it.examenResultaatAantallen
                                    .asSequence()
                                    .herExamen
                                    .sumBy { it.aantal }
                            }.toDouble()
                            ).let { if (it.isNaN()) null else it }
                }
            }

            examenlocatieToResultaten.forEach { (examenlocatieCode, resultaten) ->
                alleExamenlocaties[examenlocatieCode]?.apply {
                    slagingsPercentageEersteKeer = (
                            resultaten.sumBy {
                                it.examenResultaatAantallen
                                    .asSequence()
                                    .voldoende
                                    .eersteExamen
                                    .sumBy { it.aantal }
                            }.toDouble() / resultaten.sumBy {
                                it.examenResultaatAantallen
                                    .asSequence()
                                    .eersteExamen
                                    .sumBy { it.aantal }
                            }.toDouble()
                            ).let { if (it.isNaN()) null else it }

                    slagingsPercentageHerkansing = (
                            resultaten.sumBy {
                                it.examenResultaatAantallen
                                    .asSequence()
                                    .voldoende
                                    .herExamen
                                    .sumBy { it.aantal }
                            }.toDouble() / resultaten.sumBy {
                                it.examenResultaatAantallen
                                    .asSequence()
                                    .herExamen
                                    .sumBy { it.aantal }
                            }.toDouble()
                            ).let { if (it.isNaN()) null else it }
                }
            }
        }
    }


//    fun getResults(
//        opleiders: Collection<Opleider> = alleOpleiders.values,
//        examenlocaties: Collection<Examenlocatie> = alleExamenlocaties.values
//    ) = getResults(opleiders.map { it.code }, examenlocaties.map { it.naam })
//
//    fun getResults(
//        opleiders: Collection<String> = listOf(),
//        examenlocaties: Collection<String> = listOf()
//    ) = //        println("Getting results for $opleiders $examenlocaties")
//        if (opleiders.isEmpty() || examenlocaties.isEmpty()) {
//            sequenceOf()
//        } else {
//            csv?.let {
//                val data = it.drop(1).filter { it[0] in opleiders && it[13] in examenlocaties }
//                sequence {
//                    for ((i, line) in data.withIndex())
//                        yield(getResult(i, line))
//                }
//            } ?: throw IllegalArgumentException("Couldn't read data")
//        }

    private fun getResult(id: Int, line: List<String>) = Resultaat(
        id = id,
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

    // builds dataset for opleiders and examenlocaties
//    fun getOpleidersAndExamenlocaties(): Pair<Map<String, Opleider>, Map<String, Examenlocatie>> {
//        if (alleOpleiders.isNotEmpty() && alleExamenlocaties.isNotEmpty())
//            return alleOpleiders to alleExamenlocaties
//        csv?.let {
//            val data = it.drop(1)
//            for (line in data) {
//                try {
//                    val opleider = alleOpleiders.getOrPut(line[0]) {
//                        Opleider(
//                            code = line[0],
//                            naam = line[1],
//                            startdatum = line[2].split('-').let {
//                                Date(
//                                    day = it[0].toInt(),
//                                    month = it[1].toInt(),
//                                    year = it[2].toInt(),
//                                    hour = 0,
//                                    minute = 0,
//                                    second = 0,
//                                    millisecond = 0
//                                )
//                            },
//                            einddatum = line[3].split('-').let {
//                                Date(
//                                    day = it[0].toInt(),
//                                    month = it[1].toInt(),
//                                    year = it[2].toInt(),
//                                    hour = 0,
//                                    minute = 0,
//                                    second = 0,
//                                    millisecond = 0
//                                )
//                            },
//                            straatnaam = line[4],
//                            huisnummer = line[5],
//                            huisnummerToevoeging = line[6],
//                            postcode = line[7],
//                            plaatsnaam = line[8],
//                            gemeente = line[37]
//                        )
//                    }
//
//                    val examenlocatie = alleExamenlocaties.getOrPut(line[13]) {
//                        Examenlocatie(
//                            naam = line[13],
//                            straatnaam = line[14],
//                            huisnummer = line[15],
//                            huisnummerToevoeging = line[16],
//                            postcode = line[17],
//                            plaatsnaam = line[18],
//                            gemeente = line[38]
//                        )
//                    }
//
//
//                    opleiderToExamenlocaties.apply {
//                        get(opleider.code)?.add(examenlocatie.naam) ?: set(opleider.code, hashSetOf(examenlocatie.naam))
//                    }
//                    examenlocatieToOpleiders.apply {
//                        get(examenlocatie.naam)?.add(opleider.code) ?: set(examenlocatie.naam, hashSetOf(opleider.code))
//                    }
//                } catch (e: Exception) {
//                    // console.error(line, e)
//                }
//            }
//        } ?: throw IllegalArgumentException("Couldn't read data")
//        return alleOpleiders to alleExamenlocaties
//    }
}




