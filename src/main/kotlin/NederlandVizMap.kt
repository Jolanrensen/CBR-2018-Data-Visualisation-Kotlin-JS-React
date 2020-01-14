import data.Data
import data.Feature
import data.Opleider
import data.toData2Viz
import data2viz.GeoPathNode
import data2viz.vizComponent
import io.data2viz.color.Color
import io.data2viz.color.Colors
import io.data2viz.geo.projection.conicEqualAreaProjection
import io.data2viz.geom.PathGeom
import io.data2viz.geom.Point
import io.data2viz.geom.Polygon
import io.data2viz.geom.contains
import io.data2viz.math.Angle
import io.data2viz.math.deg
import io.data2viz.math.pct
import io.data2viz.viz.KPointerClick
import react.RBuilder
import react.RComponent
import react.RElementBuilder
import react.RProps
import react.RState

interface NederlandMapProps : RProps {
    var alleOpleidersData: Map<String, Opleider>
    var color: Color
    var selectedGemeenteNaam: (String?) -> Unit
}

interface NederlandMapState : RState

class NederlandVizMap(props: NederlandMapProps) : RComponent<NederlandMapProps, NederlandMapState>(props) {

    override fun shouldComponentUpdate(nextProps: NederlandMapProps, nextState: NederlandMapState) =
        props.alleOpleidersData != nextProps.alleOpleidersData

    override fun RBuilder.render() {
        val nederland = Data.geoJson!! // geometry type is polygon/multipolygon for each
        val gemeentesMetHunOpleiders = hashMapOf(
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
        val maxNoOpleiders = gemeentesMetHunOpleiders.values.map { it.size }.max()!!.toDouble()

        vizComponent(
            width = 600.0,
            height = 850.0
        ) {
            println("rendering card!")
            // js https://github.com/data2viz/data2viz/blob/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-js/src/main/kotlin/EarthJs.kt
            // common https://github.com/data2viz/data2viz/tree/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-common/src/main/kotlin

            val polygonToGemeente: HashMap<Polygon, Feature<Data.GemeentesProperties>> = hashMapOf()

            for ((feature, opleiders) in gemeentesMetHunOpleiders) {
                GeoPathNode().apply {
                    stroke = Colors.Web.black
                    strokeWidth = 1.0
                    val greenRedAngleDiff = Colors.Web.green.toHsl().h.rad - Colors.Web.red.toHsl().h.rad

                    fill = Colors.hsl(
                        hue = Angle(opleiders.size.toDouble() / maxNoOpleiders * greenRedAngleDiff),
                        saturation = 100.pct,
                        lightness = 50.pct
                    )

                    geoProjection = conicEqualAreaProjection {
                        scale = 15000.0
                        center(6.5.deg, 52.72.deg)
                    }
                    geoData = feature.toData2Viz()

                    val path = redrawPath()

                    val polygon = Polygon(
                        (path.path!! as PathGeom).commands.map {
                            Point(it.x, it.y)
                        }
                    )
                    polygonToGemeente[polygon] = feature

                    this@vizComponent.add(this)
                }
            }

            circle {
                fill = Colors.rgb(255, 0, 0)
                radius = 10.0
                x = 300.0
                y = 425.0
            }

            on(KPointerClick) {
                val pos = it.pos
                props.selectedGemeenteNaam(
                    polygonToGemeente.entries
                        .firstOrNull { it.key.contains(pos) }
                        ?.value
                        ?.properties
                        ?.statnaam
                )
            }

        }
    }
}

fun RBuilder.nederlandMap(handler: RElementBuilder<NederlandMapProps>.() -> Unit) = child(NederlandVizMap::class) {
    handler(this)
}
