package map

import DeselectAll
import ExamenlocatieOrOpleider
import ExamenlocatieOrOpleider.EXAMENLOCATIE
import ExamenlocatieOrOpleider.OPLEIDER
import Loading
import Loading.*
import SchakelSoort
import SelectAll
import SlagingspercentageSoort
import delegates.ReactPropAndStateDelegates.StateAsProp
import delegates.ReactPropAndStateDelegates.propDelegateOf
import delegates.ReactPropAndStateDelegates.stateDelegateOf
import SlagingspercentageSoort.*
import com.ccfraser.muirwik.components.mCircularProgress
import data.*
import data.Data.NO_EXAMENLOCATIES
import data.Data.NO_OPLEIDERS
import data.Data.NO_PRODUCTEN
import data.Data.isAllOrNoExamenlocaties
import data.Data.isAllOrNoOpleiders
import data.Data.isAllOrNoProducten
import data.Examenresultaat.ONVOLDOENDE
import data.Examenresultaat.VOLDOENDE
import data.ExamenresultaatVersie.EERSTE_EXAMEN_OF_TOETS
import data.ExamenresultaatVersie.HEREXAMEN_OF_TOETS
import data2viz.GeoPathNode
import data2viz.vizComponent
import forEachApply
import io.data2viz.color.Color
import io.data2viz.color.Colors
import io.data2viz.color.HslColor
import io.data2viz.geo.projection.conicEqualAreaProjection
import io.data2viz.geom.Point
import io.data2viz.geom.size
import io.data2viz.math.Angle
import io.data2viz.math.deg
import io.data2viz.math.pct
import io.data2viz.viz.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.xhr.XMLHttpRequest
import react.*
import styled.css
import styled.styledDiv
import kotlin.random.Random

interface NederlandVizMapProps : RProps {
    var selectedGemeente: StateAsProp<Gemeente?>
    var dataLoaded: Boolean
    var examenlocatieOrOpleider: ExamenlocatieOrOpleider
    var slagingspercentageSoort: SlagingspercentageSoort
    var schakelSoort: SchakelSoort


//    var setOpleiderFilters: ApplyFilter
//    var setExamenlocatieFilters: ApplyFilter

    var selectAllOpleiders: SelectAll<Opleider>
    var selectAllExamenlocaties: SelectAll<Examenlocatie>

    var deselectAllOpleiders: DeselectAll
    var deselectAllExamenlocaties: DeselectAll

    var selectedOpleiderKeys: StateAsProp<Set<String>>
    var selectedExamenlocatieKeys: StateAsProp<Set<String>>
    var selectedProducts: StateAsProp<Set<Product>>

    var filteredOpleiders: List<Opleider>
    var filteredExamenlocaties: List<Examenlocatie>
    var filteredProducts: List<Product>
}

interface NederlandVizMapState : RState {
    var gemeentes: Map<String, Gemeente>
    var loadingState: Loading
}

class NederlandVizMap(prps: NederlandVizMapProps) : RComponent<NederlandVizMapProps, NederlandVizMapState>(prps) {

    var selectedGemeenteState by propDelegateOf(NederlandVizMapProps::selectedGemeente)
    val examenlocatieOrOpleider by propDelegateOf(NederlandVizMapProps::examenlocatieOrOpleider)
    val slagingsPercentageSoort by propDelegateOf(NederlandVizMapProps::slagingspercentageSoort)
    val schakelSoort by propDelegateOf(NederlandVizMapProps::schakelSoort)

    var selectedGemeente: Gemeente? = selectedGemeenteState
        set(value) {
            field = value
            selectedGemeenteState = value
        }

    val dataLoaded by propDelegateOf(NederlandVizMapProps::dataLoaded)
//    val setOpleiderFilters by propDelegateOf(NederlandVizMapProps::setOpleiderFilters)
//    val setExamenlocatieFilters by propDelegateOf(NederlandVizMapProps::setExamenlocatieFilters)

    val selectAllOpleiders by propDelegateOf(NederlandVizMapProps::selectAllOpleiders)
    val selectAllExamenlocaties by propDelegateOf(NederlandVizMapProps::selectAllExamenlocaties)

    val deselectAllOpleiders by propDelegateOf(NederlandVizMapProps::deselectAllOpleiders)
    val deselectAllExamenlocaties by propDelegateOf(NederlandVizMapProps::deselectAllExamenlocaties)

