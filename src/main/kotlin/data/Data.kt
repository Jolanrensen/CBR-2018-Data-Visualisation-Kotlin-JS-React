package data

import org.w3c.xhr.XMLHttpRequest

object Data {

    var hasStartedLoading = false
    val NO_PRODUCTEN = 103
    fun Int.isAllOrNoProducten() = this == 0 || this == NO_PRODUCTEN

    interface GemeentesProperties {
        val statcode: String
        val jrstatcode: String
        val statnaam: String
        val rubriek: String
        val FID: String
    }

    fun saveOpleiderToExamenlocaties() {
        var allResults = ""
        for ((key, values) in opleiderToExamenlocaties) {
            allResults += "$key;${values.joinToString(";")}\n"
        }
        println(allResults)
    }

    fun saveExamenlocatieToOpleiders() {
        var allResults = ""
        for ((key, values) in examenlocatieToOpleiders) {
            allResults += "$key;${values.joinToString(";")}\n"
        }
        println(allResults)
    }

    fun saveOpleiderToResultaten() {
        var allResults = ""
        for ((key, values) in opleiderToResultaten) {
            allResults += "$key;${values.joinToString(";")}\n"
        }
        println(allResults)
    }

    fun saveExamenlocatieToResultaten() {
        var allResults = ""
        for ((key, values) in examenlocatieToResultaten) {
            allResults += "$key;${values.joinToString(";")}\n"
        }
        println(allResults)
    }

    fun saveAlleExamenlocaties() {
        var allExamenlocaties = ""
        for ((_, examenlocatie) in alleExamenlocaties) {
            allExamenlocaties += examenlocatie.data.joinToString(";") + "\n"
        }
        println(allExamenlocaties)
    }

