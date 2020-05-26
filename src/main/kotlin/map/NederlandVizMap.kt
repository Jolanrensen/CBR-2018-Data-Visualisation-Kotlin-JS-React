package map

import ApplyFilter
import DeselectAll
import ExamenlocatieOrOpleider
import ExamenlocatieOrOpleider.EXAMENLOCATIE
import ExamenlocatieOrOpleider.OPLEIDER
import Loading
import Loading.*
import ResultFilterAndShowProps
import SelectAll
import SlagingspercentageSoort
import delegates.ReactPropAndStateDelegates.StateAsProp
import delegates.ReactPropAndStateDelegates.propDelegateOf
import delegates.ReactPropAndStateDelegates.stateDelegateOf
import SlagingspercentageSoort.*
import com.ccfraser.muirwik.components.MColor
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.mCircularProgress
import data.*
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
import kotlinx.coroutines.delay
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

    var setOpleiderFilters: ApplyFilter
    var setExamenlocatieFilters: ApplyFilter

    var selectAllOpleiders: SelectAll
    var selectAllExamenlocaties: SelectAll

    var deselectAllOpleiders: DeselectAll
    var deselectAllExamenlocaties: DeselectAll

    var selectedOpleiderKeys: StateAsProp<Set<String>>
    var selectedExamenlocatieKeys: StateAsProp<Set<String>>
    var selectedProducts: StateAsProp<Set<Product>>
}

interface NederlandVizMapState : RState {
    var gemeentes: Map<String, Gemeente>
    var loadingState: Loading
}

class NederlandVizMap(prps: NederlandVizMapProps) : RComponent<NederlandVizMapProps, NederlandVizMapState>(prps) {

    var selectedGemeenteState by propDelegateOf(NederlandVizMapProps::selectedGemeente)
    val examenlocatieOrOpleider by propDelegateOf(NederlandVizMapProps::examenlocatieOrOpleider)
    val slagingsPercentageSoort by propDelegateOf(NederlandVizMapProps::slagingspercentageSoort)

    var selectedGemeente: Gemeente? = selectedGemeenteState
        set(value) {
            field = value
            selectedGemeenteState = value
        }

    val dataLoaded by propDelegateOf(NederlandVizMapProps::dataLoaded)
    val setOpleiderFilters by propDelegateOf(NederlandVizMapProps::setOpleiderFilters)
    val setExamenlocatieFilters by propDelegateOf(NederlandVizMapProps::setExamenlocatieFilters)

    val selectAllOpleiders by propDelegateOf(NederlandVizMapProps::selectAllOpleiders)
    val selectAllExamenlocaties by propDelegateOf(NederlandVizMapProps::selectAllExamenlocaties)

    val deselectAllOpleiders by propDelegateOf(NederlandVizMapProps::deselectAllOpleiders)
    val deselectAllExamenlocaties by propDelegateOf(NederlandVizMapProps::deselectAllExamenlocaties)

    private var selectedOpleiderKeys by propDelegateOf(NederlandVizMapProps::selectedOpleiderKeys)
    private var selectedExamenlocatieKeys by propDelegateOf(NederlandVizMapProps::selectedExamenlocatieKeys)
    private var selectedProducts by propDelegateOf(NederlandVizMapProps::selectedProducts)


    override fun NederlandVizMapState.init(props: NederlandVizMapProps) {
        gemeentes = emptyMap()
        loadingState = NOT_LOADED
    }

    var gemeentes by stateDelegateOf(NederlandVizMapState::gemeentes)
    var loadingState by stateDelegateOf(NederlandVizMapState::loadingState)

    private var previousSelectedOpleiderKeys: Set<String> = emptySet()
    private var previousSelectedExamenlocatieKeys: Set<String> = emptySet()
    private var previousSelectedProducts: Set<Product> = emptySet()

