
import com.ccfraser.muirwik.components.MGridSize
import com.ccfraser.muirwik.components.button.MIconEdge
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.form.MFormControlMargin
import com.ccfraser.muirwik.components.form.MFormControlVariant
import com.ccfraser.muirwik.components.form.MLabelMargin
import com.ccfraser.muirwik.components.form.mFormControl
import com.ccfraser.muirwik.components.input.MInputAdornmentPosition
import com.ccfraser.muirwik.components.input.MInputMargin
import com.ccfraser.muirwik.components.input.mFilledInput
import com.ccfraser.muirwik.components.input.mInputAdornment
import com.ccfraser.muirwik.components.input.mInputLabel
import com.ccfraser.muirwik.components.input.margin
import com.ccfraser.muirwik.components.mGridContainer
import com.ccfraser.muirwik.components.mGridItem
import com.ccfraser.muirwik.components.persist
import com.ccfraser.muirwik.components.targetInputValue
import kotlinx.css.LinearDimension
import kotlinx.css.padding
import kotlinx.html.InputType
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.setState
import styled.css

class FilterList<Key: Any>(props: Props<Key>) : RComponent<FilterList.Props<Key>, FilterList.State>(props) {

    interface Props<Key: Any> : RProps {
        var filterableListCreationFunction: CreateFilterableList<Key>
        var liveReload: Boolean
        var itemsName: String

        var setReloadRef: (ReloadItems) -> Unit
        var selectedItemKeys: HashSet<Key>
        var selectedOtherItemKeys: HashSet<Key>
        var onSelectionChanged: () -> Unit
    }

    interface State : RState {
        var filter: String
        var reload: ReloadItems
    }

    override fun State.init(props: Props<Key>) {
        filter = ""
        reload = {}
    }

    override fun RBuilder.render() {
        mGridContainer {
            mGridItem(xs = MGridSize.cells12) {
                mFormControl(
                    variant = MFormControlVariant.filled,
                    fullWidth = true,
                    margin = MFormControlMargin.normal
                ) {
                    css {
                        padding(LinearDimension.contentBox)
                    }
                    mInputLabel(
                        htmlFor = "filled-adornment-filter",
                        caption = "Filter ${props.itemsName}",
                        margin = MLabelMargin.dense
                    )
                    mFilledInput(
                        id = "filled-adornment-filter",
                        type = InputType.text,
                        onChange = {
                            it.persist()
                            setState {
                                filter = it.targetInputValue
                                if (props.liveReload) state.reload()
                            }
                        }
                    ) {
                        attrs {
                            margin = MInputMargin.dense
                            onKeyPress = {
                                when (it.key) {
                                    "Enter" -> {
                                        it.preventDefault()
                                        state.reload()
                                    }
                                }
                            }
                            endAdornment = mInputAdornment(position = MInputAdornmentPosition.end) {
                                mIconButton(
                                    iconName = "search",
                                    onClick = { state.reload() },
                                    edge = MIconEdge.end
                                )
                            }
                        }
                    }
                }
            }
            mGridItem(xs = MGridSize.cells12) {
                props.filterableListCreationFunction(this) {
                    setReloadRef = {
                        setState {
                            reload = it
                        }
                        props.setReloadRef(it)
                    }
                    filter = state.filter
                    selectedItemKeys = props.selectedItemKeys
                    selectedOtherItemKeys = props.selectedOtherItemKeys
                    onSelectionChanged = props.onSelectionChanged

                }
            }
        }
    }
}

fun <Key: Any> RBuilder.filterList(type: CreateFilterableList<Key>, itemsName: String = "items", handler: FilterList.Props<Key>.() -> Unit) =
    child<FilterList.Props<Key>, FilterList<Key>> {
        attrs {
            filterableListCreationFunction = type
            liveReload = true
            this.itemsName = itemsName
        }
        attrs(handler)
    }
