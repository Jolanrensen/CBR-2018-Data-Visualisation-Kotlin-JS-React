
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

interface FilterListProps<Key : Any, Type : Any?> : RProps {
    var filterableListCreationFunction: CreateFilterableList<Key, Type>
    var liveReload: Boolean
    var itemsName: String
    var selectedItemKeysDelegate: VarDelegate<Set<Key>>
    var selectedOtherItemKeysDelegate: VarDelegate<Set<Key>>
    var onSelectionChanged: () -> Unit
    var alwaysAllowSelectAll: Boolean

    var itemsDataDelegate: ValDelegate<Map<Key, Type>>
}

interface FilterListState : RState {
    var filter: String
}

class FilterList<Key : Any, Type : Any?>(props: FilterListProps<Key, Type>) :
    RComponent<FilterListProps<Key, Type>, FilterListState>(props) {

    private var selectedItemKeys by props.selectedItemKeysDelegate
    private var selectedOtherItemKeys by props.selectedOtherItemKeysDelegate

    override fun FilterListState.init(props: FilterListProps<Key, Type>) {
        filter = ""
    }

    private val filterDelegate = delegateOf(state::filter)
    private var filter by filterDelegate

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
                        }
                    ) {
                        attrs {
                            margin = MInputMargin.dense
                            // onKeyPress = {
                            //     when (it.key) {
                            //         "Enter" -> {
                            //             it.preventDefault()
                            //         }
                            //     }
                            // }
                            endAdornment = mInputAdornment(position = MInputAdornmentPosition.end) {
                                mIconButton(
                                    iconName = "search",
                                    // onClick = { /*state.reload()*/ },
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
                        mListItemText(
                            "(De)selecteer alle${
                            if (props.alwaysAllowSelectAll && selectedOtherItemKeys.isEmpty() && filter.isBlank())
                                " " else " gefilterde "}${props.itemsName}"
                        )
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
                    selectedItemKeysDelegate = props.selectedItemKeysDelegate
                    selectedOtherItemKeysDelegate = props.selectedOtherItemKeysDelegate

                    ref<FilterableList<Key, Type, *, *>> {
                        filterableList = it
                    }

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
    handler: FilterListProps<Key, Type>.() -> Unit
) =
    child<FilterListProps<Key, Type>, FilterList<Key, Type>> {
        attrs {
            filterableListCreationFunction = type
            liveReload = true
            this.itemsName = itemsName
            alwaysAllowSelectAll = false
        }
        attrs(handler)
    }
