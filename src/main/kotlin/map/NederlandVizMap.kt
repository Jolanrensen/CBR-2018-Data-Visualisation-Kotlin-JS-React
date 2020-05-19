package map

import ApplyFilter
import ExamenlocatieOrOpleider
import ExamenlocatieOrOpleider.EXAMENLOCATIE
import ExamenlocatieOrOpleider.OPLEIDER
import Loading
import Loading.*
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
import data.ExamenResultaatVersie.EERSTE_EXAMEN_OF_TOETS
import data.ExamenResultaatVersie.HEREXAMEN_OF_TOETS
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
import kotlinx.css.Display
import kotlinx.css.JustifyContent
import kotlinx.css.display
import kotlinx.css.justifyContent
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
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
}

interface NederlandVizMapState : RState {
    var gemeentes: List<Gemeente>
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


    override fun NederlandVizMapState.init(props: NederlandVizMapProps) {
        gemeentes = listOf()
        loadingState = NOT_LOADED
    }

    var gemeentes by stateDelegateOf(NederlandVizMapState::gemeentes)
    var loadingState by stateDelegateOf(NederlandVizMapState::loadingState)

    // don't update when [selectedGemeente] changed
    override fun shouldComponentUpdate(nextProps: NederlandVizMapProps, nextState: NederlandVizMapState) =
        props.dataLoaded != nextProps.dataLoaded
                || props.examenlocatieOrOpleider != nextProps.examenlocatieOrOpleider
                || props.slagingspercentageSoort != nextProps.slagingspercentageSoort
                || state.gemeentes != nextState.gemeentes
                || state.loadingState != nextState.loadingState

    private val nederland: FeatureCollection<Data.GemeentesProperties> =
        Data.geoJson!! // geometry type is polygon/multipolygon for each

    private val idColorToGemeente: HashMap<Int, Gemeente> = hashMapOf()

