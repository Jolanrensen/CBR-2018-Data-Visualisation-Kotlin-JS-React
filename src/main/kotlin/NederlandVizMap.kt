import ExamenlocatieOrOpleider.EXAMENLOCATIE
import ExamenlocatieOrOpleider.OPLEIDER
import com.ccfraser.muirwik.components.mCircularProgress
import data.*
import data2viz.GeoPathNode
import data2viz.vizComponent
import io.data2viz.color.Color
import io.data2viz.color.Colors
import io.data2viz.color.HslColor
import io.data2viz.geo.projection.conicEqualAreaProjection
import io.data2viz.math.Angle
import io.data2viz.math.deg
import io.data2viz.math.pct
import io.data2viz.viz.KPointerClick
import io.data2viz.viz.Viz
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
}

interface NederlandVizMapState : RState {
    var gemeentes: List<NederlandVizMap.Gemeente>
}

class NederlandVizMap(prps: NederlandVizMapProps) : RComponent<NederlandVizMapProps, NederlandVizMapState>(prps) {

    // maybe change this to setter only
    var selectedGemeenteState by propDelegateOf(NederlandVizMapProps::selectedGemeente)
    val examenlocatieOrOpleider by readOnlyPropDelegateOf(NederlandVizMapProps::examenlocatieOrOpleider)

    var selectedGemeente: Gemeente? = selectedGemeenteState
        set(value) {
            field = value
            selectedGemeenteState = value
        }

    val dataLoaded by readOnlyPropDelegateOf(NederlandVizMapProps::dataLoaded)

    override fun NederlandVizMapState.init(props: NederlandVizMapProps) {
        gemeentes = listOf()
    }

    var gemeentes by stateDelegateOf(NederlandVizMapState::gemeentes)

    override fun shouldComponentUpdate(nextProps: NederlandVizMapProps, nextState: NederlandVizMapState) =
        props.dataLoaded != nextProps.dataLoaded
                || state.gemeentes != nextState.gemeentes
                || props.examenlocatieOrOpleider != nextProps.examenlocatieOrOpleider

    private val nederland = Data.geoJson!! // geometry type is polygon/multipolygon for each

    data class Gemeente(
        val feature: Feature<Data.GemeentesProperties>,
        val opleiders: Collection<Opleider> = setOf(),
        val examenlocaties: Collection<Examenlocatie> = setOf(),
        val geoPathNode: GeoPathNode,
        val hiddenGeoPathNode: GeoPathNode,
        val slagingspercentageOpleiders: Double,
        val slagingspercentageExamenlocaties: Double
    ) {
        val name get() = feature.properties.statnaam
    }

    private val idColorToGemeente = hashMapOf<Int, Gemeente>()

