package filterableLists

import data.Categorie
import data.Data
import data.Examenlocatie
import data.Opleider
import data2viz.vizComponent
import delegates.ReactPropAndStateDelegates
import delegates.ReactPropAndStateDelegates.StateAsProp
import delegates.ReactPropAndStateDelegates.propDelegateOf
import io.data2viz.color.Colors
import io.data2viz.geom.size
import io.data2viz.shape.ArcBuilder
import io.data2viz.shape.arcBuilder
import io.data2viz.shape.pie
import io.data2viz.shape.tau
import libs.RPureComponent
import react.RBuilder
import react.RElementBuilder
import react.RProps
import react.RState

interface CategoriePieChartProps : RProps {
    var opleider: Opleider?
    var examenlocatie: Examenlocatie?
    var selectedCategorie: StateAsProp<Categorie?>
    var onCategorieClicked: (Categorie) -> Unit
}

interface CategoriePieChartState : RState

class CategoriePieChart(prps: CategoriePieChartProps) :
    RPureComponent<CategoriePieChartProps, CategoriePieChartState>(prps) {

    val opleider by propDelegateOf(CategoriePieChartProps::opleider)
    val examenlocatie by propDelegateOf(CategoriePieChartProps::examenlocatie)
    val onCategorieClicked by propDelegateOf(CategoriePieChartProps::onCategorieClicked)

    // state in upper component
    var selectedCategorie by propDelegateOf(CategoriePieChartProps::selectedCategorie)


    override fun RBuilder.render() {
        // we can only have one of the two
        if (opleider == null && examenlocatie == null || opleider != null && examenlocatie != null) return

        val width = 500.0
        val height = 200.0
        vizComponent(
            width = width,
            height = height
        ) {
            val margin = 20.0
            val radius = height / 2.0 - margin

            val categorieCount = Categorie.values().map { it to 0 }.toMap().toMutableMap()

            val resultaten =
                if (opleider != null) Data.opleiderToResultaten[opleider!!.code]!!
                else Data.examenlocatieToResultaten[examenlocatie!!.naam]!!

            for (resultaat in resultaten) {
                categorieCount[resultaat.categorie] = categorieCount[resultaat.categorie]!! +
                        resultaat.examenResultaatAantallen.sumBy { it.aantal }
            }

            val totalCount = categorieCount.values.sum().toDouble()

            val topX = categorieCount
                .asSequence()
                .sortedByDescending { it.value }
                .take(6)

            val overig = "Overig" to categorieCount.asSequence().sumBy {
                if (it in topX) 0 else it.value
            }

            val categorieList = (
                    topX.map { (key, value) ->
                        "${key.name}: ${key.omschrijving}"
                            .let { // trim if too long
                                if (it.length > 35) it.take(35) + "..."
                                else it
                            } to value
                    } + overig
                    )
                .toList()
                .toTypedArray()

            // follow https://www.d3-graph-gallery.com/graph/pie_basic.html
            val arcParams = pie<Pair<String, Int>> {
                value = { (it.second.toDouble() / totalCount) * tau }
            }.render(categorieList)


            val colorOf = { categorie: Pair<String, Int> ->
                listOf(
                    Colors.Web.purple,
                    Colors.Web.navy,
                    Colors.Web.teal,
                    Colors.Web.lime,
                    Colors.Web.yellow,
                    Colors.Web.red,
                    Colors.Web.gray
                )[categorieList.indexOf(categorie)]
            }

            val arcBuilder: ArcBuilder<Pair<String, Int>> = arcBuilder {
                startAngle = { data ->
                    arcParams.find { it.data == data }!!.startAngle
                }
                endAngle = { data ->
                    arcParams.find { it.data == data }!!.endAngle
                }
                padAngle = { data ->
                    arcParams.find { it.data == data }!!.padAngle ?: 0.0
                }
                outerRadius = { radius }
            }

            group {
                transform {
                    translate(x = width / 6.0 + margin, y = height / 2.0)
                }
                arcParams.forEach {
                    arcBuilder.buildArcForDatum(it.data!!, path {
                        fill = colorOf(it.data!!)
                        stroke = Colors.Web.black
                        strokeWidth = 2.0
                    })
                }
            }

            // legenda
            group {
                transform {
                    translate(x = width / 3.0 + 2.0 * margin, y = margin)
                }

                categorieList.filter { it.second > 0 }
                    .forEachIndexed { index, data ->
                        group {
                            transform {
                                translate(y = index * 15.0)
                            }
                            rect {
                                size = size(10.0, 10.0)
                                fill = colorOf(data)
                            }
                            group {
                                transform {
                                    translate(x = 15.0, y = 9.0)
                                }
                                text {
                                    textContent = data.first
                                }
                            }
                        }
                    }
            }
        }
    }
}

fun RBuilder.categoriePieChart(handler: RElementBuilder<CategoriePieChartProps>.() -> Unit) =
    child(CategoriePieChart::class, handler)