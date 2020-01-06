import com.ccfraser.muirwik.components.spacingUnits
import com.ccfraser.muirwik.components.table.MTableCellAlign
import com.ccfraser.muirwik.components.table.mTable
import com.ccfraser.muirwik.components.table.mTableBody
import com.ccfraser.muirwik.components.table.mTableCell
import com.ccfraser.muirwik.components.table.mTableHead
import com.ccfraser.muirwik.components.table.mTableRow
import data.ExamenResultaat
import data.ExamenResultaatCategorie
import data.ExamenResultaatVersie
import data.Product
import data.Resultaat
import kotlinx.css.Overflow
import kotlinx.css.marginTop
import kotlinx.css.overflowX
import kotlinx.css.pct
import kotlinx.css.width
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import styled.css

interface ResultCardProps : RProps {
    var examenResultaatVersie: ExamenResultaatVersie
    var currentResults: Sequence<Resultaat>
    var selectionFinished: () -> Boolean
    var selectedProducts: Set<Product>
}

interface ResultCardState : RState

class ResultCard(props: ResultCardProps) : RComponent<ResultCardProps, ResultCardState>(props) {

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
                        mTableCell { +props.examenResultaatVersie.title }
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
                                    +if (props.selectionFinished()) {
                                        props.currentResults
                                            .filter { it.product in props.selectedProducts }
                                            .sumBy {
                                                it.examenResultaatAantallen
                                                    .asSequence()
                                                    .filter {
                                                        it.examenResultaatVersie == props.examenResultaatVersie
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
                                +if (props.selectionFinished())
                                    "${(examenResultaten[ExamenResultaat.VOLDOENDE]!!.toDouble() / examenResultaten.values.sum() * 100.0).toInt()}%"
                                else "-"
                            }
                        }
                    }
                    mTableRow {
                        mTableCell { +"Totaal" }
                        val examenResultaten = hashMapOf<ExamenResultaat, Int>()
                        for (resultaat in ExamenResultaat.values()) {
                            mTableCell(align = MTableCellAlign.right) {
                                +if (props.selectionFinished()) {
                                    props.currentResults
                                        .filter { it.product in props.selectedProducts }
                                        .sumBy {
                                            it.examenResultaatAantallen
                                                .asSequence()
                                                .filter {
                                                    it.examenResultaatVersie == props.examenResultaatVersie
                                                        && it.examenResultaat == resultaat
                                                }.sumBy { it.aantal }
                                        }
                                        .apply { examenResultaten[resultaat] = this }
                                        .toString()
                                } else "-"
                            }
                        }
                        mTableCell(align = MTableCellAlign.right) {
                            +if (props.selectionFinished())
                                "${(examenResultaten[ExamenResultaat.VOLDOENDE]!!.toDouble() / examenResultaten.values.sum() * 100.0).toInt()}%"
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