    private var alreadyCalculatingGemeentes = false
    private fun calculateGemeentes() {
        if (gemeentes.isNotEmpty() || alreadyCalculatingGemeentes) return

        runOnWorker {
            if (gemeentes.isNotEmpty() || alreadyCalculatingGemeentes) return@runOnWorker
            alreadyCalculatingGemeentes = true
            println("calculating 'gemeentes'")

            gemeentes = nederland.features.mapIndexed { index, feature ->
                val opleiderCodes = hashSetOf<String>()
                val opleiders = Data.alleOpleiders
                    .asSequence()
                    .filter {
                        it.value.gemeente.toLowerCase() == feature.properties.statnaam.toLowerCase()
                    }
                    .apply { opleiderCodes += map { it.key } }
                    .map { it.value }
                    .toSet()

                val examenlocatieCodes = hashSetOf<String>()
                val examenlocaties = Data.alleExamenlocaties
                    .asSequence()
                    .filter {
                        it.value.gemeente.toLowerCase() == feature.properties.statnaam.toLowerCase()
                    }
                    .apply { examenlocatieCodes += map { it.key } }
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

                val ourOpleiderResults = Data.opleiderToResultaten
                    .asSequence()
                    .filter { it.key in opleiderCodes }
                    .map { it.value }
                    .flatten()
                val totaalVoldoendeOpleiders = ourOpleiderResults
                    .sumBy {
                        it.examenResultaatAantallen
                            .filter { it.examenResultaat == ExamenResultaat.VOLDOENDE }
                            .sumBy { it.aantal }
                    }
                val totaalOpleiders = ourOpleiderResults
                    .sumBy {
                        it.examenResultaatAantallen
                            .sumBy { it.aantal }
                    }

                val ourExamenlocatieResults = Data.examenlocatieToResultaten
                    .asSequence()
                    .filter { it.key in examenlocatieCodes }
                    .map { it.value }
                    .flatten()
                val totaalVoldoendeExamenlocaties = ourExamenlocatieResults
                    .sumBy {
                        it.examenResultaatAantallen
                            .filter { it.examenResultaat == ExamenResultaat.VOLDOENDE }
                            .sumBy { it.aantal }
                    }
                val totaalExamenlocaties = ourExamenlocatieResults
                    .sumBy {
                        it.examenResultaatAantallen
                            .sumBy { it.aantal }
                    }

                val gemeente = Gemeente(
                    feature = feature,
                    opleiders = opleiders,
                    examenlocaties = examenlocaties,
                    geoPathNode = geoPathNode,
                    hiddenGeoPathNode = hiddenGeoPathNode,
                    slagingspercentageOpleiders = totaalVoldoendeOpleiders.toDouble() / totaalOpleiders.toDouble(),
                    slagingspercentageExamenlocaties = totaalVoldoendeExamenlocaties.toDouble() / totaalExamenlocaties.toDouble()
                )

//                geoPathNode.fill = getGemeenteColor(false, gemeente)
                idColorToGemeente[idColor!!.rgb] = gemeente

//            println("${index.toDouble() / nederland.features.size.toDouble() * 100}%")
                gemeente
            }
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
        if (gemeentes.isEmpty()) return {
            showLoading()
            calculateGemeentes()
        }()


        var hiddenViz: Viz?
        var hiddenCanvas: HTMLCanvasElement? = null
        vizComponent(
            width = 600.0,
            height = 850.0,
            runOnHiddenViz = { it ->
                hiddenCanvas = it
                hiddenViz = this
                this@NederlandVizMap.gemeentes.forEach {
                    it.hiddenGeoPathNode.redrawPath()
                    hiddenViz!!.add(it.hiddenGeoPathNode)
                }
            }
        ) {

            println(
                "rendering card!, gemeentes size: ${this@NederlandVizMap.gemeentes
                    .size}, idColorToGemeente size: ${idColorToGemeente.size}"
            )
            // js https://github.com/data2viz/data2viz/blob/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-js/src/main/kotlin/EarthJs.kt
            // common https://github.com/data2viz/data2viz/tree/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-common/src/main/kotlin

            fun drawGemeentes() {
                clear()
                this@NederlandVizMap.gemeentes.forEach {
                    it.geoPathNode.fill = getGemeenteColor(
                        selectedGemeente == it,
                        it,
                        examenlocatieOrOpleider
                    )
                    it.geoPathNode.redrawPath()
                    add(it.geoPathNode)
                }
            }
            drawGemeentes()

//            circle {
//                fill = Colors.rgb(255, 0, 0)
//                radius = 10.0
//                x = 300.0
//                y = 425.0
//            }

            // 2nd canvas trick http://bl.ocks.org/Jverma/70f7975a72358e6d69cdd4bf6a0569e7
            on(KPointerClick) {
                val pos = it.pos
                val context = hiddenCanvas!!.getContext("2d") as CanvasRenderingContext2D
                val col = context
                    .getImageData(pos.x, pos.y, 1.0, 1.0)
                    .data
                val color = Colors.rgb(col[0].toInt(), col[1].toInt(), col[2].toInt()).rgb

                val new = idColorToGemeente[color]

                if (selectedGemeente != new) {
                    selectedGemeente = new
                    drawGemeentes()
                    render()
                }
            }
        }
    }
}

fun getGemeenteColor(
    selected: Boolean,
    gemeente: NederlandVizMap.Gemeente,
    examenlocatieOrOpleider: ExamenlocatieOrOpleider
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
                    OPLEIDER -> gemeente.slagingspercentageOpleiders
                    EXAMENLOCATIE -> gemeente.slagingspercentageExamenlocaties
                } * greenRedAngleDiff
            ),
            saturation = 100.pct,
            lightness = if (selected) 20.pct else 50.pct
        )
    }

fun RBuilder.nederlandMap(handler: RElementBuilder<NederlandVizMapProps>.() -> Unit) = child(NederlandVizMap::class) {
    handler()
}
