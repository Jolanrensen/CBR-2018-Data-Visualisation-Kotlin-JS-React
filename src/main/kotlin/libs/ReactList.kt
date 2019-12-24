package libs

import com.ccfraser.muirwik.components.child
import com.ccfraser.muirwik.components.createStyled
import kotlinext.js.JsObject
import react.*
import styled.StyledElementBuilder
import com.ccfraser.muirwik.components.setStyledPropsAndRunHandler
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
    var axis: String? /** 'x' or 'y' */
    var initialIndex: Int?
    var itemRenderer: ItemRenderer?
    var itemSizeEstimator: ItemSizeEstimator?
    var itemSizeGetter: ItemSizeGetter?
    var length: Int?
    var minSize: Int?
    var pageSize: Int?
    var scrollParentGetter: ScrollParentGetter?
    var threshold: Int?
    var type: String?
    var useStaticSize: Boolean?
    var useTranslate3d: Boolean?
}

fun ReactListProps.ref(ref: (ReactListRef) -> Unit) {
    asDynamic().ref = ref
}