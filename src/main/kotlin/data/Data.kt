package data

import data.Examenresultaat.ONVOLDOENDE
import data.Examenresultaat.VOLDOENDE
import data.ExamenresultaatCategorie.*
import data.ExamenresultaatVersie.EERSTE_EXAMEN_OF_TOETS
import data.ExamenresultaatVersie.HEREXAMEN_OF_TOETS
import org.w3c.xhr.XMLHttpRequest

object Data {

    var hasStartedLoading = false

    val opleiderToExamenlocaties: HashMap<String, HashSet<String>> = hashMapOf()
    val examenlocatieToOpleiders: HashMap<String, HashSet<String>> = hashMapOf()

    val opleiderToResultaten: HashMap<String, HashSet<Int>> = hashMapOf()
    val examenlocatieToResultaten: HashMap<String, HashSet<Int>> = hashMapOf()

//    var alleResultaten: HashMap<Int, Resultaat> = hashMapOf()

    interface GemeentesProperties {
        val statcode: String
        val jrstatcode: String
        val statnaam: String
        val rubriek: String
        val FID: String
    }

    fun saveAlleExamenlocaties() {
        var allExamenlocaties = ""
        for ((id, examenlocatie) in alleExamenlocaties) {
            allExamenlocaties += examenlocatie.data.joinToString(";") + "\n"
        }
        println(allExamenlocaties)
    }

    fun saveAlleOpleiders() {
        var allOpleiders = ""
        for ((id, opleider) in alleOpleiders) {
            allOpleiders += opleider.data.joinToString(";") + "\n"
        }
        println(allOpleiders)
    }

