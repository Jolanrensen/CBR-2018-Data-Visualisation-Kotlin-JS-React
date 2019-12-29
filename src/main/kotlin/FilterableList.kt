
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement

abstract class FilterableList<T : FilterableList.FilterableListProps, U : FilterableList.FilterableListState<*>>(props: T) :
    RComponent<T, U>(props) {

    interface FilterableListProps : RProps {
        // String filter for current list
        var filter: String

        // To be set in State.init, pass a reference to function that reloads the list based on filter
        var setReloadRef: (ReloadItems) -> Unit

        // Set of keys representing all selected items in this list, don't forget to call onSelectionChanged
        var selectedItemKeys: HashSet<String>

        // Function that should be called whenever anything changes in selectedItemKeysDelegate
        var onSelectionChanged: () -> Unit

        // Set of keys representing all selected items in other relevant list
        var selectedOtherItemKeys: HashSet<String>
    }

    interface FilterableListState<S: Any?> : RState {
        // List representing all available items in list after filter is applied
        var filteredItems: List<S>
    }
}

typealias CreateFilterableList = RBuilder.(handler: FilterableList.FilterableListProps.() -> Unit) -> ReactElement

typealias ReloadItems = () -> Unit