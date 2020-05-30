package filterableLists

import FilterableList
import FilterableListProps
import FilterableListState
import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.dialog.ModalOnCloseReason
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemAvatar
import com.ccfraser.muirwik.components.list.mListItemText
import com.ccfraser.muirwik.components.table.mTable
import com.ccfraser.muirwik.components.table.mTableBody
import com.ccfraser.muirwik.components.table.mTableCell
import com.ccfraser.muirwik.components.table.mTableRow
import data.Categorie
import data.Data
import data.Data.isAllOrNoOpleiders
import data.Data.isAllOrNoProducten
import data.Examenlocatie
import data.Product
import delegates.ReactPropAndStateDelegates.propDelegateOf
import delegates.ReactPropAndStateDelegates.stateDelegateOf
import kotlinx.css.*
import kotlinx.serialization.internal.MapEntry
import libs.reactList.ReactListRef
import libs.reactList.ref
import libs.reactList.styledReactList
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import react.RBuilder
import react.ReactElement
import react.buildElement
import react.dom.findDOMNode
import react.ref
import styled.StyleSheet
import styled.css
import styled.styledDiv
import styled.styledH4
import toInt

interface ExamenlocatiesListProps : FilterableListProps<String, Examenlocatie, Product>

interface ExamenlocatiesListState : FilterableListState

