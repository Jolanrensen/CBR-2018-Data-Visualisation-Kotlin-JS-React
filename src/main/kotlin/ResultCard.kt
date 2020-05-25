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
import data.ExamenresultaatCategorie.*
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
import io.data2viz.viz.Margins
import kotlinx.css.*
import libs.RPureComponent
import org.w3c.dom.events.Event
import react.RBuilder
import react.RProps
import react.RState
import styled.css
import styled.styledDiv
import kotlin.math.max

interface ResultCardProps : RProps {
    var currentResults: Sequence<Int>
    var selectionFinished: () -> Boolean
    var selectedProducts: Set<Product>
}

interface ResultCardState : RState {
    var examenresultaatVersie: ExamenresultaatVersie
}

class ResultCard(prps: ResultCardProps) : RPureComponent<ResultCardProps, ResultCardState>(prps) {

    val currentResults by propDelegateOf(ResultCardProps::currentResults)
    val selectionFinished by propDelegateOf(ResultCardProps::selectionFinished)
    val selectedProducts by propDelegateOf(ResultCardProps::selectedProducts)

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
        if (!selectionFinished()) return // TODO check what to show if selection is not finished

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
                    when (examenresultaatCategorie) {
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

        styledDiv {
            css {
                width = 100.pct
                marginTop = 3.spacingUnits
                overflowX = Overflow.auto
            }


            val margins = Margins(10.0, 10.0, 30.0, 70.0)

            val chartWidth = 600.0 - margins.hMargins
            val chartHeight = 300.0 - margins.vMargins

            vizComponent(
                width = 600.0,
                height = 300.0
            ) {
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
                        println("top = $top")

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
                        println("top = $top")

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
                        println("top = $top")

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
                        println("top = $top")

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
                        println("top = $top")

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
                        println("top = $top")

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
                                    +if (selectionFinished()) {
                                        when (categorie) {
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
                                    } else "-"
                                }
                            }
                            mTableCell(align = MTableCellAlign.right) {
                                +if (selectionFinished())
                                    when (categorie) {
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
                                    } else "-"
                            }
                        }
                    }

                    mTableRow {
                        mTableCell { }
                        mTableCell { +"Totaal" }
                        for (resultaat in Examenresultaat.values()) {
                            mTableCell(align = MTableCellAlign.right) {
                                +if (selectionFinished())
                                    when (resultaat) {
                                        VOLDOENDE -> aantalHandgeschakeldVoldoende + aantalAutomaatVoldoende + aantalCombiVoldoende
                                        ONVOLDOENDE -> aantalHandgeschakeldOnvoldoende + aantalAutomaatOnvoldoende + aantalCombiOnvoldoende
                                    }.toString() else "-"
                            }
                        }
                        mTableCell(align = MTableCellAlign.right) {
                            +if (selectionFinished()) (
                                    (aantalHandgeschakeldVoldoende + aantalAutomaatVoldoende + aantalCombiVoldoende).toDouble() /
                                            (aantalHandgeschakeldVoldoende + aantalAutomaatVoldoende + aantalCombiVoldoende + aantalHandgeschakeldOnvoldoende + aantalAutomaatOnvoldoende + aantalCombiOnvoldoende).toDouble()
                                    ).asPercentage() else "-"
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