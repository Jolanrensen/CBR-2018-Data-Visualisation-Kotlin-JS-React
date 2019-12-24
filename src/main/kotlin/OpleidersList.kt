import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.list.*
import data.Data
import data.Opleider
import kotlinx.css.*
import libs.*
import react.*
import styled.*
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0

class OpleidersList(props: Props) : RComponent<OpleidersList.Props, OpleidersList.State>(props) {

    interface Props : RProps {
        var filterDelegate: ReadOnlyProperty<Any?, String>
        var setRefreshOpleidersRef: (RefreshOpleiders) -> Unit
        var filteredOpleidersDelegate: ReadWriteProperty<Any?, List<Opleider>>
        var isOpleiderSelectedDelegate: ReadOnlyProperty<Any?, HashMap<String, Boolean>>
        var onSelectedOpleiderChanged: () -> Unit
        var isExamenlocatieSelectedDelegate: ReadOnlyProperty<Any?, HashMap<String, Boolean>>
    }

    private val filter by props.filterDelegate
    private var filteredOpleiders by props.filteredOpleidersDelegate // Cleaner way than passing along 2 functions for every prop
    private val isOpleiderSelected by props.isOpleiderSelectedDelegate
    private val isExamenlocatieSelected by props.isExamenlocatieSelectedDelegate

    interface State : RState

    override fun State.init(props: Props) {
        props.setRefreshOpleidersRef(::refreshOpleiders)
    }

    private var list: ReactListRef? = null

    private fun refreshOpleiders() {
        val filterTerms = filter.split(" ", ", ", ",")
        val score = hashMapOf<String, Int>()
        (if (isExamenlocatieSelected.values.any { it })
            isExamenlocatieSelected.asSequence()
                .filter { it.value }
                .map { Data.examenlocatieToOpleiders[it.key]!! }
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
        setState {
            val filteredOpleiderCodes: List<String>
            filteredOpleiders = score.asSequence()
                .filter { it.value != 0 }
                .sortedByDescending { it.value }
                .sortedByDescending { isOpleiderSelected[it.key] ?: false }
                .apply { filteredOpleiderCodes = map { it.key }.toList() }
                .map { Data.alleOpleiders[it.key]!! }
                .toList()

            // deselect all previously selected opleiders that are no longer in filteredOpleiders
            isOpleiderSelected.filter { (code, selected) ->
                selected && code !in filteredOpleiderCodes
            }.apply {
                forEach { (key, _) ->
                    isOpleiderSelected[key] = false
                }
                if (size > 0) props.onSelectedOpleiderChanged()
            }
        }

        list?.scrollTo(0)
    }

    private fun toggleSelected(opleider: String?, newState: Boolean? = null) {
        opleider ?: return
        setState {
            isOpleiderSelected[opleider] = newState ?: !(isOpleiderSelected[opleider] ?: false)
            props.onSelectedOpleiderChanged()
        }
    }

    private fun renderRow(index: Int, key: String) = buildElement {
        val opleider = filteredOpleiders[index]
        mListItem(
            button = true,
            selected = isOpleiderSelected[opleider.code] ?: false,
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
            mCheckbox(
                checked = isOpleiderSelected[opleider.code] ?: false,
                onChange = { _, newState -> toggleSelected(opleider.code, newState) })
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
//                    display = Display.inlineFlex
                    overflow = Overflow.auto
                    maxHeight = 400.px
                }
                styledReactList {
                    css(themeStyles.list)
                    attrs {
                        length = filteredOpleiders.size
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

fun RBuilder.opleidersList(handler: OpleidersList.Props.() -> Unit) =
    child(OpleidersList::class) {
        attrs(handler)
    }

typealias RefreshOpleiders = () -> Unit