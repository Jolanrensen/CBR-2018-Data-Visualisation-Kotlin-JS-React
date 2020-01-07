
package data

import io.data2viz.geojson.GeoJsonObject
import io.data2viz.geojson.Geometry
import io.data2viz.geojson.GeometryCollection
import io.data2viz.geojson.LineString
import io.data2viz.geojson.Lines
import io.data2viz.geojson.MultiLineString
import io.data2viz.geojson.MultiPoint
import io.data2viz.geojson.MultiPolygon
import io.data2viz.geojson.Point
import io.data2viz.geojson.Polygon
import io.data2viz.geojson.Position
import io.data2viz.geojson.Positions
import io.data2viz.geojson.Surface
import io.data2viz.geojson.js.Typed


@Suppress("UNCHECKED_CAST")
fun <Properties : Any> String.toFeatureCollection(): FeatureCollection<Properties>? =
    JSON.parse<Typed>(this).asGeoJsonObject<Properties>() as? FeatureCollection<Properties>

data class FeatureCollection<Properties : Any>(val features: Array<Feature<Properties>>) : GeoJsonObject

data class Feature<Properties>(
    val geometry: Geometry,
    val id: String? = null,
    val properties: Properties
) : GeoJsonObject

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
fun <Properties : Any> Typed.asGeoJsonObject(): GeoJsonObject =
    when (type) {
        "Point" -> Point(asDynamic().coordinates as Position)
        "MultiPoint" -> MultiPoint(asDynamic().coordinates as Positions)
        "LineString" -> LineString(asDynamic().coordinates as Positions)
        "MultiLineString" -> MultiLineString(asDynamic().coordinates as Lines)
        "Polygon" -> Polygon(asDynamic().coordinates as Lines)
        "MultiPolygon" -> MultiPolygon(asDynamic().coordinates as Surface)
        "GeometryCollection" -> {
            val types = asDynamic().geometries as Array<Typed>
            val geometries = types.map { it.asGeoJsonObject<Properties>() as Geometry }.toTypedArray()
            GeometryCollection(geometries)
        }
        "Feature" -> {
            val geometry = asDynamic().geometry as Typed
            Feature(
                geometry = geometry.asGeoJsonObject<Properties>() as Geometry,
                id = asDynamic().id as? String,
                properties = asDynamic().properties as Properties
            )
        }
        "FeatureCollection" -> asFeatureCollection<Properties>()
        else -> throw IllegalStateException("$type is not known")
    }

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
private fun <Properties : Any> Typed.asFeatureCollection(): FeatureCollection<Properties> {
    val dyn: dynamic = this
    val featureJs: dynamic = dyn.features
    val features: dynamic = Array<Geometry>(0) { Point(arrayOf()) }
    val size: Int = featureJs.length as Int
    for (i in 0 until size) {
        val feature = featureJs[i]
        val typed: Typed = feature.geometry as Typed
        features[i] = Feature(
            typed.asGeoJsonObject<Properties>() as Geometry,
            feature.id as? String,
            feature.properties as Properties
        )
    }
    return FeatureCollection(features as Array<Feature<Properties>>)
}