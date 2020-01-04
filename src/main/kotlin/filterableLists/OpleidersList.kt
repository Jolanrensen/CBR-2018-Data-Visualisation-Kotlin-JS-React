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
import data.Opleider
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

class OpleidersList(props: Props) : FilterableList<OpleidersList.Props, OpleidersList.State>(props) {

    interface Props : FilterableListProps<String, Opleider>
    // Available in props:
    // var filter: String
    // var setReloadRef: (ReloadItems) -> Unit
    // var selectedItemKeys: HashSet<String>
    // var onSelectionChanged: () -> Unit
    // var selectedOtherItemKeys: HashSet<String>
    // var filteredItemsDelegate: ReadWriteProperty<Any?, List<Opleider>>

    private var filteredItems by props.filteredItemsDelegate
    private var isOpleiderSelected by props.selectedItemKeysDelegate
    private var isExamenlocatieSelected by props.selectedOtherItemKeysDelegate

    interface State : FilterableListState

    override fun State.init(props: Props) {
        props.setReloadRef(::refreshOpleiders)
        props.setKeyToTypeRef { Data.alleOpleiders[it]!! }
        props.setTypeToKeyRef { it.code }
    }

    private var list: ReactListRef? = null

    private val jobs = hashSetOf<Job>()
    override fun componentWillUnmount() {
        jobs.forEach { it.cancel() }
    }

    private fun refreshOpleiders() {
        CoroutineScope(Dispatchers.Main).launch {
            // println("refreshOpleiders")
            val filterTerms = props.filter.split(" ", ", ", ",")
            val score = hashMapOf<String, Int>()
            (if (isExamenlocatieSelected.isNotEmpty())
                isExamenlocatieSelected.asSequence()
                    .map { Data.examenlocatieToOpleiders[it]!! }
                    .flatten()
                    .map { it to Data.alleOpleiders[it]!! }
                    .toMap()
            else Data.alleOpleiders).forEach { (oplCode, opleider) ->
                filterTerms.forEach {
                    val naam = opleider.naam.contains(it, true)
                    val code = oplCode.contains(it, true)
                    val plaatsnaam = opleider.plaatsnaam.contains(it, true)
                    val postcode = opleider.postcode.contains(it, true)
                    val straatnaam = opleider.straatnaam.contains(it, true)
                    score[oplCode] = (score[oplCode] ?: 0) +
                        naam.toInt() * 3 + code.toInt() + plaatsnaam.toInt() * 2 + postcode.toInt() + straatnaam.toInt()
                }
            }

            val filteredOpleiderCodes: List<String>
            filteredItems = score.asSequence()
                .filter { it.value != 0 }
                .sortedByDescending { it.value }
                .sortedByDescending { it.key in isOpleiderSelected }
                .apply { filteredOpleiderCodes = map { it.key }.toList() }
                .map { Data.alleOpleiders[it.key]!! }
                .toList()

            // deselect all previously selected opleiders that are no longer in filteredOpleiders
            isOpleiderSelected.filter { code ->
                code !in filteredOpleiderCodes
            }.apply {
                forEach { key ->
                    isOpleiderSelected -= key
                }
                if (size > 0) props.onSelectionChanged()
            }

            list?.scrollTo(0)
        }.let {
            jobs.add(it)
        }
    }

    private fun toggleSelected(opleider: String?, newState: Boolean? = null) {
        opleider ?: return
        if (newState ?: opleider !in isOpleiderSelected)
            isOpleiderSelected += opleider
        else
            isOpleiderSelected -= opleider

        props.onSelectionChanged()
    }

    private fun renderRow(index: Int, key: String) = buildElement {
        val opleider = filteredItems[index]
        mListItem(
            button = true,
            selected = opleider.code in isOpleiderSelected,
            key = key,
            divider = false,
            onClick = { toggleSelected(opleider.code) }
        ) {
            mListItemAvatar {
                mAvatar {
                    +opleider.naam.first().toString()
                }
            }
            mListItemText("${opleider.naam}, ${opleider.plaatsnaam} (${opleider.code})")
            mCheckbox(checked = opleider.code in isOpleiderSelected)
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

// as for a fun RBuilder.opleiderslist, its ::filterableLists.getOpleidersList wouldn't be an extension function anymore
val opleidersList: RBuilder.(OpleidersList.Props.() -> Unit) -> ReactElement = { handler ->
    child(OpleidersList::class) {
        attrs(handler)
    }
}