    fun saveAlleResultaten() {
        var allResults = ""
        for ((id, result) in alleResultaten) {
            allResults += "$id,${result.product.name},${
            result.examenresultaatAantallen.joinToString(";") {
                it.data.joinToString(
                    "|"
                ) { "$it" }
            }}\n"
        }
        println(allResults)
    }

    private var _alleExamenlocaties: Map<String, Examenlocatie>? = null
    val alleExamenlocaties: Map<String, Examenlocatie>
        get() {
            if (_alleExamenlocaties != null) return _alleExamenlocaties!!
            val xmlhttp = XMLHttpRequest()
            xmlhttp.open("GET", "alleExamenlocaties", false)
            xmlhttp.send()

            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null
            _alleExamenlocaties = result
                ?.split("\n")
                ?.map {
                    it.split(";").let {
                        it[0] to Examenlocatie(
                            data = it.toTypedArray()
                        )
                    }
                }
                ?.toMap()

            return _alleExamenlocaties!!
        }


    private var _alleOpleiders: Map<String, Opleider>? = null
    val alleOpleiders: Map<String, Opleider>
        get() {
            if (_alleOpleiders != null) return _alleOpleiders!!
            val xmlhttp = XMLHttpRequest()
            xmlhttp.open("GET", "alleOpleiders", false)
            xmlhttp.send()

            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null
            _alleOpleiders = result
                ?.split("\n")
                ?.map {
                    it.split(";").let {
                        it[0] to Opleider(
                            data = it.toTypedArray()
                        )
                    }
                }
                ?.toMap()

            return _alleOpleiders!!
        }

    private var _alleResultaten: Map<Int, Resultaat>? = null
    val alleResultaten: Map<Int, Resultaat>
        get() {
            if (_alleResultaten != null) return _alleResultaten!!
            val xmlhttp = XMLHttpRequest()
            xmlhttp.open("GET", "alleResultaten", false)
            xmlhttp.send()

            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null

            _alleResultaten = result
                ?.split("\n")
                ?.map {
                    it.split(",").let {
                        it[0].toInt() to Resultaat(
                            id = it[0].toInt(),
                            product = Product.valueOf(it[1]),
                            examenresultaatAantallen = it[2].split(";")
                                .map {
                                    ExamenresultaatAantal(
                                        data = it.split("|")
                                            .map { it.toInt() }
                                            .toTypedArray()
                                    )
                                }
                                .toTypedArray()
                        )
                    }
                }
                ?.toMap()

            return _alleResultaten!!
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

    val csv: Sequence<List<String>>?
        get() {
            val xmlhttp = XMLHttpRequest()
            // data from overheid cbr, https://data.overheid.nl/dataset/cbr-opleiderresultaten
            // gemeentes toegevoegd met https://www.cbs.nl/nl-nl/maatwerk/2018/36/buurt-wijk-en-gemeente-2018-voor-postcode-huisnummer
            xmlhttp.open("GET", "opleiderresultaten-met-gemeentes.csv", false)

            xmlhttp.send()
            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null

            return result
                ?.split('\n')
                ?.asSequence()
                ?.map { it.split(';') }
        }

    fun buildAllData() {
        hasStartedLoading = true
        csv?.let {
            val data = it.drop(1)

            for ((i, line) in data.withIndex()) {
                try {
                    val opleiderCode = line[0]
//                    val opleider = alleOpleiders.getOrPut(line[0]) {
//                        Opleider(
//                            code = line[0],
//                            naam = line[1],
//                            startdatum = line[2],
//                            einddatum = line[3],
//                            straatnaam = line[4],
//                            huisnummer = line[5],
//                            huisnummerToevoeging = line[6],
//                            postcode = line[7],
//                            plaatsnaam = line[8],
//                            gemeente = line[37]
//                        )
//                    }

                    val examenlocatieNaam = line[13]
//                        alleExamenlocaties.getOrPut(line[13]) {
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

//                    val resultaat = getResult(i, line)
                    val resultaatId = i

//                    alleResultaten[resultaatId] = resultaat

                    opleiderToExamenlocaties.getOrPut(opleiderCode, { hashSetOf() })
                        .add(examenlocatieNaam)
                    examenlocatieToOpleiders.getOrPut(examenlocatieNaam, { hashSetOf() })
                        .add(opleiderCode)

                    opleiderToResultaten.getOrPut(opleiderCode, { hashSetOf() })
                        .add(resultaatId)
                    examenlocatieToResultaten.getOrPut(examenlocatieNaam, { hashSetOf() })
                        .add(resultaatId)

                } catch (e: Exception) {
                }
            }

            opleiderToResultaten.forEach { (opleiderCode, resultaten) ->
                alleOpleiders[opleiderCode]?.apply {
                    slagingspercentageEersteKeer = (
                            resultaten.sumBy {
                                alleResultaten[it]!!.examenresultaatAantallen
                                    .asSequence()
                                    .voldoende
                                    .eersteExamen
                                    .sumBy { it.aantal }
                            }.toDouble() / resultaten.sumBy {
                                alleResultaten[it]!!.examenresultaatAantallen
                                    .asSequence()
                                    .eersteExamen
                                    .sumBy { it.aantal }
                            }.toDouble()
                            ).let { if (it.isNaN()) null else it }

                    slagingspercentageHerkansing = (
                            resultaten.sumBy {
                                alleResultaten[it]!!.examenresultaatAantallen
                                    .asSequence()
                                    .voldoende
                                    .herexamen
                                    .sumBy { it.aantal }
                            }.toDouble() / resultaten.sumBy {
                                alleResultaten[it]!!.examenresultaatAantallen
                                    .asSequence()
                                    .herexamen
                                    .sumBy { it.aantal }
                            }.toDouble()
                            ).let { if (it.isNaN()) null else it }
                }
            }

            examenlocatieToResultaten.forEach { (examenlocatieCode, resultaten) ->
                alleExamenlocaties[examenlocatieCode]?.apply {
                    slagingspercentageEersteKeer = (
                            resultaten.sumBy {
                                alleResultaten[it]!!.examenresultaatAantallen
                                    .asSequence()
                                    .voldoende
                                    .eersteExamen
                                    .sumBy { it.aantal }
                            }.toDouble() / resultaten.sumBy {
                                alleResultaten[it]!!.examenresultaatAantallen
                                    .asSequence()
                                    .eersteExamen
                                    .sumBy { it.aantal }
                            }.toDouble()
                            ).let { if (it.isNaN()) null else it }

                    slagingspercentageHerkansing = (
                            resultaten.sumBy {
                                alleResultaten[it]!!.examenresultaatAantallen
                                    .asSequence()
                                    .voldoende
                                    .herexamen
                                    .sumBy { it.aantal }
                            }.toDouble() / resultaten.sumBy {
                                alleResultaten[it]!!.examenresultaatAantallen
                                    .asSequence()
                                    .herexamen
                                    .sumBy { it.aantal }
                            }.toDouble()
                            ).let { if (it.isNaN()) null else it }
                }
            }
        }
    }

    private fun getResult(id: Int, line: List<String>) = Resultaat(
        id = id,
        product = Product.valueOf(
            line[11].replace('-', '_')
        ),
        examenresultaatAantallen = arrayOf(
            ExamenresultaatAantal(
                examenresultaatVersie = EERSTE_EXAMEN_OF_TOETS,
                examenresultaatCategorie = AUTOMAAT,
                examenresultaat = VOLDOENDE,
                aantal = line[23].toInt()
            ),
            ExamenresultaatAantal(
                examenresultaatVersie = EERSTE_EXAMEN_OF_TOETS,
                examenresultaatCategorie = AUTOMAAT,
                examenresultaat = ONVOLDOENDE,
                aantal = line[24].toInt()
            ),
            ExamenresultaatAantal(
                examenresultaatVersie = EERSTE_EXAMEN_OF_TOETS,
                examenresultaatCategorie = COMBI,
                examenresultaat = VOLDOENDE,
                aantal = line[25].toInt()
            ),
            ExamenresultaatAantal(
                examenresultaatVersie = EERSTE_EXAMEN_OF_TOETS,
                examenresultaatCategorie = COMBI,
                examenresultaat = ONVOLDOENDE,
                aantal = line[26].toInt()
            ),
            ExamenresultaatAantal(
                examenresultaatVersie = EERSTE_EXAMEN_OF_TOETS,
                examenresultaatCategorie = HANDGESCHAKELD,
                examenresultaat = VOLDOENDE,
                aantal = line[27].toInt()
            ),
            ExamenresultaatAantal(
                examenresultaatVersie = EERSTE_EXAMEN_OF_TOETS,
                examenresultaatCategorie = HANDGESCHAKELD,
                examenresultaat = ONVOLDOENDE,
                aantal = line[28].toInt()
            ),
            ExamenresultaatAantal(
                examenresultaatVersie = HEREXAMEN_OF_TOETS,
                examenresultaatCategorie = AUTOMAAT,
                examenresultaat = VOLDOENDE,
                aantal = line[31].toInt()
            ),
            ExamenresultaatAantal(
                examenresultaatVersie = HEREXAMEN_OF_TOETS,
                examenresultaatCategorie = AUTOMAAT,
                examenresultaat = ONVOLDOENDE,
                aantal = line[32].toInt()
            ),
            ExamenresultaatAantal(
                examenresultaatVersie = HEREXAMEN_OF_TOETS,
                examenresultaatCategorie = COMBI,
                examenresultaat = VOLDOENDE,
                aantal = line[33].toInt()
            ),
            ExamenresultaatAantal(
                examenresultaatVersie = HEREXAMEN_OF_TOETS,
                examenresultaatCategorie = COMBI,
                examenresultaat = ONVOLDOENDE,
                aantal = line[34].toInt()
            ),
            ExamenresultaatAantal(
                examenresultaatVersie = HEREXAMEN_OF_TOETS,
                examenresultaatCategorie = HANDGESCHAKELD,
                examenresultaat = VOLDOENDE,
                aantal = line[35].toInt()
            ),
            ExamenresultaatAantal(
                examenresultaatVersie = HEREXAMEN_OF_TOETS,
                examenresultaatCategorie = HANDGESCHAKELD,
                examenresultaat = ONVOLDOENDE,
                aantal = line[36].toInt()
            )
        )
    )
}