    // don't update when [selectedGemeente] changed (because that will loop)
    @Suppress("SimplifyBooleanWithConstants")
    override fun shouldComponentUpdate(nextProps: NederlandVizMapProps, nextState: NederlandVizMapState): Boolean {
        previousSelectedOpleiderKeys = selectedOpleiderKeys
        previousSelectedExamenlocatieKeys = selectedExamenlocatieKeys
        previousSelectedProducts = selectedProducts

        return false
                || props.dataLoaded != nextProps.dataLoaded
                || props.examenlocatieOrOpleider != nextProps.examenlocatieOrOpleider
                || props.slagingspercentageSoort != nextProps.slagingspercentageSoort
                || props.selectedOpleiderKeys != nextProps.selectedOpleiderKeys
                || props.selectedExamenlocatieKeys != nextProps.selectedExamenlocatieKeys
                || props.selectedProducts != nextProps.selectedProducts
                || state.gemeentes != nextState.gemeentes
                || state.loadingState != nextState.loadingState
    }

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

//                val opleiders = Data.alleOpleiders
//                    .values
//                    .filter { it.gemeente.toLowerCase() == featureStatnaam }
//
//                val examenlocaties = Data.alleExamenlocaties
//                    .values
//                    .filter { it.gemeente.toLowerCase() == featureStatnaam }

