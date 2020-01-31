import ExamenlocatieOrOpleider.EXAMENLOCATIE
import ExamenlocatieOrOpleider.OPLEIDER
import Loading.*
import delegates.ReactPropAndStateDelegates.StateAsProp
import delegates.ReactPropAndStateDelegates.propDelegateOf
import delegates.ReactPropAndStateDelegates.stateDelegateOf
import SlagingspercentageSoort.*
import com.ccfraser.muirwik.components.MColor
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.mCircularProgress
import data.*
import data2viz.GeoPathNode
import data2viz.vizComponent
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
    var selectedGemeente: StateAsProp<NederlandVizMap.Gemeente?>
    var dataLoaded: Boolean
    var examenlocatieOrOpleider: ExamenlocatieOrOpleider
    var slagingspercentageSoort: SlagingspercentageSoort

    var setOpleiderFilters: ApplyFilter
    var setExamenlocatieFilters: ApplyFilter
}

interface NederlandVizMapState : RState {
    var gemeentes: List<NederlandVizMap.Gemeente>
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

    override fun NederlandVizMapState.init(props: NederlandVizMapProps) {
        gemeentes = listOf()
        loadingState = NOT_LOADED
    }

    var gemeentes by stateDelegateOf(NederlandVizMapState::gemeentes)
    var loadingState by stateDelegateOf(NederlandVizMapState::loadingState)

    override fun shouldComponentUpdate(nextProps: NederlandVizMapProps, nextState: NederlandVizMapState) =
        props.dataLoaded != nextProps.dataLoaded
                || props.examenlocatieOrOpleider != nextProps.examenlocatieOrOpleider
                || props.slagingspercentageSoort != nextProps.slagingspercentageSoort
                || state.gemeentes != nextState.gemeentes
                || state.loadingState != nextState.loadingState

    private val nederland = Data.geoJson!! // geometry type is polygon/multipolygon for each

    data class Gemeente(
        val feature: Feature<Data.GemeentesProperties>,
        val opleiders: Collection<Opleider> = setOf(),
        val examenlocaties: Collection<Examenlocatie> = setOf(),
        val geoPathNode: GeoPathNode,
        val hiddenGeoPathNode: GeoPathNode,

        val slagingspercentageEersteKeerOpleiders: Double,
        val slagingspercentageHerexamenOpleiders: Double,
        val slagingspercentageGecombineerdOpleiders: Double,

        val slagingspercentageEersteKeerExamenlocaties: Double,
        val slagingspercentageHerexamenExamenlocaties: Double,
        val slagingspercentageGecombineerdExamenlocaties: Double
    ) {
        val name get() = feature.properties.statnaam
    }

    private val idColorToGemeente = hashMapOf<Int, Gemeente>()