    private val selectedOpleiderKeys by propDelegateOf(NederlandVizMapProps::selectedOpleiderKeys)
    private val selectedExamenlocatieKeys by propDelegateOf(NederlandVizMapProps::selectedExamenlocatieKeys)
    private val selectedProducts by propDelegateOf(NederlandVizMapProps::selectedProducts)

    private val filteredOpleiders by propDelegateOf(NederlandVizMapProps::filteredOpleiders)
    private val filteredExamenlocaties by propDelegateOf(NederlandVizMapProps::filteredExamenlocaties)
    private val filteredProducts by propDelegateOf(NederlandVizMapProps::filteredProducts)

    override fun NederlandVizMapState.init(props: NederlandVizMapProps) {
        gemeentes = emptyMap()
        loadingState = NOT_LOADED
    }

    var gemeentes by stateDelegateOf(NederlandVizMapState::gemeentes)
    var loadingState by stateDelegateOf(NederlandVizMapState::loadingState)

    // don't update when [selectedGemeente] changed (because that will loop)
    @Suppress("SimplifyBooleanWithConstants")
    override fun shouldComponentUpdate(nextProps: NederlandVizMapProps, nextState: NederlandVizMapState) = false
            || props.dataLoaded != nextProps.dataLoaded
            || props.examenlocatieOrOpleider != nextProps.examenlocatieOrOpleider
            || props.slagingspercentageSoort != nextProps.slagingspercentageSoort
            || props.schakelSoort != nextProps.schakelSoort
            || props.selectedOpleiderKeys != nextProps.selectedOpleiderKeys
            || props.selectedExamenlocatieKeys != nextProps.selectedExamenlocatieKeys
            || props.selectedProducts != nextProps.selectedProducts
            || props.filteredOpleiders != nextProps.filteredOpleiders
            || props.filteredExamenlocaties != nextProps.filteredExamenlocaties
            || props.filteredProducts != nextProps.filteredProducts
            || state.gemeentes != nextState.gemeentes
            || state.loadingState != nextState.loadingState

    private val nederland: FeatureCollection<Data.GemeentesProperties> =
        Data.geoJson!! // geometry type is polygon/multipolygon for each

    // String is a key in [gemeentes]
    private val idColorToGemeente = HashMap<Int, String>()

    @Suppress("DuplicatedCode")
    private fun calculateGemeentes() {
        if (gemeentes.isNotEmpty() || loadingState == LOADED) return

        GlobalScope.launch {
//            delay(500)
            println("calculating 'gemeentes'")

            // First get extra gemeenteData
            val xmlhttp = XMLHttpRequest()
            xmlhttp.open("GET", "gemeenteData", false)
            xmlhttp.send()

            val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null

            val gemeenteData = result!!
                .split("\n")
                .map { it.split(";") }
                .map { it.first() to it.takeLast(it.size - 1) }
                .toMap()


            gemeentes = nederland.features.map { feature ->
                val featureStatnaam = feature.properties.statnaam.toLowerCase()

                fun GeoPathNode.runOnNode() {
                    stroke = Colors.Web.darkgray
                    strokeWidth = 1.0
                    geoProjection = conicEqualAreaProjection {
                        scale = 15000.0
                        center(6.5.deg, 52.72.deg)
                    }
                    geoData = feature.toData2Viz()
                }

                val geoPathNode = GeoPathNode().apply {
                    runOnNode()
                }
                var idColor: Color?
                val hiddenGeoPathNode = GeoPathNode().apply {
                    runOnNode()
                    do {
                        idColor = Colors.rgb(
                            red = Random.nextInt(0, 255),
                            green = Random.nextInt(0, 255),
                            blue = Random.nextInt(0, 255)
                        )
                    } while (idColor!!.rgb in idColorToGemeente)
                    fill = idColor
                    strokeWidth = null
                }

                val currentGemeenteData = gemeenteData[featureStatnaam] ?: error("")

                featureStatnaam to Gemeente(
                    feature = feature,
                    opleiders = currentGemeenteData[0].split(",").toList().toSet(), //opleiders.map { it.code },
                    examenlocaties = currentGemeenteData[1].split(",").toSet(), //examenlocaties.map { it.naam },
                    geoPathNode = geoPathNode,
                    hiddenGeoPathNode = hiddenGeoPathNode,

                    /**
                     * Totaal percentage = sum[i in opleiders](i.slagingspecentage * aantal resultaten in i) / totaal aantal resultaten
                     */
                    slagingspercentageEersteKeerOpleiders = currentGemeenteData[2].toDouble()
                        .let { if (it.isNaN()) 0.0 else it },
                    slagingspercentageHerexamenOpleiders = 2.0 * currentGemeenteData[4].toDouble()
                        .let { if (it.isNaN()) 0.0 else it } - currentGemeenteData[2].toDouble()
                        .let { if (it.isNaN()) 0.0 else it }, /*currentGemeenteData[3].toDouble().let { if (it.isNaN()) 0.0 else it }*/ // recalculate
                    slagingspercentageGecombineerdOpleiders = currentGemeenteData[4].toDouble()
                        .let { if (it.isNaN()) 0.0 else it },
                    slagingspercentageEersteKeerExamenlocaties = currentGemeenteData[5].toDouble()
                        .let { if (it.isNaN()) 0.0 else it },
                    slagingspercentageHerexamenExamenlocaties = 2.0 * currentGemeenteData[7].toDouble()
                        .let { if (it.isNaN()) 0.0 else it } - currentGemeenteData[5].toDouble()
                        .let { if (it.isNaN()) 0.0 else it }, /*currentGemeenteData[6].toDouble().let { if (it.isNaN()) 0.0 else it }*/ // recalculate
                    slagingspercentageGecombineerdExamenlocaties = currentGemeenteData[7].toDouble()
                        .let { if (it.isNaN()) 0.0 else it }
                ).apply { idColorToGemeente[idColor!!.rgb] = featureStatnaam }
            }.toMap()
            loadingState = LOADED
        }
    }

