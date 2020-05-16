import com.ccfraser.muirwik.components.mSwitchWithLabel
import com.ccfraser.muirwik.components.spacingUnits
import com.ccfraser.muirwik.components.table.MTableCellAlign
import com.ccfraser.muirwik.components.table.mTable
import com.ccfraser.muirwik.components.table.mTableBody
import com.ccfraser.muirwik.components.table.mTableCell
import com.ccfraser.muirwik.components.table.mTableHead
import com.ccfraser.muirwik.components.table.mTableRow
import data.*
import data.ExamenResultaat.ONVOLDOENDE
import data.ExamenResultaat.VOLDOENDE
import data.ExamenResultaatVersie.EERSTE_EXAMEN_OF_TOETS
import data.ExamenResultaatVersie.HEREXAMEN_OF_TOETS
import delegates.ReactPropAndStateDelegates.propDelegateOf
import delegates.ReactPropAndStateDelegates.stateDelegateOf
import kotlinx.css.*
import libs.RPureComponent
import org.w3c.dom.events.Event
import react.RBuilder
import react.RProps
import react.RState
import styled.css
import styled.styledDiv

interface ResultCardProps : RProps {
    var currentResults: Sequence<Resultaat>
    var selectionFinished: () -> Boolean
    var selectedProducts: Set<Product>
}

interface ResultCardState : RState {
    var examenResultaatVersie: ExamenResultaatVersie
}

class ResultCard(prps: ResultCardProps) : RPureComponent<ResultCardProps, ResultCardState>(prps) {

    val currentResults by propDelegateOf(ResultCardProps::currentResults)
    val selectionFinished by propDelegateOf(ResultCardProps::selectionFinished)
    val selectedProducts by propDelegateOf(ResultCardProps::selectedProducts)

    override fun ResultCardState.init(props: ResultCardProps) {
        examenResultaatVersie = EERSTE_EXAMEN_OF_TOETS
    }

    var examenResultaatVersie by stateDelegateOf(ResultCardState::examenResultaatVersie)

    private val toggleExamenresultaatVersie: (Event?, Boolean?) -> Unit = { _, _ ->
        examenResultaatVersie = when (examenResultaatVersie) {
            EERSTE_EXAMEN_OF_TOETS -> HEREXAMEN_OF_TOETS
            HEREXAMEN_OF_TOETS -> EERSTE_EXAMEN_OF_TOETS
        }
    }

    override fun RBuilder.render() {
        styledDiv {
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
                                onChange = toggleExamenresultaatVersie
                            )
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
                            val examenResultaten = hashMapOf(
                                ONVOLDOENDE to hashSetOf<Int>(),
                                VOLDOENDE to hashSetOf()
                            )
                            for (resultaat in ExamenResultaat.values()) {
                                mTableCell(align = MTableCellAlign.right) {
                                    +if (selectionFinished()) {
                                        currentResults
                                            .filter { it.product in selectedProducts }
                                            .sumBy {
                                                it.examenResultaatAantallen
                                                    .filter {
                                                        it.examenResultaatVersie == examenResultaatVersie
                                                                && it.examenResultaatCategorie == categorie
                                                                && it.examenResultaat == resultaat
                                                    }.sumBy { it.aantal }
                                            }
                                            .apply { examenResultaten[resultaat]!!.add(this) }
                                            .toString()
                                    } else "-"
                                }
                            }
                            mTableCell(align = MTableCellAlign.right) {
                                +if (selectionFinished())
                                    (examenResultaten[VOLDOENDE]!!.sum().toDouble() /
                                            examenResultaten.values.flatten().sum().toDouble()).let {
                                        if (it.isNaN()) "-"
                                        else "${(it * 100.0).toInt()}%"
                                    }
                                else "-"
                            }
                        }
                    }
                    mTableRow {
                        mTableCell { +"Totaal" }
                        val examenResultaten = hashMapOf(
                            ONVOLDOENDE to hashSetOf<Int>(),
                            VOLDOENDE to hashSetOf()
                        )
                        for (resultaat in ExamenResultaat.values()) {
                            mTableCell(align = MTableCellAlign.right) {
                                +if (selectionFinished()) {
                                    currentResults
                                        .filter { it.product in selectedProducts }
                                        .sumBy {
                                            it.examenResultaatAantallen
                                                .filter {
                                                    it.examenResultaatVersie == examenResultaatVersie
                                                            && it.examenResultaat == resultaat
                                                }.sumBy { it.aantal }
                                        }
                                        .apply { examenResultaten[resultaat]!!.add(this) }
                                        .toString()
                                } else "-"
                            }
                        }
                        mTableCell(align = MTableCellAlign.right) {
                            +if (selectionFinished())
                                (examenResultaten[VOLDOENDE]!!.sum().toDouble() /
                                        examenResultaten.values.flatten().sum().toDouble()).let {
                                    if (it.isNaN()) "-"
                                    else "${(it * 100.0).toInt()}%"
                                }
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