    fun saveAlleOpleiders() {
        var allOpleiders = ""
        for ((_, opleider) in alleOpleiders) {
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

    @OptIn(ExperimentalStdlibApi::class)
    fun saveProductenToOpleiders() {
        val productToOpleiders = buildMap<Product, HashSet<String>> {
            Product.values().forEach {
                this[it] = hashSetOf()
            }
        }

        for ((key, _) in alleOpleiders) {
            opleiderToResultaten[key]!!
                .map { alleResultaten[it]!! }
                .forEach {
                    productToOpleiders[it.product]!!.add(key)
                }
        }

        var result = ""
        for ((product, opleiders) in productToOpleiders) {
            result += "${product.name};${opleiders.joinToString(";")}\n"
        }
        println(result)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun saveProductenToExamenlocaties() {
        val productToExamenlocaties = buildMap<Product, HashSet<String>> {
            Product.values().forEach {
                this[it] = hashSetOf()
            }
        }

        for ((key, _) in alleExamenlocaties) {
            examenlocatieToResultaten[key]!!
                .map { alleResultaten[it]!! }
                .forEach {
                    productToExamenlocaties[it.product]!!.add(key)
                }
        }

        var result = ""
        for ((product, examenlocaties) in productToExamenlocaties) {
            result += "${product.name};${examenlocaties.joinToString(";")}\n"
        }
        println(result)
    }

    fun loadData() {
        alleExamenlocaties
        alleOpleiders
        alleResultaten
        opleiderToExamenlocaties
        examenlocatieToOpleiders
        opleiderToResultaten
        examenlocatieToResultaten
        productToOpleiders
        productToExamenlocaties
        geoJson
    }

    private var _productToExamenlocaties: Map<Product, Set<String>>? = null
    val productToExamenlocaties: Map<Product, Set<String>>
        get() {
            if (_productToExamenlocaties != null) return _productToExamenlocaties!!
            val xmlhttp = XMLHttpRequest()
            xmlhttp.open("GET", "productToExamenlocaties", false)
            xmlhttp.send()

            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null
            _productToExamenlocaties = result
                ?.split("\n")
                ?.map { it.split(";") }
                ?.map {
                    Product.valueOf(it.first()) to
                            it.takeLast(it.size - 1)
                                .toSet()
                }
                ?.toMap()

            return _productToExamenlocaties!!
        }

    private var _productToOpleiders: Map<Product, Set<String>>? = null
    val productToOpleiders: Map<Product, Set<String>>
        get() {
            if (_productToOpleiders != null) return _productToOpleiders!!
            val xmlhttp = XMLHttpRequest()
            xmlhttp.open("GET", "productToOpleiders", false)
            xmlhttp.send()

            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null
            _productToOpleiders = result
                ?.split("\n")
                ?.map { it.split(";") }
                ?.map {
                    Product.valueOf(it.first()) to
                            it.takeLast(it.size - 1)
                                .toSet()
                }
                ?.toMap()

            return _productToOpleiders!!
        }

    private var _opleiderToExamenlocaties: Map<String, Set<String>>? = null
    val opleiderToExamenlocaties: Map<String, Set<String>>
        get() {
            if (_opleiderToExamenlocaties != null) return _opleiderToExamenlocaties!!
            val xmlhttp = XMLHttpRequest()
            xmlhttp.open("GET", "opleiderToExamenlocaties", false)
            xmlhttp.send()

            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null
            _opleiderToExamenlocaties = result
                ?.split("\n")
                ?.map {
                    it.split(";").let {
                        it.first() to
                                it.takeLast(it.size - 1)
                                    .toSet()
                    }
                }
                ?.toMap()

            return _opleiderToExamenlocaties!!
        }


    private var _examenlocatieToOpleiders: Map<String, Set<String>>? = null
    val examenlocatieToOpleiders: Map<String, Set<String>>
        get() {
            if (_examenlocatieToOpleiders != null) return _examenlocatieToOpleiders!!
            val xmlhttp = XMLHttpRequest()
            xmlhttp.open("GET", "examenlocatieToOpleiders", false)
            xmlhttp.send()

            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null
            _examenlocatieToOpleiders = result
                ?.split("\n")
                ?.map {
                    it.split(";").let {
                        it.first() to
                                it.takeLast(it.size - 1)
                                    .toSet()
                    }
                }
                ?.toMap()

            return _examenlocatieToOpleiders!!
        }


    private var _opleiderToResultaten: Map<String, Set<Int>>? = null
    val opleiderToResultaten: Map<String, Set<Int>>
        get() {
            if (_opleiderToResultaten != null) return _opleiderToResultaten!!
            val xmlhttp = XMLHttpRequest()
            xmlhttp.open("GET", "opleiderToResultaten", false)
            xmlhttp.send()

            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null
            _opleiderToResultaten = result
                ?.split("\n")
                ?.map {
                    it.split(";").let {
                        it.first() to
                                it.takeLast(it.size - 1)
                                    .map { it.toInt() }
                                    .toSet()
                    }
                }
                ?.toMap()

            return _opleiderToResultaten!!
        }

    private var _examenlocatieToResultaten: Map<String, Set<Int>>? = null
    val examenlocatieToResultaten: Map<String, Set<Int>>
        get() {
            if (_examenlocatieToResultaten != null) return _examenlocatieToResultaten!!
            val xmlhttp = XMLHttpRequest()
            xmlhttp.open("GET", "examenlocatieToResultaten", false)
            xmlhttp.send()

            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null
            _examenlocatieToResultaten = result
                ?.split("\n")
                ?.map {
                    it.split(";").let {
                        it.first() to
                                it.takeLast(it.size - 1)
                                    .map { it.toInt() }
                                    .toSet()
                    }
                }
                ?.toMap()


            return _examenlocatieToResultaten!!
        }

    val NO_EXAMENLOCATIES = 327
    fun Int.isAllOrNoExamenlocaties() = this == 0 || this == NO_EXAMENLOCATIES

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

    val NO_OPLEIDERS = 7501
    fun Int.isAllOrNoOpleiders() = this == 0 || this == NO_OPLEIDERS

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

    val NO_RESULTATEN = 76583

    private var _alleResultaten: Map<Int, Resultaat>? = null

    @OptIn(ExperimentalStdlibApi::class)
    val alleResultaten: Map<Int, Resultaat>
        get() {
            if (_alleResultaten != null) return _alleResultaten!!
            val xmlhttp = XMLHttpRequest()
            xmlhttp.open("GET", "alleResultaten", false)
            xmlhttp.send()

            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null

            @Suppress("RemoveExplicitTypeArguments")
            _alleResultaten = buildMap<Int, Resultaat> {
                result
                    ?.split("\n")
                    ?.forEach {
                        it.split(",").also {
                            this[it[0].toInt()] = Resultaat(arrayOf(
                                it[0],
                                it[1],
                                it[2].split(";")
                                    .map {
                                        ExamenresultaatAantal(
                                            it.split("|")
                                                .map { it.toInt() }
                                                .toTypedArray()
                                        )
                                    }
                                    .toTypedArray()
                            ))
                        }
                    }
            }

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
}




