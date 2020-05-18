import com.ccfraser.muirwik.components.*
import data.Data
import data.Product
import delegates.ReactPropAndStateDelegates.propDelegateOf
import delegates.ReactPropAndStateDelegates.stateAsProp
import delegates.ReactPropAndStateDelegates.stateDelegateOf
import filterableLists.categorieProductList
import filterableLists.examenlocatiesList
import filterableLists.opleidersList
import libs.RPureComponent
import react.RBuilder
import react.RProps
import react.RState

interface ResultFilterAndShowProps : RProps {
    var dataLoaded: Boolean
    var setApplyOpleidersFilterFunction: (ApplyFilter) -> Unit
    var setApplyExamenlocatieFilterFunction: (ApplyFilter) -> Unit
    var setSelectAllOpleidersFunction: (SelectAll) -> Unit
    var setSelectAllExamenlocatiesFunction: (SelectAll) -> Unit
}

interface ResultFilterAndShowState : RState {
    var selectedOpleiderKeys: Set<String>
    var selectedExamenlocatieKeys: Set<String>
    var selectedProducts: Set<Product>
}

class ResultFilterAndShow(prps: ResultFilterAndShowProps) :
    RPureComponent<ResultFilterAndShowProps, ResultFilterAndShowState>(prps) {

    private val dataLoaded by propDelegateOf(ResultFilterAndShowProps::dataLoaded)

    override fun ResultFilterAndShowState.init(props: ResultFilterAndShowProps) {
        selectedOpleiderKeys = setOf()
        selectedExamenlocatieKeys = setOf()
        selectedProducts = setOf()
    }

    private var selectedOpleiderKeys by stateDelegateOf(ResultFilterAndShowState::selectedOpleiderKeys)
    private var selectedExamenlocatieKeys by stateDelegateOf(ResultFilterAndShowState::selectedExamenlocatieKeys)
    private var selectedProducts by stateDelegateOf(ResultFilterAndShowState::selectedProducts)


    private val emptyApplyFilterFunction: (ApplyFilter) -> Unit = {}
    private val emptySelectAllFunction: (SelectAll) -> Unit = {}

    private val selectionFinished = {
        selectedOpleiderKeys.isNotEmpty() &&
                selectedExamenlocatieKeys.isNotEmpty() &&
                selectedProducts.isNotEmpty()
    }

    override fun RBuilder.render() {
        mGridContainer(
            spacing = MGridSpacing.spacing3,
            alignContent = MGridAlignContent.center,
            justify = MGridJustify.center
        ) {
            mGridItem(
                xs = MGridSize.cells12,
                md = MGridSize.cells6,
                lg = MGridSize.cells4,
                xl = MGridSize.cells2
            ) {
                filterList(opleidersList, "opleiders") {
                    dataLoaded = this@ResultFilterAndShow.dataLoaded
                    itemsData = if (this@ResultFilterAndShow.dataLoaded) Data.alleOpleiders else mapOf()
                    alwaysAllowSelectAll = true
                    selectedItemKeys = stateAsProp(ResultFilterAndShowState::selectedOpleiderKeys)
                    selectedOtherItemKeys = stateAsProp(ResultFilterAndShowState::selectedExamenlocatieKeys)
                    setApplyFilterFunction = props.setApplyOpleidersFilterFunction
                    setSelectAllFunction = props.setSelectAllOpleidersFunction
                }
            }

            mGridItem(
                xs = MGridSize.cells12,
                md = MGridSize.cells6,
                lg = MGridSize.cells4,
                xl = MGridSize.cells2
            ) {
                filterList(examenlocatiesList, "examenlocaties") {
                    dataLoaded = this@ResultFilterAndShow.dataLoaded
                    itemsData = if (this@ResultFilterAndShow.dataLoaded) Data.alleExamenlocaties else mapOf()
                    alwaysAllowSelectAll = true
                    selectedItemKeys = stateAsProp(ResultFilterAndShowState::selectedExamenlocatieKeys)
                    selectedOtherItemKeys = stateAsProp(ResultFilterAndShowState::selectedOpleiderKeys)
                    setApplyFilterFunction = props.setApplyExamenlocatieFilterFunction
                    setSelectAllFunction = props.setSelectAllExamenlocatiesFunction
                }
            }

            mGridItem(
                xs = MGridSize.cells12,
                md = MGridSize.cells12,
                lg = MGridSize.cells4,
                xl = MGridSize.cells3
            ) {
                filterList(categorieProductList, "categorieën/producten") {
                    dataLoaded = true
                    itemsData = mapOf(*Product.values().map { it to it }.toTypedArray()) // not used
                    alwaysAllowSelectAll = true
                    selectedItemKeys = stateAsProp(ResultFilterAndShowState::selectedProducts)
                    selectedOtherItemKeys = stateAsProp(setOf()) // not used
                    setApplyFilterFunction = emptyApplyFilterFunction // not used
                    setSelectAllFunction = emptySelectAllFunction // not used
                }
            }

            mGridItem(
                xs = MGridSize.cells12,
                md = MGridSize.cells12,
                lg = MGridSize.cells12,
                xl = MGridSize.cells5
            ) {
                val currentResults =
                    if (!selectionFinished())
                        sequenceOf()
                    else (Data.opleiderToResultaten
                        .asSequence()
                        .filter { it.key in selectedOpleiderKeys }
                        .map { it.value }
                        .flatten()
                        .asIterable()

                            intersect

                            Data.examenlocatieToResultaten
                                .asSequence()
                                .filter { it.key in selectedExamenlocatieKeys }
                                .map { it.value }
                                .flatten()
                                .asIterable()
                            ).asSequence()


                // TODO replace with new barchart
                resultCard {
                    this.currentResults = currentResults
                    selectionFinished = this@ResultFilterAndShow.selectionFinished
                    selectedProducts = this@ResultFilterAndShow.selectedProducts
                }


            }
        }
    }
}

fun RBuilder.resultFilterAndShow(handler: ResultFilterAndShowProps.() -> Unit) =
    child(ResultFilterAndShow::class) {
        attrs {
            handler()
        }
    }