import com.soywiz.korma.geom.Point2d
import com.soywiz.korma.geom.VectorPath
import com.soywiz.korma.geom.shape.toShape2d
import com.soywiz.korma.geom.shape.triangulate
import com.soywiz.korma.geom.triangle.SpatialMesh
import com.soywiz.korma.geom.triangle.Triangle
import data.Data
import data.Feature
import data.Opleider
import data.toData2Viz
import data2viz.GeoPathNode
import data2viz.vizComponent
import io.data2viz.color.Colors
import io.data2viz.color.HslColor
import io.data2viz.geo.projection.conicEqualAreaProjection
import io.data2viz.math.Angle
import io.data2viz.math.deg
import io.data2viz.math.pct
import io.data2viz.viz.KPointerClick
import libs.toVectorPath
import react.RBuilder
import react.RComponent
import react.RElementBuilder
import react.RProps
import react.RState
import kotlin.lazy

interface NederlandMapProps : RProps {
    var alleOpleidersData: Map<String, Opleider>

    var selectedGemeentenaam: StateAsProp<String>
    // var sele: StateDelegate<String> this can work

    // var sele: StateDelegate<String>
    // var setSele: (String) -> Unit
}

interface NederlandMapState : RState

class NederlandVizMap(prps: NederlandMapProps) : RComponent<NederlandMapProps, NederlandMapState>(prps) {

    // this works
    // var sele: String
    //     get() = props.selectedGemeentenaam.get()
    //     set(value) = props.selectedGemeentenaam.set(value)

    // dit dus niet, want props kan veranderen
    var selectedGemeentenaam by propDelegateOf(NederlandMapProps::selectedGemeentenaam)

    val alleOpleidersData by readOnlyPropDelegateOf(NederlandMapProps::alleOpleidersData)

    // equivalent to
    // val alleOpleidersData get() = props.alleOpleidersData

    override fun shouldComponentUpdate(nextProps: NederlandMapProps, nextState: NederlandMapState): Boolean {
//        console.log("shouldCompoentUpdate newProps:\n", nextProps, "\ncurrent props:\n", props)
        println(
            "selectedGemeentenaam: ${props.selectedGemeentenaam.value}, nextProps: ${nextProps.selectedGemeentenaam
                .value}"
        )
        return props.alleOpleidersData != nextProps.alleOpleidersData
                || props.selectedGemeentenaam != nextProps.selectedGemeentenaam
    }

    val nederland = Data.geoJson!! // geometry type is polygon/multipolygon for each

    private data class Gemeente(
        val feature: Feature<Data.GemeentesProperties>,
        val opleiders: List<Opleider> = listOf(),
        val vectorPath: VectorPath,
        val geoPathNode: GeoPathNode
    )

    private fun getGemeenteColor(selected: Boolean, opleidersSize: Int): HslColor {
        val maxNoOpleiders = 470 // den haag
        val greenRedAngleDiff = Colors.Web.green.toHsl().h.rad - Colors.Web.red.toHsl().h.rad

        return Colors.hsl(
            hue = Angle(opleidersSize.toDouble() / maxNoOpleiders * greenRedAngleDiff),
            saturation = 100.pct,
            lightness = if (selected) 20.pct else 50.pct
        )
    }

    private val triangleToGemeente = hashMapOf<Triangle, Gemeente>()
    private val mesh = SpatialMesh.fromTriangles(triangleToGemeente.keys)

    private val gemeentes: Collection<Gemeente> by lazy {
        println("calculating 'gemeentes'")
        val result = nederland.features.map { feature ->
            val opleiders = alleOpleidersData
                .values
                .filter {
                    it.gemeente.toLowerCase() == feature.properties.statnaam.toLowerCase()
                }

            var vectorPath: VectorPath
            val geoPathNode = GeoPathNode().apply {
                stroke = Colors.Web.black
                strokeWidth = 1.0

                fill = getGemeenteColor(false, opleiders.size)

                geoProjection = conicEqualAreaProjection {
                    scale = 15000.0
                    center(6.5.deg, 52.72.deg)
                }
                geoData = feature.toData2Viz()

                val path = redrawPath()

                vectorPath = path.toVectorPath()
            }

            val gemeente = Gemeente(
                feature = feature,
                opleiders = opleiders,
                vectorPath = vectorPath,
                geoPathNode = geoPathNode
            )
            vectorPath.toShape2d().triangulate().forEach { triangleToGemeente[it] = gemeente }
            gemeente
        }

        result
    }

    override fun RBuilder.render() {
        if (alleOpleidersData.isEmpty()) return

        println(
            "selected gemeente received as: sele=${selectedGemeentenaam}, props.sele=${props.selectedGemeentenaam
                .value}"
        )


        vizComponent(
            width = 600.0,
            height = 850.0
        ) {
            println("rendering card!")
            // js https://github.com/data2viz/data2viz/blob/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-js/src/main/kotlin/EarthJs.kt
            // common https://github.com/data2viz/data2viz/tree/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-common/src/main/kotlin


            gemeentes.forEach {
                it.geoPathNode.fill =
                    getGemeenteColor(
                        it.feature.properties.statnaam.toLowerCase() == selectedGemeentenaam.toLowerCase(),
                        it.opleiders.size
                    )

                add(it.geoPathNode)
            }

            circle {
                fill = Colors.rgb(255, 0, 0)
                radius = 10.0
                x = 300.0
                y = 425.0
            }

            on(KPointerClick) {
                val pos = it.pos
                selectedGemeentenaam = triangleToGemeente[
                        mesh.getNodeAt(
                            Point2d(pos.x, pos.y)
                        )!!.triangle
                ]
                    ?.feature
                    ?.properties
                    ?.statnaam
                    ?: "-"

            }
        }
    }
}

fun RBuilder.nederlandMap(handler: RElementBuilder<NederlandMapProps>.() -> Unit) = child(NederlandVizMap::class) {
    handler(this)
}
