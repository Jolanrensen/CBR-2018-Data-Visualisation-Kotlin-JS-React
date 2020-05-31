import com.ccfraser.muirwik.components.mSwitchWithLabel
import com.ccfraser.muirwik.components.spacingUnits
import com.ccfraser.muirwik.components.table.MTableCellAlign
import com.ccfraser.muirwik.components.table.mTable
import com.ccfraser.muirwik.components.table.mTableBody
import com.ccfraser.muirwik.components.table.mTableCell
import com.ccfraser.muirwik.components.table.mTableHead
import com.ccfraser.muirwik.components.table.mTableRow
import data.*
import data.Examenresultaat.ONVOLDOENDE
import data.Examenresultaat.VOLDOENDE
import data.ExamenresultaatSoort.*
import data.ExamenresultaatVersie.EERSTE_EXAMEN_OF_TOETS
import data.ExamenresultaatVersie.HEREXAMEN_OF_TOETS
import data2viz.vizComponent
import delegates.ReactPropAndStateDelegates.propDelegateOf
import delegates.ReactPropAndStateDelegates.stateDelegateOf
import io.data2viz.axis.Orient
import io.data2viz.axis.axis
import io.data2viz.color.Colors
import io.data2viz.geom.size
import io.data2viz.scale.Scales
import io.data2viz.scale.StrictlyContinuous
import io.data2viz.viz.KPointerClick
import io.data2viz.viz.Margins
import kotlinx.css.*
import libs.RPureComponent
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.events.Event
import react.RBuilder
import react.RConsumerProps
import react.RProps
import react.RState
import styled.css
import styled.styledDiv
import kotlin.math.max

interface ResultCardProps : RProps {
    var currentResults: Sequence<Int>
    var selectedProducts: Set<Product>
    var onSchakelSoortClicked: (ExamenresultaatSoort) -> Unit
}

interface ResultCardState : RState {
    var examenresultaatVersie: ExamenresultaatVersie
}

class ResultCard(prps: ResultCardProps) : RPureComponent<ResultCardProps, ResultCardState>(prps) {

    val currentResults by propDelegateOf(ResultCardProps::currentResults)
    val selectedProducts by propDelegateOf(ResultCardProps::selectedProducts)
    val onSchakelSoortClicked by propDelegateOf(ResultCardProps::onSchakelSoortClicked)

    override fun ResultCardState.init(props: ResultCardProps) {
        examenresultaatVersie = EERSTE_EXAMEN_OF_TOETS
    }

    var examenResultaatVersie by stateDelegateOf(ResultCardState::examenresultaatVersie)

    private val toggleExamenresultaatVersie: (Event?, Boolean?) -> Unit = { _, _ ->
        examenResultaatVersie = when (examenResultaatVersie) {
            EERSTE_EXAMEN_OF_TOETS -> HEREXAMEN_OF_TOETS
            HEREXAMEN_OF_TOETS -> EERSTE_EXAMEN_OF_TOETS
        }
    }

