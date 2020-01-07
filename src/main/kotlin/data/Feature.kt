
package data

import io.data2viz.geojson.GeoJsonObject
import io.data2viz.geojson.Geometry
import io.data2viz.geojson.GeometryCollection
import io.data2viz.geojson.LineString
import io.data2viz.geojson.MultiLineString
import io.data2viz.geojson.MultiPoint
import io.data2viz.geojson.MultiPolygon
import io.data2viz.geojson.Point
import io.data2viz.geojson.Polygon
import io.data2viz.geojson.js.Typed

// @Suppress("UNCHECKED_CAST")
fun <Properties : Any> String.toFeatureCollection(): FeatureCollection<Properties>? =
    JSON.parse<Typed>(this).asGeoJsonObject<Properties>().asDynamic()

data class FeatureCollection<Properties : Any>(val features: Array<Feature<Properties>>) : GeoJsonObject

fun <Properties: Any> FeatureCollection<Properties>.toData2Viz() = io.data2viz.geojson.FeatureCollection(
    features.map { it.toData2Viz() }.toTypedArray()
)

data class Feature<Properties>(
    val geometry: Geometry,
    val id: String? = null,
    val properties: Properties
) : GeoJsonObject

fun <Properties: Any> Feature<Properties>.toData2Viz() = io.data2viz.geojson.Feature(geometry, id)

// @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
fun <Properties : Any> Typed.asGeoJsonObject(): GeoJsonObject =
    when (type) {
        "Point" -> Point(asDynamic().coordinates)
        "MultiPoint" -> MultiPoint(asDynamic().coordinates)
        "LineString" -> LineString(asDynamic().coordinates)
        "MultiLineString" -> MultiLineString(asDynamic().coordinates)
        "Polygon" -> Polygon(asDynamic().coordinates)
        "MultiPolygon" -> MultiPolygon(asDynamic().coordinates)
        "GeometryCollection" -> {
            val types: Array<Typed> = asDynamic().geometries
            val geometries = types.map { it.asGeoJsonObject<Properties>() as Geometry }.toTypedArray()
            GeometryCollection(geometries)
        }
        "Feature" -> {
            val geometry: Typed = asDynamic().geometry
            Feature(
                geometry = geometry.asGeoJsonObject<Properties>() as Geometry,
                id = asDynamic().id,
                properties = asDynamic().properties
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
        val typed: Typed = feature.geometry
        features[i] = Feature(
            geometry = typed.asGeoJsonObject<Properties>() as Geometry,
            id = feature.id,
            properties = feature.properties
        )
    }
    return FeatureCollection(features)
}