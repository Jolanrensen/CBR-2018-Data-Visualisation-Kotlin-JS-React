
import data.Data
import data.Opleider
import data.toData2Viz
import data2viz.GeoPathNode
import data2viz.vizComponent
import io.data2viz.color.Color
import io.data2viz.color.Colors
import io.data2viz.geo.projection.conicEqualAreaProjection
import io.data2viz.math.deg
import libs.RPureComponent
import react.RBuilder
import react.RElementBuilder
import react.RProps
import react.RState

interface NederlandMapProps : RProps {
    var alleOpleidersData: Map<String, Opleider>
    var color: Color
}

interface NederlandMapState : RState

class NederlandVizMap(props: NederlandMapProps) : RPureComponent<NederlandMapProps, NederlandMapState>(props) {

    override fun RBuilder.render() {
        vizComponent(
            width = 600.0,
            height = 850.0
        ) {
            println("rendering card!")
            // js https://github.com/data2viz/data2viz/blob/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-js/src/main/kotlin/EarthJs.kt
            // common https://github.com/data2viz/data2viz/tree/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-common/src/main/kotlin

            val nederland = Data.geoJson!! // geometry type is polygon/multipolygon for each
            val rijdingen = hashMapOf(
                *nederland.features.map { gemeente ->
                    gemeente to hashSetOf(
                        *props.alleOpleidersData
                            .values
                            .filter { opleider ->
                                opleider.plaatsnaam.contains(
                                    gemeente.properties.statnaam,
                                    true
                                )
                            }.toTypedArray()
                    )
                }.toTypedArray()
            )

            val opleiders = rijdingen.values.flatten()
            println(props.alleOpleidersData.values.filter { it !in opleiders }.map { it.plaatsnaam })

            for ((feature, opleiders) in rijdingen) {
                GeoPathNode().apply {
                    stroke = Colors.Web.black
                    strokeWidth = 1.0
                    fill = if (opleiders.isEmpty()) Colors.Web.white else Colors.Web.blue
                    geoProjection = conicEqualAreaProjection {
                        scale = 15000.0
                        center(6.5.deg, 52.72.deg)
                    }
                    geoData = feature.toData2Viz()
                    redrawPath()
                    this@vizComponent.add(this)
                }
            }

            // val geoPathNode = GeoPathNode().apply {
            //     stroke = Colors.Web.black
            //     strokeWidth = 1.0
            //     fill = Colors.Web.whitesmoke
            //     geoProjection = conicEqualAreaProjection {
            //         scale = 15000.0
            //         center(6.5.deg, 52.72.deg)
            //     }
            //     geoData = nederland.toData2Viz()
            //     redrawPath()
            // }
            //
            // add(geoPathNode)
            //
            // val bredaNode = GeoPathNode().apply {
            //     stroke = Colors.Web.black
            //     strokeWidth = 1.0
            //     fill = state.circleColor
            //     geoProjection = conicEqualAreaProjection {
            //         scale = 15000.0
            //         center(6.5.deg, 52.72.deg)
            //     }
            //     val breda = FeatureCollection(
            //         nederland.features.filter { it.properties.statnaam.contains("breda", true) }
            //             .map { it.toData2Viz() }
            //             .toTypedArray()
            //     )
            //     geoData = breda
            //     redrawPath()
            // }
            //
            // add(bredaNode)

            circle {
                fill = Colors.rgb(255, 0, 0)
                radius = 10.0
                x = 300.0
                y = 425.0
            }

            // on(KPointerClick) {
            //     println("Pointer click:: ${it.pos}")
            // }

            // geoPathNode.redrawPath()
            // bredaNode.redrawPath()
        }
    }
}

fun RBuilder.nederlandMap(handler: RElementBuilder<NederlandMapProps>.() -> Unit) = child(NederlandVizMap::class) {
    handler(this)
}
