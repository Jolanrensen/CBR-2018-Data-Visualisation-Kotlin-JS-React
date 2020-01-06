
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
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemText
import com.ccfraser.muirwik.components.mCheckbox
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
import react.ref
import styled.css

abstract class FilterList<Key : Any, Type : Any?>(props: Props<Key, Type>) :
    RComponent<FilterList.Props<Key, Type>, FilterList.State>(props) {

    interface Props<Key : Any, Type: Any?> : RProps {
        var filterableListCreationFunction: CreateFilterableList<Key, Type>
        var liveReload: Boolean
        var itemsName: String

        // var setReloadRef: (ReloadItems) -> Unit
        var selectedItemKeysDelegate: VarDelegate<Set<Key>>
        var selectedOtherItemKeysDelegate: VarDelegate<Set<Key>>
        var onSelectionChanged: () -> Unit
        var alwaysAllowSelectAll: Boolean

        var itemsDataDelegate: ValDelegate<Map<Key, Type>>
    }

    private var selectedItemKeys by props.selectedItemKeysDelegate
    private var selectedOtherItemKeys by props.selectedOtherItemKeysDelegate

    interface State : RState {
        var filter: String
        // var reload: ReloadItems
        // var filteredItems: List<Type>
    }

    override fun State.init(props: Props<Key, Type>) {
        filter = ""
        // reload = {}
        // filteredItems = listOf()
    }

    private val filterDelegate = delegateOf(state::filter)
    private var filter by filterDelegate

    // private val filteredItemsDelegate = delegateOf(state::filteredItems)
    // private var filteredItems by filteredItemsDelegate

    private var filterableList: FilterableList<Key, Type, *, *>? = null
    private fun getFilteredItems() = filterableList?.getFilteredItems() ?: listOf()


    private fun typeToKey(type: Type) = filterableList?.typeToKey(type)
    private fun keyToType(key: Key) = filterableList?.keyToType(key)

    private fun toggleSelectAllVisible() {
        val filteredItems = getFilteredItems()
        if (!filteredItems.all { typeToKey(it) in selectedItemKeys }) {
            selectedItemKeys += filteredItems.map { typeToKey(it)!! }
        } else {
            selectedItemKeys -= filteredItems.map { typeToKey(it)!! }
        }

        props.onSelectionChanged()
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
                            filter = it.targetInputValue
                            // if (props.liveReload) state.reload()

                        }
                    ) {
                        attrs {
                            margin = MInputMargin.dense
                            onKeyPress = {
                                when (it.key) {
                                    "Enter" -> {
                                        it.preventDefault()
                                        // state.reload()
                                    }
                                }
                            }
                            endAdornment = mInputAdornment(position = MInputAdornmentPosition.end) {
                                mIconButton(
                                    iconName = "search",
                                    onClick = { /*state.reload()*/ },
                                    edge = MIconEdge.end
                                )
                            }
                        }
                    }
                }
            }

            if (filter.isNotBlank() || selectedOtherItemKeys.isNotEmpty() || props.alwaysAllowSelectAll) {
                mGridItem(xs = MGridSize.cells12) {
                    mListItem(
                        button = true,
                        divider = true,
                        dense = true,
                        onClick = { toggleSelectAllVisible() }
                    ) {
                        mListItemText("(De)selecteer alle${
                        if (props.alwaysAllowSelectAll && selectedOtherItemKeys.isEmpty() && filter.isBlank()) 
                            " " else " gefilterde "}${props.itemsName}")
                        val filteredItems = getFilteredItems()
                        mCheckbox(
                            checked = filteredItems
                                .asSequence()
                                .map { typeToKey(it) }
                                .any { it in selectedItemKeys },
                            indeterminate = filteredItems
                                .asSequence()
                                .map { typeToKey(it) }
                                .run {
                                    !all { it in selectedItemKeys } && any { it in selectedItemKeys }
                                }
                        )
                    }
                }
            }

            mGridItem(xs = MGridSize.cells12) {
                props.filterableListCreationFunction(this) {
                    // filteredItemsDelegate = this@FilterList.filteredItemsDelegate
                    selectedItemKeysDelegate = props.selectedItemKeysDelegate
                    selectedOtherItemKeysDelegate = props.selectedOtherItemKeysDelegate

                    ref<FilterableList<Key, Type, *, *>> {
                        filterableList = it
                    }

                    // setReloadRef = {
                    //     setState {
                    //         reload = it
                    //     }
                    //     props.setReloadRef(it)
                    // }
                    // setKeyToTypeRef = {
                    //     keyToType = it
                    // }
                    // setTypeToKeyRef = {
                    //     typeToKey = it
                    // }

                    filter = this@FilterList.filter

                    onSelectionChanged = props.onSelectionChanged
                    itemsDataDelegate = props.itemsDataDelegate

                }
            }
        }
    }
}

fun <Key : Any, Type : Any?> RBuilder.filterList(
    type: CreateFilterableList<Key, Type>,
    itemsName: String = "items",
    handler: FilterList.Props<Key, Type>.() -> Unit
) =
    child<FilterList.Props<Key, Type>, FilterList<Key, Type>> {
        attrs {
            filterableListCreationFunction = type
            liveReload = true
            this.itemsName = itemsName
            alwaysAllowSelectAll = false
        }
        attrs(handler)
    }
