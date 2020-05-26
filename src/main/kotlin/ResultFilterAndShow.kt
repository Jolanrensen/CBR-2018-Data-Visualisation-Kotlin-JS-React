import com.ccfraser.muirwik.components.*
import data.Categorie
import data.Data
import data.Product
import data.producten
import delegates.ReactPropAndStateDelegates
import delegates.ReactPropAndStateDelegates.StateAsProp
import delegates.ReactPropAndStateDelegates.propDelegateOf
import delegates.ReactPropAndStateDelegates.stateAsProp
import delegates.ReactPropAndStateDelegates.stateDelegateOf
import filterableLists.categorieProductList
import filterableLists.examenlocatiesList
import filterableLists.opleidersList
import libs.RPureComponent
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

interface ResultFilterAndShowProps : RProps {
    var dataLoaded: Boolean
    var setApplyOpleidersFilterFunction: (ApplyFilter) -> Unit
    var setApplyExamenlocatieFilterFunction: (ApplyFilter) -> Unit

    var setSelectAllOpleidersFunction: (SelectAll) -> Unit
    var setDeselectAllOpleidersFunction: (DeselectAll) -> Unit

    var setSelectAllExamenlocatiesFunction: (SelectAll) -> Unit
    var setDeselectAllExamenlocatiesFunction: (DeselectAll) -> Unit

    var selectedOpleiderKeys: StateAsProp<Set<String>>
    var selectedExamenlocatieKeys: StateAsProp<Set<String>>
    var selectedProducts: StateAsProp<Set<Product>>
}

interface ResultFilterAndShowState : RState

class ResultFilterAndShow(prps: ResultFilterAndShowProps) :
/*RPure*/RComponent<ResultFilterAndShowProps, ResultFilterAndShowState>(prps) {

    @Suppress("SimplifyBooleanWithConstants")
    override fun shouldComponentUpdate(nextProps: ResultFilterAndShowProps, nextState: ResultFilterAndShowState) = false
            || props.dataLoaded != nextProps.dataLoaded
            || props.selectedOpleiderKeys != nextProps.selectedOpleiderKeys
            || props.selectedExamenlocatieKeys != nextProps.selectedExamenlocatieKeys
            || props.selectedProducts != nextProps.selectedProducts

    private val dataLoaded by propDelegateOf(ResultFilterAndShowProps::dataLoaded)

    private var selectedOpleiderKeys by propDelegateOf(ResultFilterAndShowProps::selectedOpleiderKeys)
    private var selectedExamenlocatieKeys by propDelegateOf(ResultFilterAndShowProps::selectedExamenlocatieKeys)
    private var selectedProducts by propDelegateOf(ResultFilterAndShowProps::selectedProducts)

    private val emptySelectAllFunction: (SelectAll) -> Unit = {}
    private val emptyDeselectAllFunction: (DeselectAll) -> Unit = {}
    private val emptyOnCategorieClicked: (Categorie) -> Unit = {}

    private val selectionFinished = {
        selectedOpleiderKeys.isNotEmpty() &&
                selectedExamenlocatieKeys.isNotEmpty() &&
                selectedProducts.isNotEmpty()
    }


    private var applyCategorieFilter: ApplyFilter? = null
    private val setApplyCategorieFilterFunction: (ApplyFilter) -> Unit = {
        applyCategorieFilter = it
    }

    private val onCategorieClicked: (Categorie) -> Unit = { cat ->
        applyCategorieFilter?.invoke("")
        if (selectedProducts.any { it.categorie != cat } || cat.producten.any { it !in selectedProducts })
            selectedProducts = cat.producten.toSet()
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
                    selectedItemKeys = props.selectedOpleiderKeys
                    selectedOtherItemKeys = props.selectedExamenlocatieKeys
                    setApplyFilterFunction = props.setApplyOpleidersFilterFunction

                    setSelectAllFunction = props.setSelectAllOpleidersFunction
                    setDeselectAllFunction = props.setDeselectAllOpleidersFunction

                    onCategorieClicked = this@ResultFilterAndShow.onCategorieClicked
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
                    selectedItemKeys = props.selectedExamenlocatieKeys
                    selectedOtherItemKeys = props.selectedOpleiderKeys
                    setApplyFilterFunction = props.setApplyExamenlocatieFilterFunction

                    setSelectAllFunction = props.setSelectAllExamenlocatiesFunction
                    setDeselectAllFunction = props.setDeselectAllExamenlocatiesFunction

                    onCategorieClicked = this@ResultFilterAndShow.onCategorieClicked
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
                    selectedItemKeys = props.selectedProducts
                    selectedOtherItemKeys = stateAsProp(setOf()) // not used

                    setApplyFilterFunction = setApplyCategorieFilterFunction
                    setSelectAllFunction = emptySelectAllFunction // not used
                    setDeselectAllFunction = emptyDeselectAllFunction // not used
                    onCategorieClicked = emptyOnCategorieClicked // not used
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