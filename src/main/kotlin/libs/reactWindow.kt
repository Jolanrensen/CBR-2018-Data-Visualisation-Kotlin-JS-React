@file:JsModule("react-window")

package libs

import data.Opleider
import react.*


@JsName("FixedSizeList")
external val FixedSizeListOpleider : RClass<ListProps<List<Opleider>>>

external enum class Direction {
    vertical, horizontal
}

external enum class Alignment {
    auto, center, end, start
}

external interface RenderProps<TDataType : Any> : RProps {
    var key: String?
    var data: TDataType?
    var index: Int
    var isScrolling: Boolean
    var style: dynamic // Css shit
}

external interface ItemsRenderedProps : RProps {
    var overscanStartIndex: Int
    var ovserscanStopIndex: Int
    var visibleStartIndex: Int
    var visibleStopIndex: Int
}

external interface OnScrollProps : RProps {
    var scrollDirection: Direction
    var scrollOffset: Number
    var scrollUpdateWasRequested: Boolean
}

external interface ListProps<TDataType: Any> : RConsumerProps<RenderProps<TDataType>> {
    /**
     * React component responsible for rendering the individual item specified by an index prop. This component also receives a style prop (used for positioning).
     * If useIsScrolling is enabled for the list, the component also receives an additional isScrolling boolean prop.
     */
    //fun children(props: RenderProps<TDataType>): ReactElement
    /**
     * Optional CSS class to attach to outermost <div> element.
     */
    var className: String
    /**
     * Primary scroll direction of the list. Acceptable values are:
     * - vertical (default) - Up/down scrolling.
     * - horizontal - Left/right scrolling.
     * Note that lists may scroll in both directions (depending on CSS) but content will only be windowed in the primary direction.
     */
    var direction: Direction
    /**
     * Height of the list.
     * For vertical lists, this must be a number. It affects the number of rows that will be rendered (and displayed) at any given time.
     * For horizontal lists, this can be a number or a string (e.g. "50%").
     */
    var height: Int
    /**
     * Scroll offset for initial render.
     * For vertical lists, this affects scrollTop. For horizontal lists, this affects scrollLeft.
     */
    var initialScrollOffset: Number?
    /**
     * Ref to attach to the inner container element. This is an advanced property.
     */
//    var innerRef?: React.Ref<HTMLElement> | React.RefObject<HTMLElement>;
    /**
     * Tag name passed to document.createElement to create the inner container element. This is an advanced property; in most cases, the default ("div") should be used.
     */
//    innerTag?: string;
    /**
     * Total number of items in the list. Note that only a few items will be rendered and displayed at a time.
     */
    var itemCount: Int
    /**
     * Contextual data to be passed to the item renderer as a data prop. This is a light-weight alternative to React's built-in context API.
     */
    var itemData: TDataType?
    /**
     * By default, lists will use an item's index as its key. This is okay if:
     *   - Your collections of items is never sorted or modified
     *   - Your item renderer is not stateful and does not extend PureComponent
     * If your list does not satisfy the above constraints, use the itemKey property to specify your own keys for items:
     * @param {number} index
     */
//    itemKey?(index: number): string;
    /**
     * Size of a item in the direction being windowed. For vertical lists, this is the row height. For horizontal lists, this is the column width.
     */
    var itemSize: Number
    var onItemsRendered: ((o: ItemsRenderedProps) -> Unit)?
    var onScroll: ((o: OnScrollProps) -> Unit)?
    var outerTagName: String?
    var overscanCount: Int?
    var style: dynamic
    var useIsScrolling: Boolean?
    var width: Int
}