class ExamenlocatiesList(prps: ExamenlocatiesListProps) :
    FilterableList<String, Examenlocatie, Product, ExamenlocatiesListProps, ExamenlocatiesListState>(prps) {

    private var isExamenlocatieSelected by propDelegateOf(ExamenlocatiesListProps::selectedItemKeys)
    private val isOpleiderSelected by propDelegateOf(ExamenlocatiesListProps::selectedOtherItemKeys)
    private val isProductSelected by propDelegateOf(ExamenlocatiesListProps::selectedThirdItemKeys)

    private val filter by propDelegateOf(ExamenlocatiesListProps::filter)
    private val itemsData by propDelegateOf(ExamenlocatiesListProps::itemsData)

    private val onCategorieClicked by propDelegateOf(ExamenlocatiesListProps::onCategorieClicked)

    private val filteredItems
        get() = props.filteredItems
            ?: getFilteredItems(
                filter,
                itemsData,
                isExamenlocatieSelected,
                isOpleiderSelected,
                isProductSelected
            ) // for if ref is not yet set in FilterList

    override fun ExamenlocatiesListState.init(props: ExamenlocatiesListProps) {
        popoverOpen = false
    }

    private var popoverOpen by stateDelegateOf(ExamenlocatiesListState::popoverOpen)

    private var list: ReactListRef? = null

    override fun sortType(type: Examenlocatie) = type.run {
        when {
            slagingspercentageEersteKeer == null && slagingspercentageHerkansing == null -> 0.0
            slagingspercentageEersteKeer == null -> slagingspercentageHerkansing!!
            slagingspercentageHerkansing == null -> slagingspercentageEersteKeer!!
            else -> (type.slagingspercentageEersteKeer!! + type.slagingspercentageHerkansing!!) / 2.0
        }
    }

    override fun keyToType(key: String, itemsData: Map<String, Examenlocatie>) =
        itemsData[key] ?: error("Examenlocatie $key does not exist")

    override fun typeToKey(type: Examenlocatie, itemsData: Map<String, Examenlocatie>) = type.naam
    override fun getFilteredItems(
        filter: String,
        itemsData: Map<String, Examenlocatie>,
        selectedItemKeys: Set<String>,
        selectedOtherItemKeys: Set<String>,
        thirdSelectedItemKeys: Set<Product>
    ): List<Examenlocatie> {
        val filterTerms = filter.split(" ", ", ", ",")
        val score = hashMapOf<String, Int>()

        val thirdSelectedItemKeysEmptyOrFull = thirdSelectedItemKeys.size.isAllOrNoProducten()
        val selectedOtherItemKeysEmptyOrFull = selectedOtherItemKeys.size.isAllOrNoOpleiders()

        when {
            selectedOtherItemKeysEmptyOrFull && thirdSelectedItemKeysEmptyOrFull -> itemsData.asSequence()

            selectedOtherItemKeysEmptyOrFull && !thirdSelectedItemKeysEmptyOrFull -> thirdSelectedItemKeys.asSequence()
                .flatMap { Data.productToExamenlocaties[it]!!.asSequence() }
                .map { MapEntry(it, itemsData[it] ?: error("Examenlocatie $it does not exist")) }

            !selectedOtherItemKeysEmptyOrFull && thirdSelectedItemKeysEmptyOrFull -> selectedOtherItemKeys.asSequence()
                .flatMap { Data.opleiderToExamenlocaties[it]!!.asSequence() }
                .map { MapEntry(it, itemsData[it] ?: error("Examenlocatie $it does not exist")) }

            !selectedOtherItemKeysEmptyOrFull && !thirdSelectedItemKeysEmptyOrFull -> (
                    thirdSelectedItemKeys.asSequence()
                        .flatMap { Data.productToExamenlocaties[it]!!.asSequence() }
                        .asIterable()

                            intersect

                            selectedOtherItemKeys.asSequence()
                                .flatMap { Data.opleiderToExamenlocaties[it]!!.asSequence() }
                                .asIterable()
                    ).asSequence()
                .map { MapEntry(it, itemsData[it] ?: error("Examenlocatie $it does not exist")) }

            else -> error("this is impossible")
        }.forEach { (examNaam, examenlocatie) ->
            for (it in filterTerms) {
                val naam = examNaam.contains(it, true)
                val plaatsnaam = examenlocatie.plaatsnaam.contains(it, true)
                val postcode = examenlocatie.postcode.contains(it, true)
                val straatnaam = examenlocatie.straatnaam.contains(it, true)
                score[examNaam] = (score[examNaam] ?: 0) +
                        naam.toInt() * 3 + plaatsnaam.toInt() * 2 + postcode.toInt() + straatnaam.toInt()
            }
            if (filterTerms.isEmpty()) score[examNaam] = 1
        }

        val result = score.asSequence()
            .filter { it.value != 0 }
            .sortedByDescending { itemsData[it.key]?.let { sortType(it) } ?: error("examenlocatie $it does not exist") }
            .sortedByDescending { it.value }
            .sortedByDescending { it.key in selectedItemKeys }
            .map { itemsData[it.key] ?: error("Examenlocatie $it does not exist") }
            .toList()

        list?.scrollTo(0)
        return result
    }

    private val toggleSelected = { examenlocatie: String, newState: Boolean? ->
        { _: Event ->
            if (newState ?: examenlocatie !in isExamenlocatieSelected) {
                isExamenlocatieSelected += examenlocatie
            } else {
                isExamenlocatieSelected -= examenlocatie
            }
        }
    }

    private val renderRow = { index: Int, key: String ->
        buildElement {
            val examenlocatie = filteredItems[index]
            mListItem(
                button = true,
                selected = examenlocatie.naam in isExamenlocatieSelected,
                key = key,
                divider = false
            ) {
                mListItemAvatar {
                    mAvatar {
                        attrs {
                            onClick = openPopOver(examenlocatie, this)
                        }
                        +examenlocatie.naam.first().toString()
                    }
                }
                mListItemText(examenlocatie.naam) {
                    attrs {
                        onClick = toggleSelected(examenlocatie.naam, null)
                    }
                }
                mCheckbox(checked = examenlocatie.naam in isExamenlocatieSelected) {
                    attrs {
                        onClick = toggleSelected(examenlocatie.naam, null)
                    }
                }
            }
        }
    }


    val openPopOver = fun(examenlocatie: Examenlocatie, mAvatarProps: MAvatarProps): (Event) -> Unit {
        var avatarRef: Node? = null
        mAvatarProps.ref<dynamic> {
            avatarRef = findDOMNode(it)
        }

        return {
            it.preventDefault()
            popoverExamenlocatie = examenlocatie
            popoverAvatar = avatarRef
            popoverOpen = true
        }
    }

    val onPopoverClose: (Event, ModalOnCloseReason) -> Unit = { _, _ ->
        popoverOpen = false
    }

    val onPieCategorieClicked: (Categorie) -> Unit = {
        popoverOpen = false
        onCategorieClicked(it)
    }

    private var popoverExamenlocatie: Examenlocatie? = null
    private var popoverAvatar: Node? = null

    override fun RBuilder.render() {
        themeContext.Consumer { theme ->
            val themeStyles = object : StyleSheet("ComponentStyles", isStatic = true) {
                val list by css {
                    width = 320.px
                    backgroundColor = Color(theme.palette.background.paper)
                }
            }

            styledDiv {
                css {
                    padding(1.spacingUnits)
                    overflow = Overflow.auto
                    height = 400.px
                    minHeight = 400.px
                    maxHeight = 400.px
                }
                styledReactList {
                    css(themeStyles.list)
                    attrs {
                        length = filteredItems.size
                        itemRenderer = renderRow
                        type = "variable"
                        ref {
                            list = it
                        }
                    }
                }
            }

            mPopover(
                open = popoverOpen,
                onClose = onPopoverClose,
                anchorOriginVertical = MPopoverVerticalPosition.top,
                anchorOriginHorizontal = MPopoverHorizontalPosition.right
            ) {
                attrs {
                    anchorEl = popoverAvatar

                    transformOriginVertical = MPopoverVerticalPosition.top
                    transformOriginHorizontal = MPopoverHorizontalPosition.left
                }
                mTable {
                    mTableBody {
                        if (popoverExamenlocatie == null) return@mTableBody

                        popoverExamenlocatie!!.content.forEach { (key, element) ->
                            mTableRow(key = key) {
                                mTableCell { +key }
                                mTableCell { +element }
                            }
                        }
                    }
                }

                styledH4 {
                    css {
                        marginLeft = 10.px
                        textAlign = TextAlign.center
                    }
                    +"Verdeling categoriën"
                }

                if (popoverExamenlocatie == null) return@mPopover

                categoriePieChart {
                    attrs {
                        examenlocatie = popoverExamenlocatie
                        onCategorieClicked = onPieCategorieClicked
                    }
                }
            }

            styledDiv {
                css {
                    padding(
                        left = 5.spacingUnits,
                        right = 5.spacingUnits,
                        top = 2.spacingUnits
                    )
                }
                mTypography {
                    +"  ⬑   Klik op een avatar voor meer info!"
                }
                mTypography {
                    +"Resultaten worden o.a. gesorteerd op gemiddeld totaal slagingspercentage"
                }
            }
            Unit
        }
    }
}

val examenlocatiesList: RBuilder.(handler: ExamenlocatiesListProps.() -> Unit) -> ReactElement =
    { handler ->
        child(ExamenlocatiesList::class) {
            attrs(handler)
        }
    }
