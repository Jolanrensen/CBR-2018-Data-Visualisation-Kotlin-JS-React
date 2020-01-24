import com.ccfraser.muirwik.components.*
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
import kotlinx.css.*
import kotlinx.html.InputType
import react.*
import styled.css
import styled.styledDiv

interface FilterListProps<Key : Any, Type : Any?> : RProps {
    var filterableListCreationFunction: CreateFilterableList<Key, Type>
    var liveReload: Boolean
    var itemsName: String
    var selectedItemKeys: StateAsProp<Set<Key>>
    var selectedOtherItemKeys: StateAsProp<Set<Key>>
    var onSelectionChanged: () -> Unit
    var alwaysAllowSelectAll: Boolean

    var dataLoaded: Boolean

    var itemsData: Map<Key, Type>
}

interface FilterListState : RState {
    var filter: String
}

class FilterList<Key : Any, Type : Any?>(prps: FilterListProps<Key, Type>) :
    RComponent<FilterListProps<Key, Type>, FilterListState>(prps) {

    private var selectedItemKeys by propDelegateOf(FilterListProps<Key, Type>::selectedItemKeys)
    private var selectedOtherItemKeys by propDelegateOf(FilterListProps<Key, Type>::selectedOtherItemKeys)
    private val dataLoaded by readOnlyPropDelegateOf(FilterListProps<Key, Type>::dataLoaded)

    override fun FilterListState.init(props: FilterListProps<Key, Type>) {
        filter = ""
    }

    private var filter by stateDelegateOf(FilterListState::filter)

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
                    var currentFilter = ""
                    mFilledInput(
                        id = "filled-adornment-filter",
                        type = InputType.text,
                        onChange = {
                            it.persist()
                            currentFilter = it.targetInputValue
                        }
                    ) {
                        attrs {
                            margin = MInputMargin.dense
                             onKeyPress = {
                                 when (it.key) {
                                     "Enter" -> {
                                         it.preventDefault()
                                         filter = currentFilter
                                     }
                                 }
                             }
                            endAdornment = mInputAdornment(position = MInputAdornmentPosition.end) {
                                mIconButton(
                                    iconName = "search",
                                     onClick = {
                                         filter = currentFilter
                                     },
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
                if (dataLoaded)
                    props.filterableListCreationFunction(this) {
                        selectedItemKeys = props.selectedItemKeys
                        selectedOtherItemKeys = props.selectedOtherItemKeys

                        ref<FilterableList<Key, Type, *, *>> {
                            filterableList = it
                        }

                        filter = this@FilterList.filter

                        onSelectionChanged = props.onSelectionChanged
                        itemsData = props.itemsData

                    }
                else styledDiv {
                    css {
                        display = Display.flex
                        justifyContent = JustifyContent.center
                    }
                    mCircularProgress()
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
