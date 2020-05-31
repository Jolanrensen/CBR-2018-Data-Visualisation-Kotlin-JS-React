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
import io.data2viz.format.Type
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

interface FilterListProps<Key : Any, Type : Any?, ThirdKey : Any?> : RProps {
    var filterableListCreationFunction: CreateFilterableList<Key, Type, ThirdKey>
    var liveReload: Boolean
    var itemsName: String
    var selectedItemKeys: StateAsProp<Set<Key>>
    var selectedOtherItemKeys: StateAsProp<Set<Key>>
    var selectedThirdItemKeys: StateAsProp<Set<ThirdKey>>
    var alwaysAllowSelectAll: Boolean
    var dataLoaded: Boolean
    var itemsData: Map<Key, Type>
    var setApplyFilterFunction: (ApplyFilter) -> Unit
    var setSelectAllFunction: (SelectAll<Type>) -> Unit
    var setDeselectAllFunction: (DeselectAll) -> Unit
    var onFilteredItemsChanged: (List<Type>?) -> Unit
    var onCategorieClicked: (Categorie) -> Unit
}

typealias ApplyFilter = (String) -> Unit
typealias SelectAll<Type> = (condition: (item: Type) -> Boolean) -> Unit
typealias DeselectAll = () -> Unit

interface FilterListState : RState {
    var filter: String
//    var filterFieldValue: String
}

