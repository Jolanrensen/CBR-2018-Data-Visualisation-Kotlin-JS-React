import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.list.*
import data.Opleider
import kotlinext.js.js
import kotlinext.js.jsObject
import kotlinx.css.*
import kotlinx.html.style
import libs.*
import react.*
import react.children
import react.dom.div
import react.dom.p
import styled.*
import styled.StyledComponents.css

class OpleidersList(props: Props) : RComponent<OpleidersList.Props, OpleidersList.State>(props) {

    interface Props : RProps {
        var setOpleidersRef: ((opleiders: List<Opleider>) -> Unit) -> Unit
        var setFilterRef: ((filter: String) -> Unit) -> Unit
    }

    interface State : RState {
        var opleiders: List<Opleider>
        var selected: HashMap<String, Boolean>
        var filter: String
    }

    override fun State.init(props: Props) {
        props.setOpleidersRef(::setOpleiders)
        props.setFilterRef(::setFilter)
        opleiders = listOf()
        selected = hashMapOf()
        filter = ""
    }


    fun setFilter(filter: String) {
        setState {
            this.filter = filter
            setOpleiders(state.opleiders)
        }
    }

    fun setOpleiders(opleiders: List<Opleider>) {
        setState {
            this.opleiders = opleiders.apply { } // TODO FILTER and deselect rijscholen die nu verdwenen zijn
            this.selected =
                hashMapOf(*opleiders.map { it.code to false }.toTypedArray())// by default deselect all opleiders
        }
    }

    fun toggleSelected(opleider: String?, newState: Boolean? = null) {
        opleider ?: return
        setState {
            selected[opleider] = newState ?: !selected[opleider]!!
        }
    }

    fun renderRow(index: Int, key: String) = buildElement {
        val opleider = state.opleiders[index]
        mListItem(
            button = true,
            selected = state.selected[opleider.code] ?: false,
            key = key,
            divider = false,
            onClick = { toggleSelected(opleider.code) }
        ) {
            mListItemAvatar {
                mAvatar {
                    +opleider.naam.first().toString()
                }
            }
            mListItemText("${opleider.naam} (${opleider.code})")
            mCheckbox(
                checked = state.selected[opleider.code] ?: false,
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
                    display = Display.inlineFlex
                    overflow = Overflow.auto
                    maxHeight = 400.px
                }
                styledReactList {
                    css(themeStyles.list)
                    attrs {
                        length = state.opleiders.size
                        itemRenderer = ::renderRow
                        type = "uniform"
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