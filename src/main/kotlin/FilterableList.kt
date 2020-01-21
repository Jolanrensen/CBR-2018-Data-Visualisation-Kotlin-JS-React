
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement

interface FilterableListProps<Key : Any, Type : Any?> : RProps {
    // String filter for current list
    var filter: String

    // Set of keys representing all selected items in this list, don't forget to call onSelectionChanged
    var selectedItemKeys: StateAsProp<Set<Key>>

    // Function that should be called whenever anything changes in selectedItemKeysDelegate
    var onSelectionChanged: () -> Unit

    // Set of keys representing all selected items in other relevant list
    var selectedOtherItemKeys: StateAsProp<Set<Key>>

    // Delegate to data for items in the list
    var itemsData: Map<Key, Type>
}

interface FilterableListState : RState

abstract class FilterableList<Key : Any, Type : Any?, Props : FilterableListProps<Key, Type>, State : FilterableListState>
constructor(prps: Props) : RComponent<Props, State>(prps) {
    abstract fun getFilteredItems(): List<Type>
    abstract fun keyToType(key: Key): Type
    abstract fun typeToKey(type: Type): Key
}

typealias CreateFilterableList<Key, Type> = RBuilder.(handler: FilterableListProps<Key, Type>.() -> Unit) -> ReactElement