    @Suppress("DuplicatedCode")
    private fun calculateGemeentes() {
        if (gemeentes.isNotEmpty() || loadingState == LOADED) return

        GlobalScope.launch {
            delay(500)
            println("calculating 'gemeentes'")

            gemeentes = nederland.features.map { feature ->
                val featureStatnaam = feature.properties.statnaam.toLowerCase()

                // TODO move to
                val opleiders = Data.alleOpleiders
                    .values
                    .filter { it.gemeente.toLowerCase() == featureStatnaam }

                val examenlocaties = Data.alleExamenlocaties
                    .values
                    .filter { it.gemeente.toLowerCase() == featureStatnaam }

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

                Gemeente(
                    feature = feature,
                    opleiders = opleiders,
                    examenlocaties = examenlocaties,
                    geoPathNode = geoPathNode,
                    hiddenGeoPathNode = hiddenGeoPathNode,

                    /**
                     * Totaal percentage = sum[i in opleiders](i.slagingspecentage * aantal resultaten in i) / totaal aantal resultaten
                     */
                    slagingspercentageEersteKeerOpleiders = opleiders
                        .filter { it.slagingspercentageEersteKeer != null }
                        .let { opleiders ->
                            val opleiderCodes = opleiders.map { it.code }

                            val opleidersResultSize = Data.opleiderToResultaten
                                .asSequence()
                                .sumByDouble { (key, entry) ->
                                    if (key !in opleiderCodes) {
                                        0.0
                                    } else {
                                        entry.sumByDouble {
                                            it.examenResultaatAantallen.count {
                                                it.examenResultaatVersie == EERSTE_EXAMEN_OF_TOETS
                                            }.toDouble()
                                        }
                                    }
                                }

                            opleiders.sumByDouble {
                                it.slagingspercentageEersteKeer!! *
                                        Data.opleiderToResultaten[it.code]!!
                                            .sumByDouble {
                                                it.examenResultaatAantallen.count {
                                                    it.examenResultaatVersie == EERSTE_EXAMEN_OF_TOETS
                                                }.toDouble()
                                            }
                            } / opleidersResultSize
                        },
                    slagingspercentageHerexamenOpleiders = opleiders
                        .filter { it.slagingspercentageEersteKeer != null }
                        .let { opleiders ->
                            val opleiderCodes = opleiders.map { it.code }

                            val opleidersResultSize = Data.opleiderToResultaten
                                .asSequence()
                                .sumByDouble { (key, entry) ->
                                    if (key !in opleiderCodes) {
                                        0.0
                                    } else {
                                        entry.sumByDouble {
                                            it.examenResultaatAantallen.count {
                                                it.examenResultaatVersie == HEREXAMEN_OF_TOETS
                                            }.toDouble()
                                        }
                                    }
                                }

                            opleiders.sumByDouble {
                                it.slagingspercentageEersteKeer!! *
                                        Data.opleiderToResultaten[it.code]!!
                                            .sumByDouble {
                                                it.examenResultaatAantallen.count {
                                                    it.examenResultaatVersie == HEREXAMEN_OF_TOETS
                                                }.toDouble()
                                            }
                            } / opleidersResultSize
                        },
                    slagingspercentageGecombineerdOpleiders = opleiders
                        .filter { it.slagingspercentageEersteKeer != null && it.slagingspercentageHerkansing != null }
                        .let { opleiders ->
                            val opleiderCodes = opleiders.map { it.code }

                            val opleidersResultSize = Data.opleiderToResultaten
                                .asSequence()
                                .sumByDouble { (key, entry) ->
                                    if (key !in opleiderCodes) {
                                        0.0
                                    } else {
                                        entry.sumByDouble {
                                            it.examenResultaatAantallen.size.toDouble()
                                        }
                                    }
                                }

                            opleiders.sumByDouble {
                                (it.slagingspercentageEersteKeer!! + it.slagingspercentageHerkansing!!) / 2.0 *
                                        Data.opleiderToResultaten[it.code]!!
                                            .sumByDouble {
                                                it.examenResultaatAantallen.size.toDouble()
                                            }
                            } / opleidersResultSize
                        },

                    slagingspercentageEersteKeerExamenlocaties = examenlocaties
                        .filter { it.slagingspercentageEersteKeer != null }
                        .let { examenlocaties ->
                            val examenlocatieNamen = examenlocaties.map { it.naam }

                            val examenlocatiesResultSize = Data.examenlocatieToResultaten
                                .asSequence()
                                .sumByDouble { (key, entry) ->
                                    if (key !in examenlocatieNamen) {
                                        0.0
                                    } else {
                                        entry.sumByDouble {
                                            it.examenResultaatAantallen.count {
                                                it.examenResultaatVersie == EERSTE_EXAMEN_OF_TOETS
                                            }.toDouble()
                                        }
                                    }
                                }

                            examenlocaties.sumByDouble {
                                it.slagingspercentageEersteKeer!! *
                                        Data.examenlocatieToResultaten[it.naam]!!
                                            .sumByDouble {
                                                it.examenResultaatAantallen.count {
                                                    it.examenResultaatVersie == EERSTE_EXAMEN_OF_TOETS
                                                }.toDouble()
                                            }
                            } / examenlocatiesResultSize
                        },
                    slagingspercentageHerexamenExamenlocaties = examenlocaties
                        .filter { it.slagingspercentageEersteKeer != null }
                        .let { examenlocaties ->
                            val examenlocatieNamen = examenlocaties.map { it.naam }

                            val examenlocatiesResultSize = Data.examenlocatieToResultaten
                                .asSequence()
                                .sumByDouble { (key, entry) ->
                                    if (key !in examenlocatieNamen) {
                                        0.0
                                    } else {
                                        entry.sumByDouble {
                                            it.examenResultaatAantallen.count {
                                                it.examenResultaatVersie == HEREXAMEN_OF_TOETS
                                            }.toDouble()
                                        }
                                    }
                                }

                            examenlocaties.sumByDouble {
                                it.slagingspercentageEersteKeer!! *
                                        Data.examenlocatieToResultaten[it.naam]!!
                                            .sumByDouble {
                                                it.examenResultaatAantallen.count {
                                                    it.examenResultaatVersie == HEREXAMEN_OF_TOETS
                                                }.toDouble()
                                            }
                            } / examenlocatiesResultSize
                        },

                    slagingspercentageGecombineerdExamenlocaties = examenlocaties
                        .filter { it.slagingspercentageEersteKeer != null && it.slagingspercentageHerkansing != null }
                        .let { examenlocaties ->
                            val examenlocatieNamen = examenlocaties.map { it.naam }

                            val examenlocatiesResultSize = Data.examenlocatieToResultaten
                                .asSequence()
                                .sumByDouble { (key, entry) ->
                                    if (key !in examenlocatieNamen) {
                                        0.0
                                    } else {
                                        entry.sumByDouble {
                                            it.examenResultaatAantallen.count().toDouble()
                                        }
                                    }
                                }

                            examenlocaties.sumByDouble {
                                (it.slagingspercentageEersteKeer!! + it.slagingspercentageHerkansing!!) / 2.0 *
                                        Data.examenlocatieToResultaten[it.naam]!!
                                            .sumByDouble {
                                                it.examenResultaatAantallen.count().toDouble()
                                            }
                            } / examenlocatiesResultSize
                        }
                ).apply { idColorToGemeente[idColor!!.rgb] = this }
            }
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

        gemeentes.forEach {
            it.geoPathNode.fill = getGemeenteColor(
                selected = selectedGemeente == it,
                gemeente = it,
                examenlocatieOrOpleider = examenlocatieOrOpleider,
                slagingspercentageSoort = slagingsPercentageSoort
            )
            it.geoPathNode.redrawPath()
            add(it.geoPathNode)
        }
    }

    fun getGemeenteAt(pos: Point, hiddenCanvas: HTMLCanvasElement): Gemeente? {
        val context = hiddenCanvas.getContext("2d") as CanvasRenderingContext2D
        val col = context
            .getImageData(pos.x, pos.y, 1.0, 1.0)
            .data
        val color = Colors.rgb(col[0].toInt(), col[1].toInt(), col[2].toInt()).rgb

        return idColorToGemeente[color]
    }

    override fun RBuilder.render() {
        if (!dataLoaded) return showLoading()
        when (loadingState) {
            LOADING -> showLoading()
            NOT_LOADED -> styledDiv {
                css {
                    display = Display.flex
                    justifyContent = JustifyContent.center
                }
                mButton(caption = "LAAD KAART", color = MColor.primary, onClick = {
                    if (loadingState == NOT_LOADED) {
                        loadingState = LOADING
                        calculateGemeentes()
                    }
                })
            }
            LOADED -> {
                var hiddenViz: Viz?
                var hiddenCanvas: HTMLCanvasElement? = null

                vizComponent(
                    width = 600.0,
                    height = 850.0,
                    runOnHiddenViz = { it ->
                        hiddenCanvas = it
                        hiddenViz = this
                        gemeentes.forEachApply {
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
                            when (examenlocatieOrOpleider) {
                                EXAMENLOCATIE -> {
                                    setExamenlocatieFilters(clicked.name)
                                    setOpleiderFilters("")
                                    selectAllExamenlocaties()
                                    selectAllOpleiders()
                                }
                                OPLEIDER -> {
                                    setOpleiderFilters(clicked.name)
                                    setExamenlocatieFilters("")
                                    selectAllOpleiders()
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
    slagingspercentageSoort: SlagingspercentageSoort
): HslColor =
//        val maxNoOpleiders = 470 // den haag
    if (
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
                        EERSTE_KEER -> gemeente.slagingspercentageEersteKeerOpleiders
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

fun RBuilder.nederlandMap(handler: RElementBuilder<NederlandVizMapProps>.() -> Unit) =
    child(NederlandVizMap::class, handler)
