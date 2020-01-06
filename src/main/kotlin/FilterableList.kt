
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement

abstract class FilterableList<Key: Any, Type: Any?, Props : FilterableList.FilterableListProps<Key, Type>, State : FilterableList.FilterableListState>(props: Props) :
    RComponent<Props, State>(props) {

    interface FilterableListProps<Key: Any, Type: Any?> : RProps {
        // String filter for current list
        var filter: String

        // To be set in State.init, pass a reference to function that reloads the list based on filter
        // var setReloadRef: (ReloadItems) -> Unit

        // Set of keys representing all selected items in this list, don't forget to call onSelectionChanged
        var selectedItemKeysDelegate: VarDelegate<Set<Key>>

        // Function that should be called whenever anything changes in selectedItemKeysDelegate
        var onSelectionChanged: () -> Unit

        // Set of keys representing all selected items in other relevant list
        var selectedOtherItemKeysDelegate: VarDelegate<Set<Key>>

        var itemsDataDelegate: ValDelegate<Map<Key, Type>>

        // List representing all available items in list after filter is applied
        // var filteredItemsDelegate: VarDelegate<List<Type>>
        // var getFilteredItems: () -> List<Type>
        // var setFilteredItems: (List<Type>) -> Unit

        // Function that should be implemented to convert a filterableList's key to type
        // var setKeyToTypeRef: ((Key) -> Type) -> Unit
        // var setTypeToKeyRef: ((Type) -> Key) -> Unit
    }

    interface FilterableListState : RState

    abstract fun getFilteredItems(): List<Type>
    abstract fun keyToType(key: Key): Type
    abstract fun typeToKey(type: Type): Key
}

typealias CreateFilterableList<Key, Type> = RBuilder.(handler: FilterableList.FilterableListProps<Key, Type>.() -> Unit) -> ReactElement

typealias ReloadItems = () -> Unit