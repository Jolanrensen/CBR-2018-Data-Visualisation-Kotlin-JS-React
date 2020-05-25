package filterableLists

import data.Categorie
import data.Data
import data.Examenlocatie
import data.Opleider
import data2viz.vizComponent
import delegates.ReactPropAndStateDelegates.propDelegateOf
import delegates.ReactPropAndStateDelegates.stateDelegateOf
import io.data2viz.color.Colors
import io.data2viz.geom.Point
import io.data2viz.geom.size
import io.data2viz.shape.ArcBuilder
import io.data2viz.shape.arcBuilder
import io.data2viz.shape.pie
import io.data2viz.shape.tau
import io.data2viz.viz.*
import io.data2viz.viz.FontWeight.BOLD
import io.data2viz.viz.FontWeight.NORMAL
import libs.RPureComponent
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import react.RBuilder
import react.RElementBuilder
import react.RProps
import react.RState

interface CategoriePieChartProps : RProps {
    var opleider: Opleider?
    var examenlocatie: Examenlocatie?
    var onCategorieClicked: (Categorie) -> Unit
}

interface CategoriePieChartState : RState {
    var selectedCategorie: Categorie?
}

typealias DataTriple = Triple<Categorie?, String, Int>

class CategoriePieChart(prps: CategoriePieChartProps) :
    RPureComponent<CategoriePieChartProps, CategoriePieChartState>(prps) {

    val opleider by propDelegateOf(CategoriePieChartProps::opleider)
    val examenlocatie by propDelegateOf(CategoriePieChartProps::examenlocatie)
    val onCategorieClicked by propDelegateOf(CategoriePieChartProps::onCategorieClicked)

    // state in upper component
    var selectedCategorie by stateDelegateOf(CategoriePieChartState::selectedCategorie)


    override fun RBuilder.render() {
        // we can only have one of the two
        if (opleider == null && examenlocatie == null || opleider != null && examenlocatie != null) return

        val width = 500.0
        val height = 200.0
        vizComponent(
            width = width,
            height = height
        ) { canvas ->
            val margin = 20.0
            val radius = height / 2.0 - margin

            val categorieCount = Categorie.values().map { it to 0 }.toMap().toMutableMap()

            val resultaten =
                if (opleider != null) Data.opleiderToResultaten[opleider!!.code]!!
                else Data.examenlocatieToResultaten[examenlocatie!!.naam]!!

            for (resultaatId in resultaten) {
                val resultaat = Data.alleResultaten[resultaatId]!!
                categorieCount[resultaat.categorie] = categorieCount[resultaat.categorie]!! +
                        resultaat.examenresultaatAantallen.sumBy { it.aantal }
            }

            val totalCount = categorieCount.values.sum().toDouble()

            val topX = categorieCount
                .asSequence()
                .sortedByDescending { it.value }
                .take(6)

            val overig = DataTriple(
                first = null,
                second = "Overig",
                third = categorieCount.asSequence().sumBy {
                    if (it in topX) 0 else it.value
                }
            )

            val categorieList = (
                    topX.map { (key, value) ->
                        DataTriple(
                            first = key,
                            second = "${key.name}: ${key.omschrijving}"
                                .let { // trim if too long
                                    if (it.length > 35) it.take(35) + "..."
                                    else it
                                },
                            third = value
                        )
                    } + overig
                    )
                .toList()
                .toTypedArray()

            // follow https://www.d3-graph-gallery.com/graph/pie_basic.html
            val arcParams = pie<DataTriple> {
                value = { (it.third.toDouble() / totalCount) * tau }
            }.render(categorieList)


            val colorOf = { categorie: DataTriple ->
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

            val arcBuilder: ArcBuilder<DataTriple> = arcBuilder {
                startAngle = { data ->
                    arcParams.find { it.data == data }!!.startAngle
                }
                endAngle = { data ->
                    arcParams.find { it.data == data }!!.endAngle
                }
                padAngle = { data ->
                    arcParams.find { it.data == data }!!.padAngle ?: 0.0
                }
                outerRadius = { data ->
                    if (data.first == null || data.first != selectedCategorie)
                        radius else radius * 1.2
                }
            }

            group {
                transform {
                    translate(x = width / 6.0 + margin, y = height / 2.0)
                }
                categorieList.forEach { data ->
                    arcBuilder.buildArcForDatum(data, path {
                        fill = colorOf(data)
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

                categorieList.filter { it.third > 0 }
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
                                    textContent = data.second
                                    fontWeight = if (data.first == null || data.first != selectedCategorie)
                                        NORMAL else BOLD
                                }
                            }
                        }
                    }
            }

            fun getCategorieAt(pos: Point): Categorie? {
                val context = canvas.getContext("2d") as CanvasRenderingContext2D
                val col = context
                    .getImageData(
                        sx = pos.x * canvas.width.toDouble() / width,
                        sy = pos.y * canvas.height.toDouble() / height,
                        sw = 1.0,
                        sh = 1.0
                    )
                    .data
                val color = Colors.rgb(col[0].toInt(), col[1].toInt(), col[2].toInt())

                categorieList.forEach {
                    if (colorOf(it) == color) return it.first
                }
                return null
            }

            val onHovering: (KPointerEvent) -> Unit = {
                selectedCategorie = getCategorieAt(it.pos)
            }

            on(KMouseMove, onHovering)
            on(KTouchStart, onHovering)

            on(KPointerClick) {
                getCategorieAt(it.pos)?.let {
                    onCategorieClicked(it)
                }
            }
        }
    }
}

fun RBuilder.categoriePieChart(handler: RElementBuilder<CategoriePieChartProps>.() -> Unit) =
    child(CategoriePieChart::class, handler)