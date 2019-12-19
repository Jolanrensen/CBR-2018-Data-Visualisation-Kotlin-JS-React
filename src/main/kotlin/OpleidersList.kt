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
import styled.StyleSheet
import styled.StyledComponents.css
import styled.css
import styled.styled
import styled.styledDiv

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
                hashMapOf(*opleiders.map { it.code to true }.toTypedArray())// by default select all opleiders
        }
    }

    fun toggleSelected(opleider: String?) {
        opleider ?: return
        setState {
            selected[opleider] = !selected[opleider]!!
        }
    }

    @Suppress("UnsafeCastFromDynamic")
    val renderRow: FunctionalComponent<RenderProps<List<Opleider>>> = functionalComponent { props ->
        val opleider = props.data?.get(props.index)
        mListItem(
            primaryText = "${opleider?.naam} (${opleider?.code})",
            selected = state.selected[opleider?.code] ?: true,
            key = props.index.toString(),
            divider = true,
            onClick = { toggleSelected(opleider?.code) }
        ) {
            attrs {
                style = props.style
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
//                    display = Display.inlineFlex
                    padding(1.spacingUnits)
                }


//                mList {
//                    css(themeStyles.list)
//                    mListSubheader("\"Full\" ListItemAvatar (more code)", disableSticky = true)
//
//
//                }

                styled(FixedSizeListOpleider)() {
                    css {
                        width = 320.px
                        height = 400.px
                        maxWidth = 360.px
                        backgroundColor = Color(theme.palette.background.paper)

                    }
                    attrs {
                        height = 400
                        width = 400
                        itemSize = 100
                        itemCount = state.opleiders.size
                        itemData = state.opleiders
                        children = renderRow
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