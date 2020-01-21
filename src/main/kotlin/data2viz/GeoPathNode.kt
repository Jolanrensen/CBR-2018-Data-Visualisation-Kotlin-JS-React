package data2viz

import io.data2viz.geo.geojson.GeoPath
import io.data2viz.geo.geojson.geoPath
import io.data2viz.geo.projection.common.Projection
import io.data2viz.geo.projection.identityProjection
import io.data2viz.geojson.GeoJsonObject
import io.data2viz.geom.PathGeom
import io.data2viz.viz.PathNode

open class GeoPathNode(
    var geoData: GeoJsonObject? = null,
    var geoProjection: Projection = identityProjection(),
    path: PathGeom = PathGeom()
): PathNode(path) {

    fun redrawPath(): PathGeom {
        val geoPath = geoPath(geoProjection, path)
        clearPath()
        geoPath.project(geoData!!)
        return geoPath.path!! as PathGeom;
    }
}