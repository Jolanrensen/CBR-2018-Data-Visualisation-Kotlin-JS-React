import libs.RPureComponent
import react.RBuilder
import react.RProps
import react.RState
import react.ReactElement

interface FilterableListProps<Key : Any, Type : Any?> : RProps {
    // List of filtered items
    var filteredItems: List<Type>?

    // Set of keys representing all selected items in this list, don't forget to call onSelectionChanged
    var selectedItemKeys: StateAsProp<Set<Key>>

    // Set of keys representing all selected items in other relevant list
    var selectedOtherItemKeys: StateAsProp<Set<Key>>

    var filter: String
    var itemsData: Map<Key, Type>
}

interface FilterableListState : RState {
    var popoverOpen: Boolean
}

abstract class FilterableList<Key : Any, Type : Any?, Props : FilterableListProps<Key, Type>, State : FilterableListState>(
    prps: Props
) : RPureComponent<Props, State>(prps) {
    abstract fun sortType(type: Type): Double

    abstract fun getFilteredItems(
        filter: String,
        itemsData: Map<Key, Type>,
        selectedItemKeys: Set<Key>,
        selectedOtherItemKeys: Set<Key>
    ): List<Type>

    abstract fun keyToType(key: Key, itemsData: Map<Key, Type>): Type
    abstract fun typeToKey(type: Type, itemsData: Map<Key, Type>): Key

}
typealias CreateFilterableList<Key, Type> = RBuilder.(handler: FilterableListProps<Key, Type>.() -> Unit) -> ReactElement

