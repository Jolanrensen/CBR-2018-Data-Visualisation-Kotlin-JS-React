import com.ccfraser.muirwik.components.*
import data.Data
import data.Product
import filterableLists.categorieProductList
import filterableLists.examenlocatiesList
import filterableLists.opleidersList
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

interface ResultFilterAndShowProps : RProps {
    var dataLoaded: Boolean
    var setApplyOpleidersFilterFunction: (ApplyFilter) -> Unit
    var setApplyExamenlocatieFilterFunction: (ApplyFilter) -> Unit
}

interface ResultFilterAndShowState : RState {
    var selectedOpleiderKeys: Set<String>
    var selectedExamenlocatieKeys: Set<String>
    var selectedProducts: Set<Product>
}

class ResultFilterAndShow(prps: ResultFilterAndShowProps) :
    RComponent<ResultFilterAndShowProps, ResultFilterAndShowState>(prps) {

    override fun ResultFilterAndShowState.init(props: ResultFilterAndShowProps) {
        selectedOpleiderKeys = setOf()
        selectedExamenlocatieKeys = setOf()
        selectedProducts = setOf()
    }

    override fun shouldComponentUpdate(
        nextProps: ResultFilterAndShowProps,
        nextState: ResultFilterAndShowState
    ) = when {
        props.dataLoaded != nextProps.dataLoaded -> {
            println("dataLoaded changed")
            true
        }
        props.setApplyExamenlocatieFilterFunction != nextProps.setApplyExamenlocatieFilterFunction -> {
            println("setApplyExamenlocatieFilterFunction changed")
            true
        }
        props.setApplyOpleidersFilterFunction != nextProps.setApplyOpleidersFilterFunction -> {
            println("setApplyOpleidersFilterFunction changed")
            true
        }
        state.selectedOpleiderKeys != nextState.selectedOpleiderKeys -> {
            println("selectedOpleiderKeys changed")
            true
        }
        state.selectedExamenlocatieKeys != nextState.selectedExamenlocatieKeys -> {
            println("selectedExamenlocatieKeys changed")
            true
        }
        state.selectedProducts != nextState.selectedProducts -> {
            println("selectedProducts changed")
            true
        }
        else -> false
    }


//        return super.shouldComponentUpdate(nextProps, nextState)

    private var selectedOpleiderKeys by stateDelegateOf(ResultFilterAndShowState::selectedOpleiderKeys)
    private var selectedExamenlocatieKeys by stateDelegateOf(ResultFilterAndShowState::selectedExamenlocatieKeys)
    private var selectedProducts by stateDelegateOf(ResultFilterAndShowState::selectedProducts)

    private val dataLoaded by propDelegateOf(ResultFilterAndShowProps::dataLoaded)

    private val emptyApplyFilterFunction: (ApplyFilter) -> Unit = {}

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
                    selectedItemKeys = stateAsProp(ResultFilterAndShowState::selectedOpleiderKeys)
                    selectedOtherItemKeys = stateAsProp(ResultFilterAndShowState::selectedExamenlocatieKeys)
                    setApplyFilterFunction = props.setApplyOpleidersFilterFunction
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
                    selectedItemKeys = stateAsProp(ResultFilterAndShowState::selectedExamenlocatieKeys)
                    selectedOtherItemKeys = stateAsProp(ResultFilterAndShowState::selectedOpleiderKeys)
                    setApplyFilterFunction = props.setApplyExamenlocatieFilterFunction
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
                    alwaysAllowSelectAll = true
                    selectedItemKeys = stateAsProp(ResultFilterAndShowState::selectedProducts)
                    selectedOtherItemKeys = stateAsProp(setOf()) // not used
                    setApplyFilterFunction = emptyApplyFilterFunction // not used
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