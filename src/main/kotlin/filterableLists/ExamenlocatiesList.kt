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
import data.Data
import data.Examenlocatie
import data.Opleider
import kotlinx.css.Color
import kotlinx.css.Overflow
import kotlinx.css.backgroundColor
import kotlinx.css.maxHeight
import kotlinx.css.overflow
import kotlinx.css.padding
import kotlinx.css.px
import kotlinx.css.width
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import libs.reactList.ReactListRef
import libs.reactList.ref
import libs.reactList.styledReactList
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import propDelegateOf
import react.RBuilder
import react.ReactElement
import react.buildElement
import react.dom.findDOMNode
import react.ref
import stateDelegateOf
import styled.StyleSheet
import styled.css
import styled.styledDiv
import toInt

interface ExamenlocatiesListProps : FilterableListProps<String, Examenlocatie>
// Availhttps://en.wikipedia.org/wiki/Material_conditionalable in props:
// var filter: String
// var setReloadRef: (ReloadItems) -> Unit
// var selectedItemKeys: HashSet<String>
// var onSelectionChanged: () -> Unit
// var selectedOtherItemKeys: HashSet<String>
// var filteredItemsDelegate: ReadWriteProperty<Any?, List<Examenlocatie>>

interface ExamenlocatiesListState : FilterableListState

class ExamenlocatiesList(prps: ExamenlocatiesListProps) :
    FilterableList<String, Examenlocatie, ExamenlocatiesListProps, ExamenlocatiesListState>(prps) {

    private var isExamenlocatieSelected by propDelegateOf(ExamenlocatiesListProps::selectedItemKeys)
    private val isOpleiderSelected by propDelegateOf(ExamenlocatiesListProps::selectedOtherItemKeys)
    private val filteredItems by propDelegateOf(ExamenlocatiesListProps::filteredItems)

    override fun ExamenlocatiesListState.init(props: ExamenlocatiesListProps) {
        popoverOpen = false
    }

    private var popoverOpen by stateDelegateOf(ExamenlocatiesListState::popoverOpen)

    private var list: ReactListRef? = null

    override fun keyToType(key: String, itemsData: Map<String, Examenlocatie>) =
        itemsData[key] ?: error("Examenlocatie $key does not exist")

    override fun typeToKey(type: Examenlocatie, itemsData: Map<String, Examenlocatie>) = type.naam
    override fun getFilteredItems(filter: String, itemsData: Map<String, Examenlocatie>, selectedItemKeys: Set<String>, selectedOtherItemKeys: Set<String>): List<Examenlocatie> {
        // println("refreshExamenlocations")
        val filterTerms = filter.split(" ", ", ", ",")
        val score = hashMapOf<String, Int>()
        (if (selectedOtherItemKeys.isNotEmpty())
            selectedOtherItemKeys.asSequence()
                .map { Data.opleiderToExamenlocaties[it]!! }
                .flatten()
                .map { it to (itemsData[it] ?: error("Examenlocatie $it does not exist")) }
                .toMap()
        else itemsData).forEach { (examNaam, examenlocatie) ->
            filterTerms.forEach {
                val naam = examNaam.contains(it, true)
                val plaatsnaam = examenlocatie.plaatsnaam.contains(it, true)
                val postcode = examenlocatie.postcode.contains(it, true)
                val straatnaam = examenlocatie.straatnaam.contains(it, true)
                score[examNaam] = (score[examNaam] ?: 0) +
                        naam.toInt() * 3 + plaatsnaam.toInt() * 2 + postcode.toInt() + straatnaam.toInt()
            }
        }

        val result = score.asSequence()
            .filter { it.value != 0 }
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

    private val renderRow = { filteredItems: List<Examenlocatie> ->
        { index: Int, key: String ->
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
    }

    val openPopOver = { examenlocatie: Examenlocatie, mAvatarProps: MAvatarProps ->
        var avatarRef: Node? = null
        mAvatarProps.ref<dynamic> {
            avatarRef = findDOMNode(it)
        }
        ({ e: Event ->
            e.preventDefault()
            popoverExamenlocatie = examenlocatie
            popoverAvatar = avatarRef
            popoverOpen = true
        })
    }

    val onPopoverClose: (Event, ModalOnCloseReason) -> Unit = { _, _ ->
        popoverOpen = false
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
                    maxHeight = 400.px
                }
                styledReactList {
                    css(themeStyles.list)
                    attrs {
                        length = filteredItems.size
                        itemRenderer = renderRow(filteredItems)
                        type = "variable"
                        ref {
                            list = it
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
                            Json(JsonConfiguration.Stable).toJson(
                                Examenlocatie.serializer(),
                                popoverExamenlocatie!!
                            ).jsonObject.content.forEach { (key, element) ->
                                mTableRow(key = key) {
                                    mTableCell { +key }
                                    mTableCell { +element.primitive.content }
                                }
                            }
                        }
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
