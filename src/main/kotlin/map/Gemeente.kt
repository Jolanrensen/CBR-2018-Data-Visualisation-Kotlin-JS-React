package map

import data.Data
import data.Examenlocatie
import data.Feature
import data.Opleider
import data2viz.GeoPathNode

data class Gemeente(
    val feature: Feature<Data.GemeentesProperties>,
    val opleiders: Collection<Opleider> = setOf(),
    val examenlocaties: Collection<Examenlocatie> = setOf(),
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

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}