    @Suppress("DuplicatedCode")
    private fun calculateGemeentes() {
        if (gemeentes.isNotEmpty() || loadingState == LOADED) return

        GlobalScope.launch {
            delay(500)
            println("calculating 'gemeentes'")

            gemeentes = nederland.features.mapIndexed { index, feature ->
                val opleiders = Data.alleOpleiders
                    .asSequence()
                    .filter {
                        it.value.gemeente.toLowerCase() == feature.properties.statnaam.toLowerCase()
                    }
                    .map { it.value }
                    .toSet()

                val examenlocaties = Data.alleExamenlocaties
                    .asSequence()
                    .filter {
                        it.value.gemeente.toLowerCase() == feature.properties.statnaam.toLowerCase()
                    }
                    .map { it.value }
                    .toSet()

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
                        .let {
                            val opleiderCodes = it.map { it.code }
                            val opleidersResultSize = Data.opleiderToResultaten
                                .asSequence()
                                .filter { it.key in opleiderCodes }
                                .map { it.value }
                                .flatten()
                                .map { it.examenResultaatAantallen }
                                .flatten()
                                .eersteExamen
                                .toList()
                                .size
                                .toDouble()

                            it.sumByDouble {
                                it.slagingspercentageEersteKeer!! *
                                        Data.opleiderToResultaten[it.code]!!
                                            .asSequence()
                                            .map { it.examenResultaatAantallen }
                                            .flatten()
                                            .eersteExamen
                                            .toList()
                                            .size
                                            .toDouble()
                            } / opleidersResultSize
                        },
                    slagingspercentageHerexamenOpleiders = opleiders
                        .filter { it.slagingspercentageHerkansing != null }
                        .let {
                            val opleiderCodes = it.map { it.code }
                            val opleidersResultSize = Data.opleiderToResultaten
                                .asSequence()
                                .filter { it.key in opleiderCodes }
                                .map { it.value }
                                .flatten()
                                .map { it.examenResultaatAantallen }
                                .flatten()
                                .herExamen
                                .toList()
                                .size
                                .toDouble()

                            it.sumByDouble {
                                it.slagingspercentageHerkansing!! *
                                        Data.opleiderToResultaten[it.code]!!
                                            .asSequence()
                                            .map { it.examenResultaatAantallen }
                                            .flatten()
                                            .herExamen
                                            .toList()
                                            .size
                                            .toDouble()
                            } / opleidersResultSize
                        },
                    slagingspercentageGecombineerdOpleiders = opleiders
                        .filter { it.slagingspercentageEersteKeer != null && it.slagingspercentageHerkansing != null }
                        .let {
                            val opleiderCodes = it.map { it.code }
                            val opleidersResultSize = Data.opleiderToResultaten
                                .asSequence()
                                .filter { it.key in opleiderCodes }
                                .map { it.value }
                                .flatten()
                                .map { it.examenResultaatAantallen }
                                .flatten()
                                .toList()
                                .size
                                .toDouble()

                            it.sumByDouble {
                                (it.slagingspercentageEersteKeer!! + it.slagingspercentageHerkansing!!) / 2.0 *
                                        Data.opleiderToResultaten[it.code]!!
                                            .asSequence()
                                            .map { it.examenResultaatAantallen }
                                            .flatten()
                                            .toList()
                                            .size
                                            .toDouble()
                            } / opleidersResultSize
                        },

                    slagingspercentageEersteKeerExamenlocaties = examenlocaties
                        .filter { it.slagingspercentageEersteKeer != null }
                        .let {
                            val examenlocatieNamen = it.map { it.naam }
                            val examenlocatiesResultSize = Data.examenlocatieToResultaten
                                .asSequence()
                                .filter { it.key in examenlocatieNamen }
                                .map { it.value }
                                .flatten()
                                .map { it.examenResultaatAantallen }
                                .flatten()
                                .eersteExamen
                                .toList()
                                .size
                                .toDouble()

                            it.sumByDouble {
                                it.slagingspercentageEersteKeer!! *
                                        Data.examenlocatieToResultaten[it.naam]!!
                                            .asSequence()
                                            .map { it.examenResultaatAantallen }
                                            .flatten()
                                            .eersteExamen
                                            .toList()
                                            .size
                                            .toDouble()
                            } / examenlocatiesResultSize
                        },
                    slagingspercentageHerexamenExamenlocaties = examenlocaties
                        .filter { it.slagingspercentageHerkansing != null }
                        .let {
                            val examenlocatieNamen = it.map { it.naam }
                            val examenlocatiesResultSize = Data.examenlocatieToResultaten
                                .asSequence()
                                .filter { it.key in examenlocatieNamen }
                                .map { it.value }
                                .flatten()
                                .map { it.examenResultaatAantallen }
                                .flatten()
                                .herExamen
                                .toList()
                                .size
                                .toDouble()

                            it.sumByDouble {
                                it.slagingspercentageHerkansing!! *
                                        Data.examenlocatieToResultaten[it.naam]!!
                                            .asSequence()
                                            .map { it.examenResultaatAantallen }
                                            .flatten()
                                            .herExamen
                                            .toList()
                                            .size
                                            .toDouble()
                            } / examenlocatiesResultSize
                        },
                    slagingspercentageGecombineerdExamenlocaties = examenlocaties
                        .filter { it.slagingspercentageEersteKeer != null && it.slagingspercentageHerkansing != null }
                        .let {
                            val examenlocatieNamen = it.map { it.naam }
                            val examenlocatiesResultSize = Data.examenlocatieToResultaten
                                .asSequence()
                                .filter { it.key in examenlocatieNamen }
                                .map { it.value }
                                .flatten()
                                .map { it.examenResultaatAantallen }
                                .flatten()
                                .toList()
                                .size
                                .toDouble()

                            it.sumByDouble {
                                (it.slagingspercentageEersteKeer!! + it.slagingspercentageHerkansing!!) / 2.0 *
                                        Data.examenlocatieToResultaten[it.naam]!!
                                            .asSequence()
                                            .map { it.examenResultaatAantallen }
                                            .flatten()
                                            .toList()
                                            .size
                                            .toDouble()
                            } / examenlocatiesResultSize
                        }
                ).apply { idColorToGemeente[idColor!!.rgb] = this }
            }
            loadingState = LOADED
        }
    }


    override fun RBuilder.render() {
        fun showLoading() {
            styledDiv {
                css {
                    display = Display.flex
                    justifyContent = JustifyContent.center
                }
                mCircularProgress()
            }
        }
        if (!dataLoaded) return showLoading()
        when (loadingState) {
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
            LOADING -> showLoading()
            LOADED -> {
                var hiddenViz: Viz?
                var hiddenCanvas: HTMLCanvasElement? = null

                fun getGemeenteAt(pos: Point): Gemeente? {
                    val context = hiddenCanvas!!.getContext("2d") as CanvasRenderingContext2D
                    val col = context
                        .getImageData(pos.x, pos.y, 1.0, 1.0)
                        .data
                    val color = Colors.rgb(col[0].toInt(), col[1].toInt(), col[2].toInt()).rgb

                    return idColorToGemeente[color]
                }

                vizComponent(
                    width = 600.0,
                    height = 850.0,
                    runOnHiddenViz = { it ->
                        hiddenCanvas = it
                        hiddenViz = this
                        this@NederlandVizMap.gemeentes.forEachApply {
                            hiddenGeoPathNode.redrawPath()
                            hiddenViz!!.add(hiddenGeoPathNode)
                        }
                    }
                ) {

                    val greenRedAngleDiff = Colors.Web.green.toHsl().h.rad - Colors.Web.red.toHsl().h.rad

                    println(
                        "rendering card!, gemeentes size: ${this@NederlandVizMap.gemeentes
                            .size}, idColorToGemeente size: ${idColorToGemeente.size}"
                    )
                    // js https://github.com/data2viz/data2viz/blob/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-js/src/main/kotlin/EarthJs.kt
                    // common https://github.com/data2viz/data2viz/tree/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-common/src/main/kotlin

                    fun drawGemeentes() {
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

                        this@NederlandVizMap.gemeentes.forEach {
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
                    drawGemeentes()

                    // 2nd canvas trick http://bl.ocks.org/Jverma/70f7975a72358e6d69cdd4bf6a0569e7
                    fun onHovering(it: Point) {
                        val new = getGemeenteAt(it)

                        if (selectedGemeente != new) {
                            selectedGemeente = new
                            drawGemeentes()
                            render()
                        }
                    }
                    on(KMouseMove) {
                       onHovering(it.pos)
                    }
                    on(KTouchStart) {
                        onHovering(it.pos)
                    }

                    on(KPointerClick) {
                        getGemeenteAt(it.pos)?.let {
                            when (examenlocatieOrOpleider) {
                                EXAMENLOCATIE -> {
                                    setExamenlocatieFilters(it.name)
                                    setOpleiderFilters("")
                                }
                                OPLEIDER -> {
                                    setOpleiderFilters(it.name)
                                    setExamenlocatieFilters("")
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
    gemeente: NederlandVizMap.Gemeente,
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

fun RBuilder.nederlandMap(handler: RElementBuilder<NederlandVizMapProps>.() -> Unit) = child(NederlandVizMap::class) {
    handler()
}