                fun GeoPathNode.runOnNode() {
                    stroke = Colors.Web.black
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
                    opleiders = currentGemeenteData[0].split(",").toSet(), //opleiders.map { it.code },
                    examenlocaties = currentGemeenteData[1].split(",").toSet(), //examenlocaties.map { it.naam },
                    geoPathNode = geoPathNode,
                    hiddenGeoPathNode = hiddenGeoPathNode,

                    /**
                     * Totaal percentage = sum[i in opleiders](i.slagingspecentage * aantal resultaten in i) / totaal aantal resultaten
                     */
                    slagingspercentageEersteKeerOpleiders = currentGemeenteData[2].toDouble(),
//                    opleiders
//                        .filter { it.slagingspercentageEersteKeer != null }
//                        .let { opleiders ->
//                            val opleiderCodes = opleiders.map { it.code }
//
//                            val opleidersResultSize = Data.opleiderToResultaten
//                                .asSequence()
//                                .sumByDouble { (key, entry) ->
//                                    if (key !in opleiderCodes) {
//                                        0.0
//                                    } else {
//                                        entry.sumByDouble {
//                                            Data.alleResultaten[it]!!.examenresultaatAantallen.count {
//                                                it.examenresultaatVersie == EERSTE_EXAMEN_OF_TOETS
//                                            }.toDouble()
//                                        }
//                                    }
//                                }
//
//                            opleiders.sumByDouble {
//                                it.slagingspercentageEersteKeer!! *
//                                        Data.opleiderToResultaten[it.code]!!
//                                            .sumByDouble {
//                                                Data.alleResultaten[it]!!.examenresultaatAantallen.count {
//                                                    it.examenresultaatVersie == EERSTE_EXAMEN_OF_TOETS
//                                                }.toDouble()
//                                            }
//                            } / opleidersResultSize
//                        },
                    slagingspercentageHerexamenOpleiders = currentGemeenteData[3].toDouble(),
//                    opleiders
//                        .filter { it.slagingspercentageEersteKeer != null }
//                        .let { opleiders ->
//                            val opleiderCodes = opleiders.map { it.code }
//
//                            val opleidersResultSize = Data.opleiderToResultaten
//                                .asSequence()
//                                .sumByDouble { (key, entry) ->
//                                    if (key !in opleiderCodes) {
//                                        0.0
//                                    } else {
//                                        entry.sumByDouble {
//                                            Data.alleResultaten[it]!!.examenresultaatAantallen.count {
//                                                it.examenresultaatVersie == HEREXAMEN_OF_TOETS
//                                            }.toDouble()
//                                        }
//                                    }
//                                }
//
//                            opleiders.sumByDouble {
//                                it.slagingspercentageEersteKeer!! *
//                                        Data.opleiderToResultaten[it.code]!!
//                                            .sumByDouble {
//                                                Data.alleResultaten[it]!!.examenresultaatAantallen.count {
//                                                    it.examenresultaatVersie == HEREXAMEN_OF_TOETS
//                                                }.toDouble()
//                                            }
//                            } / opleidersResultSize
//                        },
                    slagingspercentageGecombineerdOpleiders = currentGemeenteData[4].toDouble(),
//                    opleiders
//                        .filter { it.slagingspercentageEersteKeer != null && it.slagingspercentageHerkansing != null }
//                        .let { opleiders ->
//                            val opleiderCodes = opleiders.map { it.code }
//
//                            val opleidersResultSize = Data.opleiderToResultaten
//                                .asSequence()
//                                .sumByDouble { (key, entry) ->
//                                    if (key !in opleiderCodes) {
//                                        0.0
//                                    } else {
//                                        entry.sumByDouble {
//                                            Data.alleResultaten[it]!!.examenresultaatAantallen.size.toDouble()
//                                        }
//                                    }
//                                }
//
//                            opleiders.sumByDouble {
//                                (it.slagingspercentageEersteKeer!! + it.slagingspercentageHerkansing!!) / 2.0 *
//                                        Data.opleiderToResultaten[it.code]!!
//                                            .sumByDouble {
//                                                Data.alleResultaten[it]!!.examenresultaatAantallen.size.toDouble()
//                                            }
//                            } / opleidersResultSize
//                        },

                    slagingspercentageEersteKeerExamenlocaties = currentGemeenteData[5].toDouble(),
//                    examenlocaties
//                        .filter { it.slagingspercentageEersteKeer != null }
//                        .let { examenlocaties ->
//                            val examenlocatieNamen = examenlocaties.map { it.naam }
//
//                            val examenlocatiesResultSize = Data.examenlocatieToResultaten
//                                .asSequence()
//                                .sumByDouble { (key, entry) ->
//                                    if (key !in examenlocatieNamen) {
//                                        0.0
//                                    } else {
//                                        entry.sumByDouble {
//                                            Data.alleResultaten[it]!!.examenresultaatAantallen.count {
//                                                it.examenresultaatVersie == EERSTE_EXAMEN_OF_TOETS
//                                            }.toDouble()
//                                        }
//                                    }
//                                }
//
//                            examenlocaties.sumByDouble {
//                                it.slagingspercentageEersteKeer!! *
//                                        Data.examenlocatieToResultaten[it.naam]!!
//                                            .sumByDouble {
//                                                Data.alleResultaten[it]!!.examenresultaatAantallen.count {
//                                                    it.examenresultaatVersie == EERSTE_EXAMEN_OF_TOETS
//                                                }.toDouble()
//                                            }
//                            } / examenlocatiesResultSize
//                        },
                    slagingspercentageHerexamenExamenlocaties = currentGemeenteData[6].toDouble(),
//                    examenlocaties
//                        .filter { it.slagingspercentageEersteKeer != null }
//                        .let { examenlocaties ->
//                            val examenlocatieNamen = examenlocaties.map { it.naam }
//
//                            val examenlocatiesResultSize = Data.examenlocatieToResultaten
//                                .asSequence()
//                                .sumByDouble { (key, entry) ->
//                                    if (key !in examenlocatieNamen) {
//                                        0.0
//                                    } else {
//                                        entry.sumByDouble {
//                                            Data.alleResultaten[it]!!.examenresultaatAantallen.count {
//                                                it.examenresultaatVersie == HEREXAMEN_OF_TOETS
//                                            }.toDouble()
//                                        }
//                                    }
//                                }
//
//                            examenlocaties.sumByDouble {
//                                it.slagingspercentageEersteKeer!! *
//                                        Data.examenlocatieToResultaten[it.naam]!!
//                                            .sumByDouble {
//                                                Data.alleResultaten[it]!!.examenresultaatAantallen.count {
//                                                    it.examenresultaatVersie == HEREXAMEN_OF_TOETS
//                                                }.toDouble()
//                                            }
//                            } / examenlocatiesResultSize
//                        },

                    slagingspercentageGecombineerdExamenlocaties = currentGemeenteData[7].toDouble()
//                    examenlocaties
//                        .filter { it.slagingspercentageEersteKeer != null && it.slagingspercentageHerkansing != null }
//                        .let { examenlocaties ->
//                            val examenlocatieNamen = examenlocaties.map { it.naam }
//
//                            val examenlocatiesResultSize = Data.examenlocatieToResultaten
//                                .asSequence()
//                                .sumByDouble { (key, entry) ->
//                                    if (key !in examenlocatieNamen) {
//                                        0.0
//                                    } else {
//                                        entry.sumByDouble {
//                                            Data.alleResultaten[it]!!.examenresultaatAantallen.count().toDouble()
//                                        }
//                                    }
//                                }
//
//                            examenlocaties.sumByDouble {
//                                (it.slagingspercentageEersteKeer!! + it.slagingspercentageHerkansing!!) / 2.0 *
//                                        Data.examenlocatieToResultaten[it.naam]!!
//                                            .sumByDouble {
//                                                Data.alleResultaten[it]!!.examenresultaatAantallen.count().toDouble()
//                                            }
//                            } / examenlocatiesResultSize
//                        }
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
                selectedExamenlocatieKeys = selectedExamenlocatieKeys,
                selectedOpleiderKeys = selectedOpleiderKeys,
                selectedProducts = selectedProducts
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
                            deselectAllOpleiders()
                            deselectAllExamenlocaties()
                            setExamenlocatieFilters("")
                            setOpleiderFilters("")
                            when (examenlocatieOrOpleider) {
                                EXAMENLOCATIE -> {
                                    selectedExamenlocatieKeys = clicked.examenlocaties
                                    selectAllOpleiders()
                                }
                                OPLEIDER -> {
                                    selectedOpleiderKeys = clicked.opleiders
                                    selectAllExamenlocaties()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getGemeenteColor(
    selected: Boolean,
    gemeente: Gemeente,
    examenlocatieOrOpleider: ExamenlocatieOrOpleider,
    slagingspercentageSoort: SlagingspercentageSoort,

    selectedOpleiderKeys: Set<String>,
    selectedExamenlocatieKeys: Set<String>,
    selectedProducts: Set<Product>
): HslColor {

    /* TODO
        all selected:

        no opleiders: 7501
        no examenlocaties: 327
        no products: 103
     */
    return if (
        when (examenlocatieOrOpleider) {
            OPLEIDER -> gemeente.opleiders.isEmpty()
            EXAMENLOCATIE -> gemeente.examenlocaties.isEmpty()
        }
    ) {
        Colors.Web.black.toHsl()
    } else {
        val greenRedAngleDiff = Colors.Web.green.toHsl().h.rad - Colors.Web.red.toHsl().h.rad

        Colors.hsl(
            hue = Angle(
                when (examenlocatieOrOpleider) {
                    OPLEIDER -> when (slagingspercentageSoort) {
                        EERSTE_KEER -> gemeente.slagingspercentageEersteKeerOpleiders // todo use these only when none or all are selected
                        HERKANSING -> gemeente.slagingspercentageHerexamenOpleiders
                        GECOMBINEERD -> gemeente.slagingspercentageGecombineerdOpleiders
                    }
                    EXAMENLOCATIE -> when (slagingspercentageSoort) {
                        EERSTE_KEER -> gemeente.slagingspercentageEersteKeerExamenlocaties
                        HERKANSING -> gemeente.slagingspercentageHerexamenExamenlocaties
                        GECOMBINEERD -> gemeente.slagingspercentageGecombineerdExamenlocaties
                    }
                } * greenRedAngleDiff
            ),
            saturation = 100.pct,
            lightness = if (selected) 20.pct else 50.pct
        )
    }
}

fun RBuilder.nederlandMap(handler: RElementBuilder<NederlandVizMapProps>.() -> Unit) =
    child(NederlandVizMap::class, handler)
