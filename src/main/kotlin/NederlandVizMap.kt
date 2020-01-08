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
                                opleider.gemeente.contains(
                                    gemeente.properties.statnaam,
                                    true
                                ) ||
                                    gemeente.properties.statnaam.contains(
                                        opleider.gemeente,
                                        true
                                    )
                            }.toTypedArray()
                    )
                }.toTypedArray()
            )

            // println(rijdingen.map { it.key.properties.statnaam })

            // val opleiders = rijdingen.values.flatten()
            // println(props.alleOpleidersData.values.filter { it !in opleiders }.map { it.gemeente }.toSet())

            val maxNoOpleiders = rijdingen.values.map { it.size }.max()!!.toDouble()

            for ((feature, opleiders) in rijdingen) {
                GeoPathNode().apply {
                    stroke = Colors.Web.black
                    strokeWidth = 1.0
                    fill = Colors.Web.black
                        .withGreen((opleiders.size.toDouble() / maxNoOpleiders * 255.0).toInt())
                        .withRed((255.0 / (opleiders.size.toDouble() / maxNoOpleiders * 255.0)).toInt())

                    geoProjection = conicEqualAreaProjection {
                        scale = 15000.0
                        center(6.5.deg, 52.72.deg)
                    }
                    geoData = feature.toData2Viz()
                    redrawPath()
                    this@vizComponent.add(this)
                }
            }

            // circle {
            //     fill = Colors.rgb(255, 0, 0)
            //     radius = 10.0
            //     x = 300.0
            //     y = 425.0
            // }

            // on(KPointerClick) {
            //     println("Pointer click:: ${it.pos}")
            // }

        }
    }
}

fun RBuilder.nederlandMap(handler: RElementBuilder<NederlandMapProps>.() -> Unit) = child(NederlandVizMap::class) {
    handler(this)
}
