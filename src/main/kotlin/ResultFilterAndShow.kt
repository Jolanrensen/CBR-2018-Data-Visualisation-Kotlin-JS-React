import com.ccfraser.muirwik.components.*
import data.Data
import data.ExamenResultaatVersie
import data.Product
import filterableLists.categorieProductList
import filterableLists.examenlocatiesList
import filterableLists.opleidersList
import react.*

interface ResultFilterAndShowProps : RProps {
    var dataLoaded: Boolean
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

    private var selectedOpleiderKeys by stateDelegateOf(ResultFilterAndShowState::selectedOpleiderKeys)
    private var selectedExamenlocatieKeys by stateDelegateOf(ResultFilterAndShowState::selectedExamenlocatieKeys)
    private var selectedProducts by stateDelegateOf(ResultFilterAndShowState::selectedProducts)

    private val dataLoaded by readOnlyPropDelegateOf(ResultFilterAndShowProps::dataLoaded)

    private fun selectionFinished() =
        selectedOpleiderKeys.isNotEmpty() &&
                selectedExamenlocatieKeys.isNotEmpty() &&
                selectedProducts.isNotEmpty()

    override fun RBuilder.render() {
        mGridContainer(
            spacing = MGridSpacing.spacing3,
            alignContent = MGridAlignContent.center
        ) {
            mGridItem(
                xs = MGridSize.cells12,
                md = MGridSize.cells6,
                lg = MGridSize.cells4,
                xl = MGridSize.cells2
            ) {
                filterList(opleidersList, "opleiders") {
                    itemsData = if (dataLoaded) Data.alleOpleiders else mapOf()
                    selectedItemKeysDelegate = stateDelegateOf(ResultFilterAndShowState::selectedOpleiderKeys)
                    selectedOtherItemKeysDelegate = stateDelegateOf(ResultFilterAndShowState::selectedExamenlocatieKeys)
                    onSelectionChanged = {}
                }
            }

            mGridItem(
                xs = MGridSize.cells12,
                md = MGridSize.cells6,
                lg = MGridSize.cells4,
                xl = MGridSize.cells2
            ) {
                filterList(examenlocatiesList, "examenlocaties") {
                    itemsData = if (dataLoaded) Data.alleExamenlocaties else mapOf()
                    selectedItemKeysDelegate = stateDelegateOf(ResultFilterAndShowState::selectedExamenlocatieKeys)
                    selectedOtherItemKeysDelegate = stateDelegateOf(ResultFilterAndShowState::selectedOpleiderKeys)
                    onSelectionChanged = {}
                }
            }

            mGridItem(
                xs = MGridSize.cells12,
                md = MGridSize.cells12,
                lg = MGridSize.cells4,
                xl = MGridSize.cells3
            ) {
                filterList(categorieProductList, "categorieën/producten") {
                    alwaysAllowSelectAll = true
                    selectedItemKeysDelegate = stateDelegateOf(ResultFilterAndShowState::selectedProducts)
                    selectedOtherItemKeysDelegate = stateDelegateOf(setOf()) // not used
                    onSelectionChanged = {}
                }
            }

            mGridItem(
                xs = MGridSize.cells12,
                md = MGridSize.cells12,
                lg = MGridSize.cells12,
                xl = MGridSize.cells5
            ) {
                mGridContainer(
                    spacing = MGridSpacing.spacing3,
                    alignContent = MGridAlignContent.center
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

//                    Data.getResults(
//                        selectedOpleiderKeys,
//                        selectedExamenlocatieKeys
//                    )
                    mGridItem(
                        xs = MGridSize.cells12,
                        md = MGridSize.cells6,
                        lg = MGridSize.cells6,
                        xl = MGridSize.cells12
                    ) {
                        resultCard {
//                            examenResultaatVersie = ExamenResultaatVersie.EERSTE_EXAMEN_OF_TOETS
                            this.currentResults = currentResults
                            selectionFinished = ::selectionFinished
                            selectedProducts = this@ResultFilterAndShow.selectedProducts
                        }
                    }

//                    mGridItem(
//                        xs = MGridSize.cells12,
//                        md = MGridSize.cells6,
//                        lg = MGridSize.cells6,
//                        xl = MGridSize.cells12
//                    ) {
//                        resultCard {
////                            examenResultaatVersie = ExamenResultaatVersie.HEREXAMEN_OF_TOETS
//                            this.currentResults = currentResults
//                            selectionFinished = ::selectionFinished
//                            selectedProducts = this@ResultFilterAndShow.selectedProducts
//                        }
//                    }
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