    override fun RBuilder.render() {
        var aantalHandgeschakeldVoldoende = 0
        var aantalHandgeschakeldOnvoldoende = 0
        var aantalAutomaatVoldoende = 0
        var aantalAutomaatOnvoldoende = 0
        var aantalCombiVoldoende = 0
        var aantalCombiOnvoldoende = 0

        for (currentResultId in currentResults) {
            val currentResult = Data.alleResultaten[currentResultId]!!
            if (currentResult.product !in selectedProducts)
                continue

            for (aantal in currentResult.examenresultaatAantallen) {
                if (aantal.examenresultaatVersie != examenResultaatVersie)
                    continue

                aantal.apply {
                    when (examenresultaatSoort) {
                        HANDGESCHAKELD ->
                            when (examenresultaat) {
                                VOLDOENDE -> aantalHandgeschakeldVoldoende += aantal.aantal
                                ONVOLDOENDE -> aantalHandgeschakeldOnvoldoende += aantal.aantal
                            }
                        AUTOMAAT -> when (examenresultaat) {
                            VOLDOENDE -> aantalAutomaatVoldoende += aantal.aantal
                            ONVOLDOENDE -> aantalAutomaatOnvoldoende += aantal.aantal
                        }
                        COMBI -> when (examenresultaat) {
                            VOLDOENDE -> aantalCombiVoldoende += aantal.aantal
                            ONVOLDOENDE -> aantalCombiOnvoldoende += aantal.aantal
                        }
                    }
                }
            }
        }

        println("Results: $aantalHandgeschakeldVoldoende, $aantalAutomaatVoldoende, $aantalCombiVoldoende, $aantalHandgeschakeldOnvoldoende, $aantalAutomaatOnvoldoende, $aantalCombiOnvoldoende")

        val maxHeight = max(
            aantalHandgeschakeldVoldoende + aantalAutomaatVoldoende + aantalCombiVoldoende,
            aantalHandgeschakeldOnvoldoende + aantalAutomaatOnvoldoende + aantalCombiOnvoldoende
        )

        val examenResultaatCategorieColors = mapOf(
            HANDGESCHAKELD to Colors.Web.olivedrab,
            AUTOMAAT to Colors.Web.yellowgreen,
            COMBI to Colors.Web.darkgreen
        )

        val categorieColorsToExamenResultaat = mapOf(
            Colors.Web.olivedrab to HANDGESCHAKELD,
            Colors.Web.yellowgreen to AUTOMAAT,
            Colors.Web.darkgreen to COMBI
        )

        styledDiv {
            css {
                width = 100.pct
                marginTop = 3.spacingUnits
                overflowX = Overflow.auto
            }


            val margins = Margins(10.0, 10.0, 30.0, 70.0)

            val chartWidth = 600.0 - margins.hMargins
            val chartHeight = 300.0 - margins.vMargins

            val width = 600.0
            val height = 300.0
            vizComponent(
                width = width,
                height = height
            ) { canvas ->
                // width voldoende/onvoldoende
                // height handgeschakeld+automaat+combi

                val heightScale = Scales.Continuous.linear {
                    domain = listOf(.0, maxHeight.toDouble() + 1.0)
                    range = listOf(chartHeight, .0)
                }

                val widthScale = Scales.Discrete.band<Examenresultaat> {
                    domain = Examenresultaat.values().toList()
                    range = StrictlyContinuous(.0, chartWidth)
                    padding = .1
                }


                group {
                    transform {
                        translate(margins.left, margins.top)
                    }

                    // voldoende

                    var voldoendeHeight1 = 0.0
                    rect {
                        fill = examenResultaatCategorieColors[HANDGESCHAKELD]

                        val top = heightScale(aantalHandgeschakeldVoldoende)

                        y = top
                        x = widthScale(VOLDOENDE)

                        voldoendeHeight1 = chartHeight - top

                        size = size(
                            widthScale.bandwidth,
                            voldoendeHeight1
                        )
                    }

                    var voldoendeHeight2 = 0.0
                    rect {
                        fill = examenResultaatCategorieColors[AUTOMAAT]

                        val top = heightScale(aantalAutomaatVoldoende)

                        y = top - voldoendeHeight1
                        x = widthScale(VOLDOENDE)

                        voldoendeHeight2 = chartHeight - top

                        size = size(
                            widthScale.bandwidth,
                            voldoendeHeight2
                        )
                    }

                    rect {
                        fill = examenResultaatCategorieColors[COMBI]

                        val top = heightScale(aantalCombiVoldoende)

                        y = top - voldoendeHeight1 - voldoendeHeight2
                        x = widthScale(VOLDOENDE)

                        val voldoendeHeight3 = chartHeight - top

                        size = size(
                            widthScale.bandwidth,
                            voldoendeHeight3
                        )
                    }

                    // onvoldoende

                    var onvoldoendeHeight1 = 0.0
                    rect {
                        fill = examenResultaatCategorieColors[HANDGESCHAKELD]

                        val top = heightScale(aantalHandgeschakeldOnvoldoende)

                        y = top
                        x = widthScale(ONVOLDOENDE)

                        onvoldoendeHeight1 = chartHeight - top

                        size = size(
                            widthScale.bandwidth,
                            onvoldoendeHeight1
                        )
                    }

                    var onvoldoendeHeight2 = 0.0
                    rect {
                        fill = examenResultaatCategorieColors[AUTOMAAT]

                        val top = heightScale(aantalAutomaatOnvoldoende)

                        y = top - onvoldoendeHeight1
                        x = widthScale(ONVOLDOENDE)

                        onvoldoendeHeight2 = chartHeight - top

                        size = size(
                            widthScale.bandwidth,
                            onvoldoendeHeight2
                        )
                    }

                    rect {
                        fill = examenResultaatCategorieColors[COMBI]

                        val top = heightScale(aantalCombiOnvoldoende)

                        y = top - onvoldoendeHeight1 - onvoldoendeHeight2
                        x = widthScale(ONVOLDOENDE)

                        val onvoldoendeHeight3 = chartHeight - top

                        size = size(
                            widthScale.bandwidth,
                            onvoldoendeHeight3
                        )
                    }
                }

                // Place x-axis
                group {
                    transform {
                        translate(margins.left, 300.0 - margins.bottom)
                    }
                    axis(Orient.BOTTOM, widthScale)
                }

                // Place y-axis
                group {
                    transform {
                        translate(margins.left, margins.top)
                    }
                    axis(Orient.LEFT, heightScale)
                }

                val onHovering = {

                }

                on(KPointerClick) {
                    val context = canvas.getContext("2d") as CanvasRenderingContext2D
                    val col = context.getImageData(
                        sx = it.pos.x * canvas.width.toDouble() / width,
                        sy = it.pos.y * canvas.height.toDouble() / height,
                        sw = 1.0,
                        sh = 1.0
                    ).data
                    val color = Colors.rgb(col[0].toInt(), col[1].toInt(), col[2].toInt())

                    categorieColorsToExamenResultaat[color]?.let {
                        onSchakelSoortClicked(it)
                    }
                }
            }

            // TODO make bar chart clickable

            mTable {
                mTableHead {
                    mTableRow {
                        mTableCell { }
                        mTableCell {
                            mSwitchWithLabel(
                                label = examenResultaatVersie.title,
                                checked = examenResultaatVersie == EERSTE_EXAMEN_OF_TOETS,
                                onChange = toggleExamenresultaatVersie
                            )
                        }
                        Examenresultaat.values().forEach {
                            mTableCell(align = MTableCellAlign.right) { +it.title }
                        }
                        mTableCell(align = MTableCellAlign.right) { +"Percentage Voldoende" }
                    }
                }
                mTableBody {
                    for (categorie in listOf(COMBI, AUTOMAAT, HANDGESCHAKELD)) {
                        mTableRow(key = categorie.title) {
                            mTableCell {
                                css {
                                    val vizColor = examenResultaatCategorieColors[categorie] ?: error("")
                                    color = Color("rgb(${vizColor.r},${vizColor.g},${vizColor.b})")
                                }
                                +"■"
                            }
                            mTableCell {
                                +categorie.title
                            }
                            for (resultaat in Examenresultaat.values()) {
                                mTableCell(align = MTableCellAlign.right) {
                                    +when (categorie) {
                                        HANDGESCHAKELD ->
                                            when (resultaat) {
                                                VOLDOENDE -> aantalHandgeschakeldVoldoende
                                                ONVOLDOENDE -> aantalHandgeschakeldOnvoldoende
                                            }
                                        AUTOMAAT -> when (resultaat) {
                                            VOLDOENDE -> aantalAutomaatVoldoende
                                            ONVOLDOENDE -> aantalAutomaatOnvoldoende
                                        }
                                        COMBI -> when (resultaat) {
                                            VOLDOENDE -> aantalCombiVoldoende
                                            ONVOLDOENDE -> aantalCombiOnvoldoende
                                        }
                                    }.toString()

                                }
                            }
                            mTableCell(align = MTableCellAlign.right) {
                                +when (categorie) {
                                    HANDGESCHAKELD -> (
                                            aantalHandgeschakeldVoldoende.toDouble() /
                                                    (aantalHandgeschakeldVoldoende + aantalHandgeschakeldOnvoldoende).toDouble()
                                            ).asPercentage()
                                    AUTOMAAT -> (
                                            aantalAutomaatVoldoende.toDouble() /
                                                    (aantalAutomaatVoldoende + aantalAutomaatOnvoldoende).toDouble()
                                            ).asPercentage()
                                    COMBI -> (
                                            aantalCombiVoldoende.toDouble() /
                                                    (aantalCombiVoldoende + aantalCombiOnvoldoende).toDouble()
                                            ).asPercentage()

                                }
                            }
                        }
                    }

                    mTableRow {
                        mTableCell { }
                        mTableCell { +"Totaal" }
                        for (resultaat in Examenresultaat.values()) {
                            mTableCell(align = MTableCellAlign.right) {
                                +when (resultaat) {
                                    VOLDOENDE -> aantalHandgeschakeldVoldoende + aantalAutomaatVoldoende + aantalCombiVoldoende
                                    ONVOLDOENDE -> aantalHandgeschakeldOnvoldoende + aantalAutomaatOnvoldoende + aantalCombiOnvoldoende
                                }.toString()
                            }
                        }
                        mTableCell(align = MTableCellAlign.right) {
                            +(
                                    (aantalHandgeschakeldVoldoende
                                            + aantalAutomaatVoldoende
                                            + aantalCombiVoldoende
                                            ).toDouble()

                                            /

                                            (aantalHandgeschakeldVoldoende
                                                    + aantalAutomaatVoldoende
                                                    + aantalCombiVoldoende
                                                    + aantalHandgeschakeldOnvoldoende
                                                    + aantalAutomaatOnvoldoende
                                                    + aantalCombiOnvoldoende
                                                    ).toDouble()
                                    ).asPercentage()
                        }
                    }
                }
            }
        }
    }
}

fun RBuilder.resultCard(handler: ResultCardProps.() -> Unit) = child(ResultCard::class) {
    attrs(handler)
}