import com.ccfraser.muirwik.components.mCircularProgress
import data.Data
import data.ExamenResultaat
import data.Feature
import data.toData2Viz
import data2viz.GeoPathNode
import data2viz.vizComponent
import io.data2viz.color.Color
import io.data2viz.color.Colors
import io.data2viz.color.HslColor
import io.data2viz.geo.projection.conicEqualAreaProjection
import io.data2viz.math.Angle
import io.data2viz.math.deg
import io.data2viz.math.pct
import io.data2viz.viz.KMouseMove
import io.data2viz.viz.Viz
import kotlinx.css.Display
import kotlinx.css.JustifyContent
import kotlinx.css.display
import kotlinx.css.justifyContent
import libs.*
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import react.*
import styled.css
import styled.styledDiv
import kotlin.random.Random

interface NederlandMapProps : RProps {
    var selectedGemeente: StateAsProp<NederlandVizMap.Gemeente?>
    var dataLoaded: Boolean
}

interface NederlandMapState : RState {
    var gemeentes: JsObject<NederlandVizMap.Gemeente>
}

class NederlandVizMap(prps: NederlandMapProps) : RComponent<NederlandMapProps, NederlandMapState>(prps) {

    // maybe change this to setter only
    var selectedGemeenteState by propDelegateOf(NederlandMapProps::selectedGemeente)

    var selectedGemeente: Gemeente? = selectedGemeenteState
        set(value) {
            field = value
            selectedGemeenteState = value
        }

    val dataLoaded by readOnlyPropDelegateOf(NederlandMapProps::dataLoaded)

    override fun NederlandMapState.init(props: NederlandMapProps) {
        gemeentes = jsObjectOf()
    }

    var gemeentes by stateDelegateOf(NederlandMapState::gemeentes)


    override fun shouldComponentUpdate(nextProps: NederlandMapProps, nextState: NederlandMapState) =
        props.dataLoaded != nextProps.dataLoaded
                || state.gemeentes != nextState.gemeentes
    //|| props.selectedGemeente != nextProps.selectedGemeente


    data class Gemeente(
        val feature: Feature<Data.GemeentesProperties>,
        val opleiders: Array<String> = arrayOf(),
        val examenlocaties: Array<String> = arrayOf(),
        val geoPathNode: GeoPathNode,
        val hiddenGeoPathNode: GeoPathNode,
        val slagingspercentage: Double
    ) {
        val name get() = feature.properties.statnaam
    }

    private var idColorToGemeente = jsObjectOf<String>()

    fun getGemeenteColor(selected: Boolean, gemeente: NederlandVizMap.Gemeente): HslColor {
//        val maxNoOpleiders = 470 // den haag
        return if (gemeente.opleiders.isEmpty()) {
            Colors.Web.black.toHsl()
        } else {
            val greenRedAngleDiff = Colors.Web.green.toHsl().h.rad - Colors.Web.red.toHsl().h.rad

            Colors.hsl(
                hue = Angle(gemeente.slagingspercentage * greenRedAngleDiff),
                saturation = 100.pct,
                lightness = if (selected) 20.pct else 50.pct
            )
        }
    }

    private var alreadyCalculatingGemeentes = false

    private fun calculateGemeentes() {
        if (gemeentes.isNotEmpty() || alreadyCalculatingGemeentes) return
        alreadyCalculatingGemeentes = true

//        GlobalScope.launch {

        val nederland = Data.geoJson!! // geometry type is polygon/multipolygon for each

//        if (gemeentes.isNotEmpty() || alreadyCalculatingGemeentes) return@runAsync
//        alreadyCalculatingGemeentes = true
        println("calculating 'gemeentes'")

        console.log(Data.alleOpleiders.entries)

        val gemeentes = jsObjectOf<Gemeente>()

        nederland.features.mapIndexed { index, feature ->
            val opleiders = Data.alleOpleiders.entries
//                    .asSequence()
                .filter {
                    it.second.gemeente.toLowerCase() == feature.properties.statnaam.toLowerCase()
                }
                .map { it.first }

            val examenlocaties = Data.alleExamenlocaties.entries
//                    .asSequence()
                .filter {
                    it.second.gemeente.toLowerCase() == feature.properties.statnaam.toLowerCase()
                }
                .map { it.first }

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
                } while (idColor!!.rgb.toString() in idColorToGemeente)
                fill = idColor
                strokeWidth = null
            }

            val ourResults = Data.opleiderToResultaten
                .entries
                .filter { it.first in opleiders }
                .map { it.second.map { Data.alleResultaten[it] ?: error("") } }
                .flatten()
            val totaalVoldoende = ourResults
                .sumBy {
                    it.examenResultaatAantallen
                        .filter { it.examenResultaat == ExamenResultaat.VOLDOENDE }
                        .sumBy { it.aantal }
                }
            val totaal = ourResults
                .sumBy {
                    it.examenResultaatAantallen
                        .sumBy { it.aantal }
                }

            val gemeente = Gemeente(
                feature = feature,
                opleiders = opleiders.toTypedArray(),
                examenlocaties = examenlocaties.toTypedArray(),
                geoPathNode = geoPathNode,
                hiddenGeoPathNode = hiddenGeoPathNode,
                slagingspercentage = totaalVoldoende.toDouble() / totaal.toDouble()
            )

            geoPathNode.fill = getGemeenteColor(false, gemeente)
            idColorToGemeente[idColor!!.rgb.toString()] = gemeente.name

            gemeente
        }.forEach {
            gemeentes[it.name] = it
        }

        console.log("finished calculating gemeentes: ", idColorToGemeente, gemeentes)
        this@NederlandVizMap.gemeentes = gemeentes
//        }
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
                gemeentes.values.forEach {
                    it.hiddenGeoPathNode.redrawPath()
                    hiddenViz!!.add(it.hiddenGeoPathNode)
                }
            }
        ) {

            println(
                "rendering card!, gemeentes size: ${gemeentes
                    .size}, idColorToGemeente size: ${idColorToGemeente.size}"
            )
            // js https://github.com/data2viz/data2viz/blob/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-js/src/main/kotlin/EarthJs.kt
            // common https://github.com/data2viz/data2viz/tree/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-common/src/main/kotlin

            fun drawGemeentes() {
                clear()
                gemeentes.values.forEach {
                    it.geoPathNode.fill = getGemeenteColor(
                        selectedGemeente == it,
                        it
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
            on(KMouseMove) {
                val pos = it.pos
                val context = hiddenCanvas!!.getContext("2d") as CanvasRenderingContext2D
                val col = context
                    .getImageData(pos.x, pos.y, 1.0, 1.0)
                    .data
                val color = Colors.rgb(col[0].toInt(), col[1].toInt(), col[2].toInt()).rgb.toString()

                val new = gemeentes[idColorToGemeente[color]]

                if (selectedGemeente != new) {
                    selectedGemeente = new
                    drawGemeentes()
                    render()
                }
            }
        }
    }
}


fun RBuilder.nederlandMap(handler: RElementBuilder<NederlandMapProps>.() -> Unit) = child(NederlandVizMap::class) {
    handler()
}