    fun RBuilder.showLoading() {
        styledDiv {
            css {
                display = Display.flex
                justifyContent = JustifyContent.center
            }
            mCircularProgress()
        }
    }

    fun Viz.drawGemeentes() {
        val greenRedAngleDiff = Colors.Web.green.toHsl().h.rad - Colors.Web.red.toHsl().h.rad

        clear()

        for (i in 101 downTo 0) {
            rect {
                size = size(10.0, 1.0)
                x = 0.0
                y = i.toDouble()
                strokeWidth = 0.0
                fill = Colors.hsl(
                    hue = Angle((1.0 - i.toDouble() / 100.0) * greenRedAngleDiff),
                    saturation = 100.pct,
                    lightness = 50.pct
                )
            }
            if (i % 20 == 0) {
                text {
                    x = 15.0
                    y = 100.0 - i
                    textContent = "$i%"
                }
            }
        }

        gemeentes.values.forEach {
            it.geoPathNode.fill = getGemeenteColor(
                selected = selectedGemeente == it,
                gemeente = it,
                examenlocatieOrOpleider = examenlocatieOrOpleider,
                slagingspercentageSoort = slagingsPercentageSoort,
                schakelSoort = schakelSoort,
                selectedExamenlocatieKeys = selectedExamenlocatieKeys,
                selectedOpleiderKeys = selectedOpleiderKeys,
                selectedProducts = selectedProducts,
                filteredOpleiders = filteredOpleiders,
                filteredExamenlocaties = filteredExamenlocaties,
                filteredProducts = filteredProducts
            )
            it.geoPathNode.redrawPath()
            add(it.geoPathNode)
        }
    }

    val width = 600.0
    val height = 850.0

    fun getGemeenteAt(pos: Point, hiddenCanvas: HTMLCanvasElement): Gemeente? {
        val context = hiddenCanvas.getContext("2d") as CanvasRenderingContext2D
        val col = context
            .getImageData(
                sx = pos.x * hiddenCanvas.width.toDouble() / width,
                sy = pos.y * hiddenCanvas.height.toDouble() / height,
                sw = 1.0,
                sh = 1.0
            )
            .data
        val color = Colors.rgb(col[0].toInt(), col[1].toInt(), col[2].toInt()).rgb

        return gemeentes[idColorToGemeente[color]]
    }

    fun saveGemeentes() {
        var result = ""
        for ((naam, gemeente) in gemeentes) {
            result += "$naam;${gemeente.opleiders.joinToString(",")};${gemeente.examenlocaties.joinToString(",")};${gemeente.slagingspercentageEersteKeerOpleiders};${gemeente.slagingspercentageHerexamenOpleiders};${gemeente.slagingspercentageGecombineerdOpleiders};${gemeente.slagingspercentageEersteKeerExamenlocaties};${gemeente.slagingspercentageHerexamenExamenlocaties};${gemeente.slagingspercentageGecombineerdExamenlocaties}\n"
        }

        println(result)
    }

