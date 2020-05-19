import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MIconEdge
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.form.MFormControlVariant
import com.ccfraser.muirwik.components.input.MInputAdornmentPosition
import com.ccfraser.muirwik.components.input.MInputProps
import com.ccfraser.muirwik.components.input.mInputAdornment
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemText
import data.Categorie
import delegates.ReactPropAndStateDelegates
import delegates.ReactPropAndStateDelegates.StateAsProp
import delegates.ReactPropAndStateDelegates.propDelegateOf
import delegates.ReactPropAndStateDelegates.stateDelegateOf
import kotlinext.js.jsObject
import kotlinx.css.Display
import kotlinx.css.JustifyContent
import kotlinx.css.display
import kotlinx.css.justifyContent
import libs.RPureComponent
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import react.*
import styled.css
import styled.styledDiv

interface FilterListProps<Key : Any, Type : Any?> : RProps {
    var filterableListCreationFunction: CreateFilterableList<Key, Type>
    var liveReload: Boolean
    var itemsName: String
    var selectedItemKeys: StateAsProp<Set<Key>>
    var selectedOtherItemKeys: StateAsProp<Set<Key>>
    var alwaysAllowSelectAll: Boolean
    var dataLoaded: Boolean
    var itemsData: Map<Key, Type>
    var setApplyFilterFunction: (ApplyFilter) -> Unit
    var setSelectAllFunction: (SelectAll) -> Unit
    var onCategorieClicked: (Categorie) -> Unit
}

typealias ApplyFilter = (String) -> Unit
typealias SelectAll = () -> Unit

interface FilterListState : RState {
    var filter: String
    var filterFieldValue: String
}

class FilterList<Key : Any, Type : Any?>(prps: FilterListProps<Key, Type>) :
    RPureComponent<FilterListProps<Key, Type>, FilterListState>(prps) {

    private var selectedItemKeys by propDelegateOf(FilterListProps<Key, Type>::selectedItemKeys)
    private val selectedOtherItemKeys by propDelegateOf(FilterListProps<Key, Type>::selectedOtherItemKeys)
    private val dataLoaded by propDelegateOf(FilterListProps<Key, Type>::dataLoaded)
    private val itemsData by propDelegateOf(FilterListProps<Key, Type>::itemsData)

    override fun FilterListState.init(props: FilterListProps<Key, Type>) {
        filter = ""
        filterFieldValue = ""
    }

    override fun componentDidMount() {
        props.setApplyFilterFunction(applyFilter)
        props.setSelectAllFunction(selectAll)
    }

    private val applyFilter: ApplyFilter = {
        if (filter != it) {
            filter = it
            inputField?.value = it
            filterFieldValue = it
            selectedItemKeys = setOf()

            println("filter set to $it")
        } else {
            println("filter was already set to $it")
        }
    }

    private val selectAll: SelectAll = {
        println("select all called!")
        val new = getFilteredItems(filter, itemsData, selectedItemKeys, selectedOtherItemKeys)!!
            .map { typeToKey(it)!! }
            .toSet()

        if (new.any { it !in selectedItemKeys } || selectedItemKeys.any { it !in new })
            selectedItemKeys = new
    }

    private var filter
        get() = state.filter
        set(value) {
            if (value == state.filter) return
            setState {
                filter = value
            }
            val filteredItems = getFilteredItems(value, itemsData, selectedItemKeys, selectedOtherItemKeys)!!

            // deselect all previously selected items that are no longer in filteredItems
            selectedItemKeys.filter {
                keyToType(it) !in filteredItems
            }.let {
                selectedItemKeys -= it
            }
        }

    private var filterFieldValue by stateDelegateOf(FilterListState::filterFieldValue)

    private var filterableList: FilterableList<Key, Type, *, *>? = null

    private fun getFilteredItems(
        filter: String,
        itemsData: Map<Key, Type>,
        selectedItemKeys: Set<Key>,
        selectedOtherItemKeys: Set<Key>
    ) = filterableList?.getFilteredItems(
            filter,
            itemsData,
            selectedItemKeys,
            selectedOtherItemKeys
        )

    private fun sortType(type: Type) = filterableList?.sortType(type)
    private fun typeToKey(type: Type) = filterableList?.typeToKey(type, itemsData)
    private fun keyToType(key: Key) = filterableList?.keyToType(key, itemsData)

    private val toggleSelectAllVisible: (Event?) -> Unit = {
        val filteredItems = getFilteredItems(filter, itemsData, selectedItemKeys, selectedOtherItemKeys)!!
        if (!filteredItems.all { typeToKey(it) in selectedItemKeys }) {
            selectedItemKeys += filteredItems.map { typeToKey(it)!! }
        } else {
            selectedItemKeys -= filteredItems.map { typeToKey(it)!! }
        }
    }

    private var inputField: MInputProps? = null
    private val setInputRef: (ref: MInputProps) -> Unit = { inputField = it }

    private val onTextFieldChange: (Event) -> Unit = {
        it.persist()
        filterFieldValue = it.targetInputValue
    }

    private val onEnterKeyPress: (KeyboardEvent) -> Unit = {
        when (it.key) {
            "Enter" -> {
                it.preventDefault()
                filter = filterFieldValue
            }
        }
    }

    private val onSearchButtonClick: (Event) -> Unit = {
        filter = filterFieldValue
    }

    override fun RBuilder.render() {
        mGridContainer {
            mGridItem(xs = MGridSize.cells12) {
                mTextField(
                    value = null,
                    label = "Filter ${props.itemsName}",
                    variant = MFormControlVariant.filled,
                    fullWidth = true,
                    onChange = onTextFieldChange
                ) {
                    attrs {
                        inputRef = setInputRef
                        onKeyPress = onEnterKeyPress
                        inputProps = jsObject {
                            endAdornment = mInputAdornment(position = MInputAdornmentPosition.end) {
                                mIconButton(
                                    iconName = "search",
                                    onClick = onSearchButtonClick,
                                    edge = MIconEdge.end
                                )
                            }
                        }
                        inputLabelProps = jsObject {
                            shrink = filterFieldValue.isNotEmpty()
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
                        onClick = toggleSelectAllVisible
                    ) {
                        mListItemText(
                            "(De)selecteer alle${
                            if (props.alwaysAllowSelectAll && selectedOtherItemKeys.isEmpty() && filter.isBlank())
                                " " else " gefilterde "}${props.itemsName}"
                        )
                        val filteredItems = getFilteredItems(filter, itemsData, selectedItemKeys, selectedOtherItemKeys)

                        mCheckbox(
                            checked = (filteredItems ?: listOf())
                                .asSequence()
                                .map { typeToKey(it) }
                                .any { it in selectedItemKeys },
                            indeterminate = (filteredItems ?: listOf())
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
                if (dataLoaded) {
                    props.filterableListCreationFunction(this) {
                        selectedItemKeys = props.selectedItemKeys
                        selectedOtherItemKeys = props.selectedOtherItemKeys
                        onCategorieClicked = props.onCategorieClicked

                        ref<FilterableList<Key, Type, *, *>> {
                            filterableList = it
                        }

                        filteredItems = this@FilterList.run {
                            getFilteredItems(
                                filter,
                                itemsData,
                                selectedItemKeys,
                                selectedOtherItemKeys
                            )?.toList()
                        }

                        filter = this@FilterList.filter
                        itemsData = this@FilterList.itemsData
                    }
                } else {
                    styledDiv {
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
