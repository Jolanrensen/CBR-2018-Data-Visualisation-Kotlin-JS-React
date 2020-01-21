package filterableLists

import FilterableList
import FilterableListProps
import FilterableListState
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemAvatar
import com.ccfraser.muirwik.components.list.mListItemText
import com.ccfraser.muirwik.components.mAvatar
import com.ccfraser.muirwik.components.mCheckbox
import com.ccfraser.muirwik.components.spacingUnits
import com.ccfraser.muirwik.components.themeContext
import data.Data
import data.Examenlocatie
import kotlinx.css.Color
import kotlinx.css.Overflow
import kotlinx.css.backgroundColor
import kotlinx.css.maxHeight
import kotlinx.css.overflow
import kotlinx.css.padding
import kotlinx.css.px
import kotlinx.css.width
import libs.reactList.ReactListRef
import libs.reactList.ref
import libs.reactList.styledReactList
import propDelegateOf
import react.RBuilder
import react.ReactElement
import react.buildElement
import readOnlyPropDelegateOf
import styled.StyleSheet
import styled.css
import styled.styledDiv
import toInt

interface ExamenlocatiesListProps : FilterableListProps<String, Examenlocatie>
// Available in props:
// var filter: String
// var setReloadRef: (ReloadItems) -> Unit
// var selectedItemKeys: HashSet<String>
// var onSelectionChanged: () -> Unit
// var selectedOtherItemKeys: HashSet<String>
// var filteredItemsDelegate: ReadWriteProperty<Any?, List<Examenlocatie>>

interface ExamenlocatiesListState : FilterableListState

class ExamenlocatiesList(prps: ExamenlocatiesListProps) :
    FilterableList<String, Examenlocatie, ExamenlocatiesListProps, ExamenlocatiesListState>(prps) {

    override fun keyToType(key: String) = alleExamenlocatiesData[key] ?: error("Examenlocatie $key does not exist")
    override fun typeToKey(type: Examenlocatie) = type.naam

    private var isExamenlocatieSelected by propDelegateOf(ExamenlocatiesListProps::selectedItemKeys)
    private val isOpleiderSelected by propDelegateOf(ExamenlocatiesListProps::selectedOtherItemKeys)
    private val alleExamenlocatiesData by readOnlyPropDelegateOf(ExamenlocatiesListProps::itemsData)
    private val filter by readOnlyPropDelegateOf(ExamenlocatiesListProps::filter)
    private val onSelectionChanged by readOnlyPropDelegateOf(ExamenlocatiesListProps::onSelectionChanged)

    override fun ExamenlocatiesListState.init(props: ExamenlocatiesListProps) {}

    private var list: ReactListRef? = null

    override fun getFilteredItems(): List<Examenlocatie> {
        // println("refreshExamenlocations")
        val filterTerms = filter.split(" ", ", ", ",")
        val score = hashMapOf<String, Int>()
        (if (isOpleiderSelected.isNotEmpty())
            isOpleiderSelected.asSequence()
                .map { Data.opleiderToExamenlocaties[it]!! }
                .flatten()
                .map { it to (alleExamenlocatiesData[it] ?: error("Examenlocatie $it does not exist")) }
                .toMap()
        else alleExamenlocatiesData).forEach { (examNaam, examenlocatie) ->
            filterTerms.forEach {
                val naam = examNaam.contains(it, true)
                val plaatsnaam = examenlocatie.plaatsnaam.contains(it, true)
                val postcode = examenlocatie.postcode.contains(it, true)
                val straatnaam = examenlocatie.straatnaam.contains(it, true)
                score[examNaam] = (score[examNaam] ?: 0) +
                    naam.toInt() * 3 + plaatsnaam.toInt() * 2 + postcode.toInt() + straatnaam.toInt()
            }
        }

        val filteredExamenlocatieCodes: List<String>
        val result = score.asSequence()
            .filter { it.value != 0 }
            .sortedByDescending { it.value }
            .sortedByDescending { it.key in isExamenlocatieSelected }
            .apply { filteredExamenlocatieCodes = map { it.key }.toList() }
            .map { alleExamenlocatiesData[it.key] ?: error("Examenlocatie $it does not exist") }
            .toList()

        // deselect all previously selected examenlocaties that are no longer in filteredExamenlocaties
        isExamenlocatieSelected.filter { naam ->
            naam !in filteredExamenlocatieCodes
        }.apply {
            forEach { key ->
                isExamenlocatieSelected -= key
            }
            if (size > 0) onSelectionChanged()
        }

        list?.scrollTo(0)
        return result
    }

    private fun toggleSelected(examenlocatie: String, newState: Boolean? = null) {
        if (newState ?: examenlocatie !in isExamenlocatieSelected) {
            isExamenlocatieSelected += examenlocatie
        } else {
            isExamenlocatieSelected -= examenlocatie
        }

        onSelectionChanged()
    }

    private fun renderRow(filteredItems: List<Examenlocatie>, index: Int, key: String) = buildElement {
        val examenlocatie = filteredItems[index]
        mListItem(
            button = true,
            selected = examenlocatie.naam in isExamenlocatieSelected,
            key = key,
            divider = false,
            onClick = { toggleSelected(examenlocatie.naam) }
        ) {
            mListItemAvatar {
                mAvatar {
                    +examenlocatie.naam.first().toString()
                }
            }
            mListItemText(examenlocatie.naam)
            mCheckbox(checked = examenlocatie.naam in isExamenlocatieSelected)
        }
    }

    override fun RBuilder.render() {
        val filteredItems = getFilteredItems()
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
                        itemRenderer = { index, key ->
                            renderRow(filteredItems, index, key)
                        }
                        type = "variable"
                        ref {
                            list = it
                        }
                    }
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
