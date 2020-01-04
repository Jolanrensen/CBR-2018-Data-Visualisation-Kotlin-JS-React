package filterableLists

import FilterableList
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemAvatar
import com.ccfraser.muirwik.components.list.mListItemText
import com.ccfraser.muirwik.components.mAvatar
import com.ccfraser.muirwik.components.mCheckbox
import com.ccfraser.muirwik.components.spacingUnits
import com.ccfraser.muirwik.components.themeContext
import data.Data
import data.Examenlocatie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.css.Color
import kotlinx.css.Overflow
import kotlinx.css.backgroundColor
import kotlinx.css.maxHeight
import kotlinx.css.overflow
import kotlinx.css.padding
import kotlinx.css.px
import kotlinx.css.width
import libs.ReactListRef
import libs.ref
import libs.styledReactList
import react.RBuilder
import react.ReactElement
import react.buildElement
import styled.StyleSheet
import styled.css
import styled.styledDiv
import toInt

class ExamenlocatiesList(props: Props) :
    FilterableList<ExamenlocatiesList.Props, ExamenlocatiesList.State>(props) {

    interface Props : FilterableListProps<String, Examenlocatie>
    // Available in props:
    // var filter: String
    // var setReloadRef: (ReloadItems) -> Unit
    // var selectedItemKeys: HashSet<String>
    // var onSelectionChanged: () -> Unit
    // var selectedOtherItemKeys: HashSet<String>
    // var filteredItemsDelegate: ReadWriteProperty<Any?, List<Examenlocatie>>

    // not sure why "by props.filteredItemsDelegate" doesn't work
    // private var filteredItems: List<Examenlocatie>
    //     get() = props.filteredItemsDelegate.getValue(this, ::filteredItems)
    //     set(value) = props.filteredItemsDelegate.setValue(this, ::filteredItems, value)

    var filteredItems by props.filteredItemsDelegate

    private var isExamenlocatieSelected by props.selectedItemKeysDelegate
    private val isOpleiderSelected by props.selectedOtherItemKeysDelegate

    interface State : FilterableListState

    override fun State.init(props: Props) {
        props.setReloadRef(::refreshExamenlocaties)
        props.setKeyToTypeRef { Data.alleExamenlocaties[it]!! }
        props.setTypeToKeyRef { it.naam }
    }

    private var list: ReactListRef? = null

    private val jobs = hashSetOf<Job>()
    override fun componentWillUnmount() {
        jobs.forEach { it.cancel() }
    }

    private fun refreshExamenlocaties() {
        CoroutineScope(Dispatchers.Main).launch {
            // println("refreshExamenlocations")
            val filterTerms = props.filter.split(" ", ", ", ",")
            val score = hashMapOf<String, Int>()
            (if (isOpleiderSelected.isNotEmpty())
                isOpleiderSelected.asSequence()
                    .map { Data.opleiderToExamenlocaties[it]!! }
                    .flatten()
                    .map { it to Data.alleExamenlocaties[it]!! }
                    .toMap()
            else Data.alleExamenlocaties).forEach { (examNaam, examenlocatie) ->
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
            filteredItems = score.asSequence()
                .filter { it.value != 0 }
                .sortedByDescending { it.value }
                .sortedByDescending { it.key in isExamenlocatieSelected }
                .apply { filteredExamenlocatieCodes = map { it.key }.toList() }
                .map { Data.alleExamenlocaties[it.key]!! }
                .toList()

            // deselect all previously selected examenlocaties that are no longer in filteredExamenlocaties
            isExamenlocatieSelected.filter { naam ->
                naam !in filteredExamenlocatieCodes
            }.apply {
                forEach { key ->
                    isExamenlocatieSelected -= key
                }
                if (size > 0) props.onSelectionChanged()
            }


            list?.scrollTo(0)
        }.let {
            jobs.add(it)
        }
    }

    private fun toggleSelected(examenlocatie: String, newState: Boolean? = null) {
        if (newState ?: examenlocatie !in isExamenlocatieSelected) {
            isExamenlocatieSelected += examenlocatie
        } else {
            isExamenlocatieSelected -= examenlocatie
        }

        props.onSelectionChanged()
    }

    private fun renderRow(index: Int, key: String) = buildElement {
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
            mListItemText("${examenlocatie.naam}, ${examenlocatie.plaatsnaam} (${examenlocatie.naam})")
            mCheckbox(checked = examenlocatie.naam in isExamenlocatieSelected)
        }
    }

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
                        itemRenderer = ::renderRow
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

val examenlocatiesList: RBuilder.(ExamenlocatiesList.Props.() -> Unit) -> ReactElement = { handler ->
    child(ExamenlocatiesList::class) {
        attrs(handler)
    }
}