    override fun RBuilder.render() {
        if (!dataLoaded) return showLoading()
        when (loadingState) {
            LOADING -> showLoading()
            NOT_LOADED -> {
                if (loadingState == NOT_LOADED) {
                    GlobalScope.launch {
                        loadingState = LOADING
                        calculateGemeentes()
                    }
                }
                showLoading()
            }
            LOADED -> {
                var hiddenViz: Viz?
                var hiddenCanvas: HTMLCanvasElement? = null

                vizComponent(
                    width = width,
                    height = height,
                    runOnHiddenViz = { it ->
                        hiddenCanvas = it
                        hiddenViz = this
                        gemeentes.values.forEachApply {
                            hiddenGeoPathNode.redrawPath()
                            hiddenViz!!.add(hiddenGeoPathNode)
                        }
                    }
                ) {
                    println(
                        "rendering card!, gemeentes size: ${gemeentes
                            .size}, idColorToGemeente size: ${idColorToGemeente.size}"
                    )
                    // js https://github.com/data2viz/data2viz/blob/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-js/src/main/kotlin/EarthJs.kt
                    // common https://github.com/data2viz/data2viz/tree/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-common/src/main/kotlin

                    drawGemeentes()

                    // 2nd canvas trick http://bl.ocks.org/Jverma/70f7975a72358e6d69cdd4bf6a0569e7

                    val onHovering: (KPointerEvent) -> Unit = {
                        val new = getGemeenteAt(it.pos, hiddenCanvas!!)

                        if (selectedGemeente != new) {
                            selectedGemeente = new
                            drawGemeentes()
                            render()
                        }
                    }
                    on(KMouseMove, onHovering)
                    on(KTouchStart, onHovering)

                    on(KPointerClick) {
                        getGemeenteAt(it.pos, hiddenCanvas!!)?.let { clicked ->
                            // TODO maybe keep filter but select only all opleiders/ex... from clicked gemeente in currently filtered items
//                            deselectAllOpleiders()
//                            deselectAllExamenlocaties()
                            when (examenlocatieOrOpleider) {
                                EXAMENLOCATIE -> {
                                    selectAllExamenlocaties { it.naam in clicked.examenlocaties }
//                                    selectAllOpleiders { true }
                                }
                                OPLEIDER -> {
                                    selectAllOpleiders { it.code in clicked.opleiders }
//                                    selectAllExamenlocaties { true }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getSlagingsPercentageOpleiders(
    opleiders: Set<String>,
    selectedProducts: Set<Product>,
    slagingspercentageSoort: SlagingspercentageSoort,
    schakelSoort: SchakelSoort
): Double {
    if (opleiders.isEmpty()) return -1.0

    var isEmpty = true

    var voldoende = 0.0
    var onvoldoende = 0.0

    selectedProducts.flatMap { Data.productToOpleiders[it]!! }
        .intersect(opleiders)
        .asSequence()
        .map { opleiderKey -> Data.opleiderToResultaten[opleiderKey]!! }
        .flatMap { resultaatKeys ->
            resultaatKeys.asSequence().map { resultaatKey -> Data.alleResultaten[resultaatKey]!! }
        }
        .flatMap { resultaat -> resultaat.examenresultaatAantallen.asSequence() }
        .filter { examenresultaatAantal ->
            when (slagingspercentageSoort) {
                EERSTE_KEER -> examenresultaatAantal.examenresultaatVersie == EERSTE_EXAMEN_OF_TOETS
                HERKANSING -> examenresultaatAantal.examenresultaatVersie == HEREXAMEN_OF_TOETS
                GECOMBINEERD -> true
            }
        }.forEach { examenresultaatAantal ->
            if (schakelSoort == SchakelSoort.GEMIDDELD || schakelSoort.value == examenresultaatAantal.examenresultaatSoort) {
                when (examenresultaatAantal.examenresultaat) {
                    VOLDOENDE -> voldoende += examenresultaatAantal.aantal
                    ONVOLDOENDE -> onvoldoende += examenresultaatAantal.aantal
                }
                if (examenresultaatAantal.aantal > 0) isEmpty = false
            }
        }

    return when {
        isEmpty -> -1.0
        voldoende + onvoldoende == 0.0 -> 0.0
        else -> voldoende / (voldoende + onvoldoende)
    }
}

fun getSlagingsPercentageExamenlocaties(
    examenlocaties: Set<String>,
    selectedProducts: Set<Product>,
    slagingspercentageSoort: SlagingspercentageSoort,
    schakelSoort: SchakelSoort
): Double {
    if (examenlocaties.isEmpty()) return -1.0

    var isEmpty = true

    var voldoende = 0.0
    var onvoldoende = 0.0

    selectedProducts.flatMap { Data.productToExamenlocaties[it]!! }
        .intersect(examenlocaties)
        .asSequence()
        .map { examenlocatieKey -> Data.examenlocatieToResultaten[examenlocatieKey]!! }
        .flatMap { resultaatKeys ->
            resultaatKeys.asSequence().map { resultaatKey -> Data.alleResultaten[resultaatKey]!! }
        }
        .flatMap { resultaat -> resultaat.examenresultaatAantallen.asSequence() }
        .filter { examenresultaatAantal ->
            when (slagingspercentageSoort) {
                EERSTE_KEER -> examenresultaatAantal.examenresultaatVersie == EERSTE_EXAMEN_OF_TOETS
                HERKANSING -> examenresultaatAantal.examenresultaatVersie == HEREXAMEN_OF_TOETS
                GECOMBINEERD -> true
            }
        }.forEach { examenresultaatAantal ->
            if (schakelSoort == SchakelSoort.GEMIDDELD || schakelSoort.value == examenresultaatAantal.examenresultaatSoort) {
                when (examenresultaatAantal.examenresultaat) {
                    VOLDOENDE -> voldoende += examenresultaatAantal.aantal
                    ONVOLDOENDE -> onvoldoende += examenresultaatAantal.aantal
                }
                if (examenresultaatAantal.aantal > 0) isEmpty = false
            }
        }

    return when {
        isEmpty -> -1.0
        voldoende + onvoldoende == 0.0 -> 0.0
        else -> voldoende / (voldoende + onvoldoende)
    }
}

fun getGemeentePercentage(
    examenlocatieOrOpleider: ExamenlocatieOrOpleider,
    slagingspercentageSoort: SlagingspercentageSoort,
    schakelSoort: SchakelSoort,
    gemeente: Gemeente,
    selectedOpleiderKeys: Set<String>,
    selectedProducts: Set<Product>,
    selectedExamenlocatieKeys: Set<String>,
    filteredOpleiders: List<Opleider>,
    filteredExamenlocaties: List<Examenlocatie>,
    filteredProducts: List<Product>
): Double {
    /*
        all selected:

        no opleiders: 7501
        no examenlocaties: 327
        no products: 103
     */
    val selectedProductsAllOrNone = selectedProducts.size.isAllOrNoProducten()
    val filteredProductsAll = filteredProducts.size == NO_PRODUCTEN

    val selectedOpleiderKeysAllOrNone = selectedOpleiderKeys.size.isAllOrNoOpleiders()
    val filteredOpleidersAll = filteredOpleiders.size == NO_OPLEIDERS

    val selectedExamenlocatieKeysAllOrNone = selectedExamenlocatieKeys.size.isAllOrNoExamenlocaties()
    val filteredExamenlocatiesAll = filteredExamenlocaties.size == NO_EXAMENLOCATIES

    return when (examenlocatieOrOpleider) {
        OPLEIDER ->
            when {
                selectedOpleiderKeysAllOrNone && selectedProductsAllOrNone && filteredOpleidersAll && filteredProductsAll && schakelSoort == SchakelSoort.GEMIDDELD ->
                    when (slagingspercentageSoort) {
                        EERSTE_KEER -> gemeente.slagingspercentageEersteKeerOpleiders
                        HERKANSING -> gemeente.slagingspercentageHerexamenOpleiders
                        GECOMBINEERD -> gemeente.slagingspercentageGecombineerdOpleiders
                    }

                else ->
                    getSlagingsPercentageOpleiders(
                        opleiders = when {
                            filteredOpleidersAll ->
                                when {
                                    selectedOpleiderKeysAllOrNone -> gemeente.opleiders
                                    else -> selectedOpleiderKeys intersect gemeente.opleiders
                                }
                            else ->
                                when {
                                    selectedOpleiderKeysAllOrNone -> gemeente.opleiders intersect filteredOpleiders.map { it.code }
                                    else -> selectedOpleiderKeys intersect gemeente.opleiders intersect filteredOpleiders.map { it.code }
                                }
                        },
                        selectedProducts = when {
                            filteredProductsAll ->
                                when {
                                    selectedProductsAllOrNone -> Product.values().toSet()
                                    else -> selectedProducts
                                }

                            else ->
                                when {
                                    selectedProductsAllOrNone -> filteredProducts.toSet()
                                    else -> selectedProducts intersect filteredProducts
                                }
                        },

                        slagingspercentageSoort = slagingspercentageSoort,
                        schakelSoort = schakelSoort
                    )
            }

        EXAMENLOCATIE ->
            when {
                selectedExamenlocatieKeysAllOrNone && selectedProductsAllOrNone && filteredExamenlocatiesAll && filteredProductsAll && schakelSoort == SchakelSoort.GEMIDDELD ->
                    when (slagingspercentageSoort) {
                        EERSTE_KEER -> gemeente.slagingspercentageEersteKeerExamenlocaties
                        HERKANSING -> gemeente.slagingspercentageHerexamenExamenlocaties
                        GECOMBINEERD -> gemeente.slagingspercentageGecombineerdExamenlocaties
                    }

                else ->
                    getSlagingsPercentageExamenlocaties(
                        examenlocaties = when {
                            filteredExamenlocatiesAll ->
                                when {
                                    selectedExamenlocatieKeysAllOrNone -> gemeente.examenlocaties
                                    else -> (gemeente.examenlocaties intersect selectedExamenlocatieKeys)
                                }
                            else ->
                                when {
                                    selectedExamenlocatieKeysAllOrNone -> gemeente.examenlocaties intersect filteredExamenlocaties.map { it.naam }
                                    else -> gemeente.examenlocaties intersect selectedExamenlocatieKeys intersect filteredExamenlocaties.map { it.naam }
                                }

                        },

                        selectedProducts = when {
                            filteredProductsAll ->
                                when {
                                    selectedProductsAllOrNone -> Product.values().toSet()
                                    else -> selectedProducts
                                }
                            else ->
                                when {
                                    selectedProductsAllOrNone -> filteredProducts.toSet()
                                    else -> selectedProducts intersect filteredProducts
                                }
                        },

                        slagingspercentageSoort = slagingspercentageSoort,
                        schakelSoort = schakelSoort
                    )
            }
    }.apply { gemeente.percentageCache = this }
}

fun getGemeenteColor(
    selected: Boolean,
    gemeente: Gemeente,
    examenlocatieOrOpleider: ExamenlocatieOrOpleider,
    slagingspercentageSoort: SlagingspercentageSoort,
    schakelSoort: SchakelSoort,
    selectedOpleiderKeys: Set<String>,
    selectedExamenlocatieKeys: Set<String>,
    selectedProducts: Set<Product>,
    filteredOpleiders: List<Opleider>,
    filteredExamenlocaties: List<Examenlocatie>,
    filteredProducts: List<Product>,
    useCachedPercentage: Boolean = false
): HslColor =
    if (
        examenlocatieOrOpleider == OPLEIDER && gemeente.opleiders.isEmpty()
        || examenlocatieOrOpleider == EXAMENLOCATIE && gemeente.examenlocaties.isEmpty()
    ) {
        Colors.Web.black.toHsl()
    } else {
        val greenRedAngleDiff = Colors.Web.green.toHsl().h.rad - Colors.Web.red.toHsl().h.rad

        val cachedPercentage = if (useCachedPercentage) gemeente.percentageCache else null
        val percentage = cachedPercentage ?: getGemeentePercentage(
            examenlocatieOrOpleider = examenlocatieOrOpleider,
            slagingspercentageSoort = slagingspercentageSoort,
            schakelSoort = schakelSoort,
            gemeente = gemeente,
            selectedOpleiderKeys = selectedOpleiderKeys,
            selectedProducts = selectedProducts,
            selectedExamenlocatieKeys = selectedExamenlocatieKeys,
            filteredOpleiders = filteredOpleiders,
            filteredExamenlocaties = filteredExamenlocaties,
            filteredProducts = filteredProducts
        )

        if (percentage < 0.0000001) Colors.Web.black.toHsl()
        else Colors.hsl(
            hue = Angle(greenRedAngleDiff * percentage),
            saturation = 100.pct,
            lightness = if (selected) 20.pct else 50.pct
        )
    }

fun RBuilder.nederlandMap(handler: RElementBuilder<NederlandVizMapProps>.() -> Unit) =
    child(NederlandVizMap::class, handler)
