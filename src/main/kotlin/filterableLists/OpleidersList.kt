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
import data.Opleider
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
import org.w3c.dom.events.Event
import propDelegateOf
import react.RBuilder
import react.ReactElement
import react.buildElement
import styled.StyleSheet
import styled.css
import styled.styledDiv
import toInt

interface OpleidersListProps : FilterableListProps<String, Opleider>
// Available in props:
// var filter: String
// var setReloadRef: (ReloadItems) -> Unit
// var selectedItemKeys: HashSet<String>
// var onSelectionChanged: () -> Unit
// var selectedOtherItemKeys: HashSet<String>
// var filteredItemsDelegate: ReadWriteProperty<Any?, List<Opleider>>

interface OpleidersListState : FilterableListState

class OpleidersList(prps: OpleidersListProps) :
    FilterableList<String, Opleider, OpleidersListProps, OpleidersListState>(prps) {

    private var isOpleiderSelected by propDelegateOf(OpleidersListProps::selectedItemKeys)
    private var isExamenlocatieSelected by propDelegateOf(OpleidersListProps::selectedOtherItemKeys)
    private val filteredItems by propDelegateOf(OpleidersListProps::filteredItems)

    private var list: ReactListRef? = null

    override fun keyToType(key: String, itemsData: Map<String, Opleider>) =
        itemsData[key] ?: error("opleider $key does not exist")

    override fun typeToKey(type: Opleider, itemsData: Map<String, Opleider>) = type.code
    override fun getFilteredItems(filter: String, itemsData: Map<String, Opleider>, selectedItemKeys: Set<String>, selectedOtherItemKeys: Set<String>): List<Opleider> {
        val filterTerms = filter.split(" ", ", ", ",")
        val score = hashMapOf<String, Int>()
        (if (selectedOtherItemKeys.isNotEmpty())
            selectedOtherItemKeys.asSequence()
                .map { Data.examenlocatieToOpleiders[it]!! }
                .flatten()
                .map { it to (itemsData[it] ?: error("opleider $it does not exist")) }
                .toMap()
        else itemsData).forEach { (oplCode, opleider) ->
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

        val result = score.asSequence()
            .filter { it.value != 0 }
            .sortedByDescending { it.value }
            .sortedByDescending { it.key in selectedItemKeys }
            .map { itemsData[it.key] ?: error("opleider $it does not exist") }
            .toList()

        list?.scrollTo(0)

        return result
    }

    private val toggleSelected = { opleider: String?, newState: Boolean? ->
        { _: Event ->
            if (opleider != null) {
                if (newState ?: opleider !in isOpleiderSelected)
                    isOpleiderSelected += opleider
                else
                    isOpleiderSelected -= opleider
            }
        }
    }

    private val renderRow = { filteredItems: List<Opleider> ->
        { index: Int, key: String ->
            buildElement {
                val opleider = filteredItems[index]
                mListItem(
                    button = true,
                    selected = opleider.code in isOpleiderSelected,
                    key = key,
                    divider = false,
                    onClick = toggleSelected(opleider.code, null)
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
                        itemRenderer = renderRow(filteredItems)
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
val opleidersList: RBuilder.(OpleidersListProps.() -> Unit) -> ReactElement = { handler ->
    child(OpleidersList::class) {
        attrs(handler)
    }
}
