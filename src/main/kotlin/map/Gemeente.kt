package map

import data.Data
import data.Examenlocatie
import data.Feature
import data.Opleider
import data2viz.GeoPathNode

class Gemeente(
    val feature: Feature<Data.GemeentesProperties>,
    val opleiders: Set<String> = setOf(),
    val examenlocaties: Set<String> = setOf(),
    val geoPathNode: GeoPathNode,
    val hiddenGeoPathNode: GeoPathNode,

    var percentageCache: Double? = null,

    val slagingspercentageEersteKeerOpleiders: Double,
    val slagingspercentageHerexamenOpleiders: Double,
    val slagingspercentageGecombineerdOpleiders: Double,

    val slagingspercentageEersteKeerExamenlocaties: Double,
    val slagingspercentageHerexamenExamenlocaties: Double,
    val slagingspercentageGecombineerdExamenlocaties: Double
) {
    val name get() = feature.properties.statnaam
}