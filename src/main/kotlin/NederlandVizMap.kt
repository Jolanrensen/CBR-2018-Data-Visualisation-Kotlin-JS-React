import data.Data
import data.Feature
import data.Opleider
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
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import react.RBuilder
import react.RComponent
import react.RElementBuilder
import react.RProps
import react.RState
import kotlin.lazy
import kotlin.random.Random

interface NederlandMapProps : RProps {
    var alleOpleidersData: Map<String, Opleider>
    var selectedGemeente: StateAsProp<NederlandVizMap.Gemeente?>
}

interface NederlandMapState : RState

class NederlandVizMap(prps: NederlandMapProps) : RComponent<NederlandMapProps, NederlandMapState>(prps) {


    var selectedGemeente by propDelegateOf(NederlandMapProps::selectedGemeente)
    val alleOpleidersData by readOnlyPropDelegateOf(NederlandMapProps::alleOpleidersData)


    override fun shouldComponentUpdate(nextProps: NederlandMapProps, nextState: NederlandMapState) =
        props.alleOpleidersData != nextProps.alleOpleidersData
                || props.selectedGemeente != nextProps.selectedGemeente

    private val nederland = Data.geoJson!! // geometry type is polygon/multipolygon for each

    data class Gemeente(
        val feature: Feature<Data.GemeentesProperties>,
        val opleiders: List<Opleider> = listOf(),
        val geoPathNode: GeoPathNode,
        val hiddenGeoPathNode: GeoPathNode
    )

    private val idColorToGemeente = hashMapOf<Int, Gemeente>()

    private fun getGemeenteColor(selected: Boolean, opleidersSize: Int?): HslColor {
        val maxNoOpleiders = 470 // den haag
        val greenRedAngleDiff = Colors.Web.green.toHsl().h.rad - Colors.Web.red.toHsl().h.rad

        return Colors.hsl(
            hue = Angle(opleidersSize!!.toDouble() / maxNoOpleiders * greenRedAngleDiff),
            saturation = 100.pct,
            lightness = if (selected) 20.pct else 50.pct
        )
    }

    private val gemeentes: Collection<Gemeente> by lazy {
        println("calculating 'gemeentes'")
        nederland.features.map { feature ->
            val opleiders = alleOpleidersData
                .values
                .filter {
                    it.gemeente.toLowerCase() == feature.properties.statnaam.toLowerCase()
                }

            fun GeoPathNode.runOnNode() {
                stroke = Colors.Web.black
                strokeWidth = 1.0
                geoProjection = conicEqualAreaProjection {
                    scale = 15000.0
                    center(6.5.deg, 52.72.deg)
                }
                geoData = feature.toData2Viz()

                //redrawPath()

                //vectorPath = path.toVectorPath()
            }

            //var vectorPath: VectorPath
            val geoPathNode = GeoPathNode().apply {
                runOnNode()
                fill = getGemeenteColor(false, opleiders.size)
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

            val gemeente = Gemeente(
                feature = feature,
                opleiders = opleiders,
                geoPathNode = geoPathNode,
                hiddenGeoPathNode = hiddenGeoPathNode
            )
            idColorToGemeente[idColor!!.rgb] = gemeente


            gemeente
        }
    }

    override fun RBuilder.render() {
        if (alleOpleidersData.isEmpty()) return

        var hiddenViz: Viz?
        var hiddenCanvas: HTMLCanvasElement? = null
        vizComponent(
            width = 600.0,
            height = 850.0,
            runOnHiddenViz = { it ->
                hiddenCanvas = it
                hiddenViz = this
                gemeentes.forEach {
                    it.hiddenGeoPathNode.redrawPath()
                    hiddenViz!!.add(it.hiddenGeoPathNode)
                }
            }
        ) {

            println("rendering card!, gemeentes size: ${gemeentes.size}, idColorToGemeente size: ${idColorToGemeente.size}")
            // js https://github.com/data2viz/data2viz/blob/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-js/src/main/kotlin/EarthJs.kt
            // common https://github.com/data2viz/data2viz/tree/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-common/src/main/kotlin


            gemeentes.forEach {
                it.geoPathNode.fill = getGemeenteColor(
                        selectedGemeente == it,
                        it.opleiders.size
                    )
                it.geoPathNode.redrawPath()
                add(it.geoPathNode)

            }

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
                val color = Colors.rgb(col[0].toInt(), col[1].toInt(), col[2].toInt()).rgb

                val new = idColorToGemeente[color]

                if (selectedGemeente != new) {
                    selectedGemeente = new
                }
            }
        }
    }
}

fun RBuilder.nederlandMap(handler: RElementBuilder<NederlandMapProps>.() -> Unit) = child(NederlandVizMap::class) {
    handler(this)
}
