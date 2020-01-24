package data

import data.ExamenResultaat.ONVOLDOENDE
import data.ExamenResultaat.VOLDOENDE
import data.ExamenResultaatCategorie.*
import data.ExamenResultaatVersie.EERSTE_EXAMEN_OF_TOETS
import data.ExamenResultaatVersie.HEREXAMEN_OF_TOETS
import fromJsonToMap
import kotlinx.serialization.PrimitiveKind
import libs.JsObject
import libs.*
import libs.jsObjectOf
import libs.kworker.JobDescriptor
import libs.kworker.Jobs
import libs.kworker.JobsMainSync
import libs.set
import org.khronos.webgl.ArrayBuffer
import org.w3c.xhr.XMLHttpRequest
import toJsonString


object Data {

    var hasStartedLoading = false

    var alleOpleiders: JsObject<Opleider> = jsObjectOf()
    var alleExamenlocaties: JsObject<Examenlocatie> = jsObjectOf()

    var opleiderToExamenlocaties: JsObject<Array<String>> = jsObjectOf()
    var examenlocatieToOpleiders: JsObject<Array<String>> = jsObjectOf()

    var opleiderToResultaten: JsObject<Array<String>> = jsObjectOf()
    var examenlocatieToResultaten: JsObject<Array<String>> = jsObjectOf()

    var alleResultaten: JsObject<Resultaat> = jsObjectOf()

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
            // TODO don't hardcode localhost lol
            xmlhttp.open("GET", "http://localhost:8080/gemeente_2018.geojson", false)

            xmlhttp.send()
            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null

            field = result?.toFeatureCollection()
            return field
        }

    object BuildAllDataJob : JobDescriptor {
        private fun getCsv(): List<List<String>>? {
            val xmlhttp = XMLHttpRequest()
            // data from overheid cbr, gemeentes toegevoegd met https://www.cbs.nl/nl-nl/maatwerk/2018/36/buurt-wijk-en-gemeente-2018-voor-postcode-huisnummer
            // TODO don't hardcode localhost lol
            xmlhttp.open("GET", "http://localhost:8080/opleiderresultaten-met-gemeentes.csv", false)
            xmlhttp.send()
            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null

            return result
                ?.split('\n')
                ?.map { it.split(';') }
        }

        override suspend fun execute(args: Array<Any?>): Array<Any?> {
            println("started loading csv in the background")

            val alleOpleiders: JsObject<Opleider> = jsObjectOf()
            val alleExamenlocaties: JsObject<Examenlocatie> = jsObjectOf()
            val opleiderToExamenlocaties: JsObject<Array<String>> = jsObjectOf()
            val examenlocatieToOpleiders: JsObject<Array<String>> = jsObjectOf()

            val opleiderToResultaten: JsObject<Array<String>> = jsObjectOf()
            val examenlocatieToResultaten: JsObject<Array<String>> = jsObjectOf()

            val alleResultaten: JsObject<Resultaat> = jsObjectOf()

            getCsv()?.let {
                val data = it.drop(1)

                for ((i, line) in data.withIndex()) {
                    try {
                        val opleider = alleOpleiders.getOrPut(line[0]) {
                            Opleider(
                                code = line[0],
                                naam = line[1],
                                _startdatum = line[2].split('-').let {
                                    kotlin.js.Date(
                                        day = it[0].toInt(),
                                        month = it[1].toInt(),
                                        year = it[2].toInt(),
                                        hour = 0,
                                        minute = 0,
                                        second = 0,
                                        millisecond = 0
                                    ).toDateString()
                                },
                                _einddatum = line[3].split('-').let {
                                    kotlin.js.Date(
                                        day = it[0].toInt(),
                                        month = it[1].toInt(),
                                        year = it[2].toInt(),
                                        hour = 0,
                                        minute = 0,
                                        second = 0,
                                        millisecond = 0
                                    ).toDateString()
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

                        val resultaat = getResult(i.toString(), line, alleOpleiders, alleExamenlocaties)

                        alleResultaten[resultaat.id] = resultaat

                        opleiderToExamenlocaties.getOrPut(opleider.code) { arrayOf() }
                            .push(examenlocatie.naam)
                        examenlocatieToOpleiders.getOrPut(examenlocatie.naam) { arrayOf() }
                            .push(opleider.code)

                        opleiderToResultaten.getOrPut(opleider.code) { arrayOf() }
                            .push(resultaat.id)
                        examenlocatieToResultaten.getOrPut(examenlocatie.naam) { arrayOf() }
                            .push(resultaat.id)

                    } catch (e: Exception) {
                    }
                }
            }

            return arrayOf(
                alleOpleiders,
                alleExamenlocaties,
                opleiderToExamenlocaties,
                examenlocatieToOpleiders,
                opleiderToResultaten,
                examenlocatieToResultaten,
                alleResultaten
            )
        }
    }


    @Suppress("UNCHECKED_CAST", "UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    fun buildAllData(callback: () -> Unit) {
        hasStartedLoading = true


        println("started loading csv in the foreground 1")
        JobsMainSync({
            //register(BuildAllDataJob)
        }, {
            println("started loading csv in the foreground 2")

            Jobs().execute(BuildAllDataJob, arrayOf())
                .let {
                    alleOpleiders = it[0] as JsObject<Opleider>
                    alleExamenlocaties = it[1] as JsObject<Examenlocatie>
                    opleiderToExamenlocaties = it[2] as JsObject<Array<String>>
                    examenlocatieToOpleiders = it[3] as JsObject<Array<String>>
                    opleiderToResultaten = it[4] as JsObject<Array<String>>
                    examenlocatieToResultaten = it[5] as JsObject<Array<String>>
                    alleResultaten = it[6] as JsObject<Resultaat>
                }
            callback()
        })
    }

    internal fun getResult(
        id: String,
        line: List<String>,
        alleOpleiders: JsObject<Opleider>,
        alleExamenlocaties: JsObject<Examenlocatie>
    ) = Resultaat(
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




