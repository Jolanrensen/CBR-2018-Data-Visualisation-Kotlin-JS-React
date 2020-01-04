package libs.reactList

import kotlinext.js.JsObject
import react.RBuilder
import react.RComponent
import react.RElementBuilder
import react.RState
import react.ReactElement
import styled.StyledElementBuilder
import styled.StyledProps

typealias ReactList = RComponent<ReactListProps, RState>

@JsModule("react-list")
external val reactList: ReactList

external interface ReactListRef {
    fun scrollTo(index: Int)
    fun scrollAround(index: Int)
    fun getVisibleRange(): IntArray
}

fun RBuilder.styledReactList(
    className: String? = null,
    handler: StyledElementBuilder<ReactListProps>.() -> Unit
) = createStyled(component = reactList) {
    setStyledPropsAndRunHandler(className, handler)
}

fun RBuilder.reactList(handler: RElementBuilder<ReactListProps>.() -> Unit) =
    child(component = reactList, handler = handler)


typealias ItemRenderer = (index: Int, key: String) -> ReactElement?
typealias ItemsRenderer = (items: Array<ReactElement?>?, ref: String?) -> ReactElement?
typealias ItemSizeEstimator = (index: Int, cache: JsObject) -> Int
typealias ItemSizeGetter = (index: Int) -> Int
typealias ScrollParentGetter = () -> ReactElement?

external interface ReactListProps : StyledProps {
    var axis: String? /** "x" or "y", default: "y" */
    var initialIndex: Int?
    var itemRenderer: ItemRenderer?
    var itemSizeEstimator: ItemSizeEstimator?
    var itemSizeGetter: ItemSizeGetter?
    var length: Int? /** default: 0 */
    var minSize: Int? /** default: 1 */
    var pageSize: Int? /** default: 10 */
    var scrollParentGetter: ScrollParentGetter?
    var threshold: Int? /** default: 100 */
    var type: String? /** "simple", "variable", "uniform" or "simple", default: "simple" */
    var useStaticSize: Boolean? /** default: false */
    var useTranslate3d: Boolean? /** default: false */
}

fun ReactListProps.ref(ref: (ReactListRef) -> Unit) {
    asDynamic().ref = ref
}