class FilterList<Key : Any, Type : Any?, ThirdKey : Any?>(prps: FilterListProps<Key, Type, ThirdKey>) :
    RComponent<FilterListProps<Key, Type, ThirdKey>, FilterListState>(prps) {

    private var selectedItemKeys by propDelegateOf(FilterListProps<Key, Type, ThirdKey>::selectedItemKeys)
    private val selectedOtherItemKeys by propDelegateOf(FilterListProps<Key, Type, ThirdKey>::selectedOtherItemKeys)
    private val selectedThirdItemKeys by propDelegateOf(FilterListProps<Key, Type, ThirdKey>::selectedThirdItemKeys)

    private val dataLoaded by propDelegateOf(FilterListProps<Key, Type, ThirdKey>::dataLoaded)
    private val itemsData by propDelegateOf(FilterListProps<Key, Type, ThirdKey>::itemsData)

    private val onFilteredItemsChanged by propDelegateOf(FilterListProps<Key, Type, ThirdKey>::onFilteredItemsChanged)

    override fun FilterListState.init(props: FilterListProps<Key, Type, ThirdKey>) {
        filter = ""
//        filterFieldValue = ""
    }

    @Suppress("SimplifyBooleanWithConstants")
    override fun shouldComponentUpdate(
        nextProps: FilterListProps<Key, Type, ThirdKey>,
        nextState: FilterListState
    ) = false
            || props.selectedItemKeys != nextProps.selectedItemKeys
            || props.selectedOtherItemKeys != nextProps.selectedOtherItemKeys
            || props.selectedThirdItemKeys != nextProps.selectedThirdItemKeys
            || props.dataLoaded != nextProps.dataLoaded
            || state.filter != nextState.filter
//            || state.filterFieldValue != nextState.filterFieldValue

    override fun componentDidMount() {
        props.setApplyFilterFunction(applyFilter)
        props.setSelectAllFunction(selectAll)
        props.setDeselectAllFunction(deselectAll)
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

    private val selectAll: SelectAll<Type> = { condition ->
        println("select all called!")
        val new = getFilteredItems(filter, itemsData, selectedItemKeys, selectedOtherItemKeys, selectedThirdItemKeys)!!
            .filter { condition(it) }
            .map { typeToKey(it)!! }
            .toSet()

        if (new.any { it !in selectedItemKeys } || selectedItemKeys.any { it !in new })
            selectedItemKeys = new
    }

    private val deselectAll: DeselectAll = {
        println("deselect all called!")
        if (selectedItemKeys.isNotEmpty()) {
            selectedItemKeys = setOf()
        }
    }

    private var filter
        get() = state.filter
        set(value) {
            if (value == state.filter) return
            setState {
                filter = value
            }

            // deselect all previously selected items that are no longer in filteredItems
            getFilteredItems(value, itemsData, selectedItemKeys, selectedOtherItemKeys, selectedThirdItemKeys)
        }

    private var filterFieldValue: String = ""// by stateDelegateOf(FilterListState::filterFieldValue)

    private var filterableList: FilterableList<Key, Type, ThirdKey, *, *>? = null

    private var previousFilteredItems: List<Type>? = null

    private fun getFilteredItems(
        filter: String,
        itemsData: Map<Key, Type>,
        selectedItemKeys: Set<Key>,
        selectedOtherItemKeys: Set<Key>,
        selectedThirdItemKeys: Set<ThirdKey>
    ): List<Type>? {

        val filteredItems = filterableList?.getFilteredItems(
            filter,
            itemsData,
            selectedItemKeys,
            selectedOtherItemKeys,
            selectedThirdItemKeys
        )

        // deselect all previously selected items that are no longer in filteredItems
        if (filteredItems != null) {
            selectedItemKeys.filter {
                keyToType(it) !in filteredItems
            }.let {
                this.selectedItemKeys -= it
            }
        }

        when {
            filteredItems == null && previousFilteredItems != null -> {
                previousFilteredItems = null
                onFilteredItemsChanged(null)
            }
            filteredItems != null && previousFilteredItems == null -> {
                previousFilteredItems = filteredItems
                onFilteredItemsChanged(filteredItems)
            }
            filteredItems != null && previousFilteredItems != null ->
                if (
                    filteredItems.size != previousFilteredItems!!.size
                    || !filteredItems.containsAll(previousFilteredItems!!)
                    || !previousFilteredItems!!.containsAll(filteredItems)
                ) {
                    previousFilteredItems = filteredItems
                    onFilteredItemsChanged(filteredItems)
                }
        }

        return filteredItems
    }

    private fun sortType(type: Type) = filterableList?.sortType(type)
    private fun typeToKey(type: Type) = filterableList?.typeToKey(type, itemsData)
    private fun keyToType(key: Key) = filterableList?.keyToType(key, itemsData)

    private val toggleSelectAllVisible: (Event?) -> Unit = {
        val filteredItems =
            getFilteredItems(filter, itemsData, selectedItemKeys, selectedOtherItemKeys, selectedThirdItemKeys)!!
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
//        filter = it.targetInputValue
        if (it.targetInputValue != filterFieldValue)
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
                            shrink = true
//                            shrink = filterFieldValue.isNotEmpty()
//                            shrink = filter.isNotEmpty()
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
                        val filteredItems = getFilteredItems(
                            filter,
                            itemsData,
                            selectedItemKeys,
                            selectedOtherItemKeys,
                            selectedThirdItemKeys
                        )

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
                        selectedThirdItemKeys = props.selectedThirdItemKeys
                        onCategorieClicked = props.onCategorieClicked

                        ref<FilterableList<Key, Type, ThirdKey, *, *>> {
                            filterableList = it
                        }

                        filteredItems = this@FilterList.run {
                            getFilteredItems(
                                filter,
                                itemsData,
                                selectedItemKeys,
                                selectedOtherItemKeys,
                                selectedThirdItemKeys
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

fun <Key : Any, Type : Any?, ThirdKey : Any?> RBuilder.filterList(
    type: CreateFilterableList<Key, Type, ThirdKey>,
    itemsName: String = "items",
    handler: FilterListProps<Key, Type, ThirdKey>.() -> Unit
) =
    child<FilterListProps<Key, Type, ThirdKey>, FilterList<Key, Type, ThirdKey>> {
        attrs {
            filterableListCreationFunction = type
            liveReload = true
            this.itemsName = itemsName
            alwaysAllowSelectAll = false
        }
        attrs(handler)
    }
