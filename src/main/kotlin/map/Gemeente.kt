package map

import data.Data
import data.Examenlocatie
import data.Feature
import data.Opleider
import data2viz.GeoPathNode

class Gemeente(
    val feature: Feature<Data.GemeentesProperties>,
    val opleiders: Collection<String> = setOf(),
    val examenlocaties: Collection<String> = setOf(),
    val geoPathNode: GeoPathNode,
    val hiddenGeoPathNode: GeoPathNode,

    val slagingspercentageEersteKeerOpleiders: Double,
    val slagingspercentageHerexamenOpleiders: Double,
    val slagingspercentageGecombineerdOpleiders: Double,

    val slagingspercentageEersteKeerExamenlocaties: Double,
    val slagingspercentageHerexamenExamenlocaties: Double,
    val slagingspercentageGecombineerdExamenlocaties: Double
) {
    val name get() = feature.properties.statnaam
}