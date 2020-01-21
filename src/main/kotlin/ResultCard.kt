import com.ccfraser.muirwik.components.mSwitch
import com.ccfraser.muirwik.components.mSwitchWithLabel
import com.ccfraser.muirwik.components.spacingUnits
import com.ccfraser.muirwik.components.table.MTableCellAlign
import com.ccfraser.muirwik.components.table.mTable
import com.ccfraser.muirwik.components.table.mTableBody
import com.ccfraser.muirwik.components.table.mTableCell
import com.ccfraser.muirwik.components.table.mTableHead
import com.ccfraser.muirwik.components.table.mTableRow
import data.*
import data.ExamenResultaatVersie.EERSTE_EXAMEN_OF_TOETS
import data.ExamenResultaatVersie.HEREXAMEN_OF_TOETS
import kotlinx.css.*
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import styled.css

interface ResultCardProps : RProps {
    var currentResults: Sequence<Resultaat>
    var selectionFinished: () -> Boolean
    var selectedProducts: Set<Product>
}

interface ResultCardState : RState {
    var examenResultaatVersie: ExamenResultaatVersie
}

class ResultCard(prps: ResultCardProps) : RComponent<ResultCardProps, ResultCardState>(prps) {

    val currentResults by readOnlyPropDelegateOf(ResultCardProps::currentResults)
    val selectionFinished by readOnlyPropDelegateOf(ResultCardProps::selectionFinished)
    val selectedProducts by readOnlyPropDelegateOf(ResultCardProps::selectedProducts)

    override fun ResultCardState.init(props: ResultCardProps) {
        examenResultaatVersie = EERSTE_EXAMEN_OF_TOETS
    }

    var examenResultaatVersie by stateDelegateOf(ResultCardState::examenResultaatVersie)

    override fun RBuilder.render() {
        hoveringCard {
            css {
                width = 100.pct
                marginTop = 3.spacingUnits
                overflowX = Overflow.auto
            }
            mTable {
                mTableHead {
                    mTableRow {
                        mTableCell {
                            mSwitchWithLabel(
                                label = examenResultaatVersie.title,
                                checked = examenResultaatVersie == EERSTE_EXAMEN_OF_TOETS,
                                onChange = { _, _ ->
                                    examenResultaatVersie = when (examenResultaatVersie) {
                                        EERSTE_EXAMEN_OF_TOETS -> HEREXAMEN_OF_TOETS
                                        HEREXAMEN_OF_TOETS -> EERSTE_EXAMEN_OF_TOETS
                                    }
                                })
                        }
                        ExamenResultaat.values().forEach {
                            mTableCell(align = MTableCellAlign.right) { +it.title }
                        }
                        mTableCell(align = MTableCellAlign.right) { +"Percentage Voldoende" }
                    }
                }
                mTableBody {
                    for (categorie in ExamenResultaatCategorie.values()) {
                        mTableRow(key = categorie.title) {
                            mTableCell { +categorie.title }
                            val examenResultaten = hashMapOf<ExamenResultaat, Int>()
                            for (resultaat in ExamenResultaat.values()) {
                                mTableCell(align = MTableCellAlign.right) {
                                    +if (selectionFinished()) {
                                        currentResults
                                            .asSequence()
                                            .filter { it.product in selectedProducts }
                                            .sumBy {
                                                it.examenResultaatAantallen
                                                    .asSequence()
                                                    .filter {
                                                        it.examenResultaatVersie == examenResultaatVersie
                                                                && it.examenResultaatCategorie == categorie
                                                                && it.examenResultaat == resultaat
                                                    }.sumBy { it.aantal }
                                            }
                                            .apply { examenResultaten[resultaat] = this }
                                            .toString()
                                    } else "-"
                                }
                            }
                            mTableCell(align = MTableCellAlign.right) {
                                +if (selectionFinished())
                                    "${(examenResultaten[ExamenResultaat.VOLDOENDE]!!.toDouble() / examenResultaten.values
                                        .sum() * 100.0).toInt()}%"
                                else "-"
                            }
                        }
                    }
                    mTableRow {
                        mTableCell { +"Totaal" }
                        val examenResultaten = hashMapOf<ExamenResultaat, Int>()
                        for (resultaat in ExamenResultaat.values()) {
                            mTableCell(align = MTableCellAlign.right) {
                                +if (selectionFinished()) {
                                    currentResults
                                        .asSequence()
                                        .filter { it.product in selectedProducts }
                                        .sumBy {
                                            it.examenResultaatAantallen
                                                .asSequence()
                                                .filter {
                                                    it.examenResultaatVersie == examenResultaatVersie
                                                            && it.examenResultaat == resultaat
                                                }.sumBy { it.aantal }
                                        }
                                        .apply { examenResultaten[resultaat] = this }
                                        .toString()
                                } else "-"
                            }
                        }
                        mTableCell(align = MTableCellAlign.right) {
                            +if (selectionFinished())
                                "${(examenResultaten[ExamenResultaat.VOLDOENDE]!!.toDouble() / examenResultaten.values
                                    .sum() * 100.0).toInt()}%"
                            else "-"
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